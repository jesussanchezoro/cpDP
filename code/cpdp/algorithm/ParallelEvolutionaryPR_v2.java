package cpdp.algorithm;

import cpdp.combinations.ExteriorPathRelinking;
import cpdp.combinations.RandomPathRelinking;
import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.RandomManager;
import grafo.optilib.tools.Timer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class ParallelEvolutionaryPR_v2 implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private RandomPathRelinking ipr;
    private ExteriorPathRelinking epr;
    private int refsetSize;
    private int popSize;
    private int numberOfIters;
    private ExecutorService pool;

    private CPDPSolution best;

    public ParallelEvolutionaryPR_v2(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int refsetSize, int popSize) {
        this.c = c;
        this.ls = ls;
        this.refsetSize = refsetSize;
        this.popSize = popSize;
        this.ipr = new RandomPathRelinking(ls);
        this.epr = new ExteriorPathRelinking(ls);
        this.numberOfIters = 1;
    }

    public ParallelEvolutionaryPR_v2(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int refsetSize, int popSize, int numberOfIters) {
        this(c, ls, refsetSize, popSize);
        this.numberOfIters = numberOfIters;
    }


    private void dynamicPR(CPDPInstance instance, Set<CPDPSolution> refset) {
        List<CPDPSolution> refsetList = new ArrayList<>(refset);
        List<Future<CPDPSolution>> futSols = new ArrayList<>(popSize-refsetSize);
        for (int i = 0; i < popSize-refsetSize; i++) {
            pool.submit(() -> {
                CPDPSolution sol = c.constructSolution(instance);
                ls.improve(sol);
                checkBest(sol);
                CPDPSolution rfSol = refsetList.get(RandomManager.getRandom().nextInt(refsetList.size()));
                CPDPSolution combined = null;
                if (rfSol.getOf() == sol.getOf()) {
                    combined = epr.combine(sol, rfSol);
                } else {
                    combined = ipr.combine(sol, rfSol);
                }
                if (combined != null) {
                    ls.improve(combined);
                    checkBest(combined);
                }
                return combined;
            });
        }
        for (Future<CPDPSolution> futSol : futSols) {
            CPDPSolution combined = null;
            try {
                combined = futSol.get();
                if (combined != null) {
                    addRefset(refset, combined);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean evolve(Set<CPDPSolution> refset) {
        List<CPDPSolution> newSols = new ArrayList<>(refset);
        List<CPDPSolution> nextSols = new ArrayList<>(refset.size());
        boolean anyImprove = false;
        boolean improved = true;
        List<Future<CPDPSolution>> futCandidate = new ArrayList<>(newSols.size()*refset.size());
        while (improved) {
            improved = false;
            Set<CPDPSolution> candidateSols = new HashSet<>();
            for (CPDPSolution newSol : newSols) {
                for (CPDPSolution refsetSol : refset) {
                    if (!newSol.equals(refsetSol)) {
                        futCandidate.add(pool.submit(() -> {
                            CPDPSolution combined = null;
                            if (newSol.getOf() == refsetSol.getOf()) {
                                combined = epr.combine(newSol, refsetSol);
                            } else {
                                combined = ipr.combine(newSol, refsetSol);
                            }
                            return combined;
                        }));
                    }
                }
            }
            for (Future<CPDPSolution> candidate : futCandidate) {
                CPDPSolution combined = null;
                try {
                    combined = candidate.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (combined != null) {
                    candidateSols.add(combined);
                }
            }

            nextSols.clear();
            for (CPDPSolution candidateSol : candidateSols) {
                improved = addRefset(refset, candidateSol);
                if (improved) {
                    nextSols.add(candidateSol);
                    anyImprove = true;
                }
                checkBest(candidateSol);
            }
            newSols = nextSols;
        }
        return anyImprove;
    }

    @Override
    public Result execute(CPDPInstance instance) {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

        best = null;
        Result r = new Result(instance.getName());
        System.out.print(instance.getName()+"\t"+instance.getP()+"\t"+instance.getQ()+"\t");

        Timer.initTimer();

        for (int iter = 0; iter < numberOfIters; iter++) {
            List<Future<CPDPSolution>> sols = new ArrayList<>(refsetSize);
            Set<CPDPSolution> refset = new HashSet<>();
            // Initial Refset
            for (int i = 0; i < refsetSize; i++) {
                sols.add(pool.submit(() -> {
                    CPDPSolution sol = c.constructSolution(instance);
                    ls.improve(sol);
                    return sol;
                }));
            }
            for (Future<CPDPSolution> fut : sols) {
                try {
                    CPDPSolution sol = fut.get();
                    refset.add(sol);
                    checkBest(sol);
                } catch (Exception e) {
                    System.out.println("ERROR EN LA GENERACION DE SOLUCION");
                }
            }
            System.out.println(best.getOf());
            boolean anyImprove = true;
            while (anyImprove) {
                // Dynamic Path Relinking
                dynamicPR(instance, refset);
                // Evolutionary
                anyImprove = evolve(refset);
            }
        }

        int bestOf = best == null ? -1 : best.getOf();
        double secs = Timer.getTime()/1000.0;
        r.add("p", instance.getP());
        r.add("q", instance.getQ());
        r.add("OF", bestOf);
        r.add("Time (s)", secs);
        System.out.println(bestOf+"\t"+secs);

        // Show points
        int n = instance.getN();
        float[][] coords = instance.getCoords();
        System.out.println("Originales");
        for (int i = instance.getR(); i < n; i++) {
            System.out.println(coords[i][0]+" "+coords[i][1]);
        }
        System.out.println("Elegidos");
        for (int s : best.getSelected()) {
            System.out.println(coords[s][0]+" "+coords[s][1]);
        }

        pool.shutdown();
        try {
            if (!pool.awaitTermination(1, TimeUnit.DAYS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return r;
    }

    private boolean addRefset(Set<CPDPSolution> refset, CPDPSolution sol) {
        if (refset.contains(sol)) {
            return false;
        }
        CPDPSolution closestWorse = null;
        int minDist = 0x3f3f3f;
        boolean improve = false;
        for (CPDPSolution refSol : refset) {
            if (sol.getOf() > refSol.getOf()) {
                int dist = sol.distance(refSol);
                if (dist < minDist) {
                    closestWorse = refSol;
                    minDist = dist;
                    improve = true;
                }
            }
        }
        if (improve) {
            closestWorse.copy(sol);
        }
        return improve;
    }

    private synchronized boolean checkBest(CPDPSolution sol) {
        if (best == null) {
            best = new CPDPSolution(sol);
            System.out.println("\tNEW BEST: "+best.getOf());
            return true;
        } else if (sol.getOf() > best.getOf()) {
            best.copy(sol);
            System.out.println("\tNEW BEST: "+best.getOf());
            return true;
        }
        return false;
    }

    @Override
    public CPDPSolution getBestSolution() {
        return best;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"(" + c +", " + ls + ", " + refsetSize +", " + popSize + ")";
    }
}
