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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class EvolutionaryPR_v2 implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private RandomPathRelinking ipr;
    private ExteriorPathRelinking epr;
    private int refsetSize;
    private int popSize;
    private int itersNonImprove;

    private CPDPSolution best;

    public EvolutionaryPR_v2(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int refsetSize, int popSize, int itersNonImprove) {
        this.c = c;
        this.ls = ls;
        this.refsetSize = refsetSize;
        this.popSize = popSize;
        this.ipr = new RandomPathRelinking();
        this.epr = new ExteriorPathRelinking();
        this.itersNonImprove = itersNonImprove;
    }

    private void dynamicPR(CPDPInstance instance, Set<CPDPSolution> refset) {
        List<CPDPSolution> refsetList = new ArrayList<>(refset);
        for (int i = 0; i < popSize-refsetSize; i++) {
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
                addRefset(refset, combined);
            }
        }
    }

    private boolean evolve(Set<CPDPSolution> refset) {
        List<CPDPSolution> newSols = new ArrayList<>(refset);
        List<CPDPSolution> nextSols = new ArrayList<>(refset.size());
        boolean anyImprove = false;
        boolean improved = true;
        while (improved) {
            improved = false;
            Set<CPDPSolution> candidateSols = new HashSet<>();
            for (CPDPSolution newSol : newSols) {
                for (CPDPSolution refsetSol : refset) {
                    if (!newSol.equals(refsetSol)) {
                        CPDPSolution combined = null;
                        if (newSol.getOf() == refsetSol.getOf()) {
                            combined = epr.combine(newSol, refsetSol);
                        } else {
                            combined = ipr.combine(newSol, refsetSol);
                        }
                        if (combined != null) {
                            candidateSols.add(combined);
                        }
                    }
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
        best = null;
        Result r = new Result(instance.getName());
        System.out.print(instance.getName()+"\t"+instance.getP()+"\t"+instance.getQ()+"\t");
        Timer.initTimer();
        Set<CPDPSolution> refset = new HashSet<>();
        int maxIters = refsetSize*10;
        int iters = 0;
        // Initial Refset
        while (refset.size() < refsetSize && iters < maxIters) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            refset.add(sol);
            checkBest(sol);
            iters++;
        }

        for (int i = 0; i < itersNonImprove; i++) {
            boolean anyImprove = true;
            while (anyImprove) {
                // Dynamic Path Relinking
                dynamicPR(instance, refset);
                // Evolutionary
                anyImprove = evolve(refset);
            }
            r.add("I"+(i+1), best.getOf());
            System.out.println("\tI"+(i+1)+"\t"+best.getOf());
            if (i < itersNonImprove-1) {
                resetRefset(refset);
            }
        }



        double secs = Timer.getTime()/1000.0;
        r.add("p", instance.getP());
        r.add("q", instance.getQ());
        r.add("OF", best.getOf());
        r.add("Time (s)", secs);
        System.out.println(best.getOf()+"\t"+secs);
        return r;
    }

    private void resetRefset(Set<CPDPSolution> refset) {
        CPDPInstance instance = best.getInstance();
        refset.clear();
        refset.add(new CPDPSolution(best));
        int maxIters = refsetSize*10;
        int iters = 0;
        while (refset.size() < refsetSize && iters < maxIters) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            refset.add(sol);
            checkBest(sol);
            iters++;
        }
    }

    private boolean addRefset(Set<CPDPSolution> refset, CPDPSolution sol) {
        if (refset.contains(sol)) {
            return false;
        }
        CPDPSolution closestWorse = null;
        int minDist = 0x3f3f3f;
        boolean improve = false;
        for (CPDPSolution refSol : refset) {
//            if (sol.getOf() == refSol.getOf()) {
//                return false;
//            } else
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

    private boolean checkBest(CPDPSolution sol) {
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
        return this.getClass().getSimpleName()+"(" + c +", " + ls + ", " + refsetSize +", " + popSize + "," + itersNonImprove + ")";
    }
}
