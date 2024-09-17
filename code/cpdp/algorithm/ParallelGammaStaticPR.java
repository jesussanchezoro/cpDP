package cpdp.algorithm;

import cpdp.combinations.ExteriorPathRelinking;
import cpdp.combinations.RandomPathRelinking;
import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.Timer;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelGammaStaticPR implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private RandomPathRelinking ipr;
    private ExteriorPathRelinking epr;
    private int populationSize;
    private int refsetSize;
    private float prTypeThreshold;

    private CPDPSolution best;

    public ParallelGammaStaticPR(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int populationSize, int refsetSize, float prTypeThreshold) {
        this.c = c;
        this.ls = ls;
        this.populationSize = populationSize;
        this.refsetSize = refsetSize;
        this.ipr = new RandomPathRelinking();
        this.epr = new ExteriorPathRelinking();
        this.prTypeThreshold = prTypeThreshold;
    }

    private int distanceToRefSet(List<CPDPSolution> refset, CPDPSolution sol) {
        int minDist = 0x3f3f3f3f;
        for (CPDPSolution refsetSol : refset) {
            int d = sol.distance(refsetSol);
            minDist = Math.min(d, minDist);
        }
        return minDist;
    }

    @Override
    public Result execute(CPDPInstance instance) {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        List<Future<CPDPSolution>> sols = new ArrayList<>(populationSize);
        best = null;
        Result r = new Result(instance.getName());
        System.out.print(instance.getName()+"\t"+instance.getP()+"\t"+instance.getQ()+"\t");
        Timer.initTimer();
        Set<CPDPSolution> population = new HashSet<>();
        for (int i = 0; i < populationSize; i++) {
            sols.add(pool.submit(() -> {
                CPDPSolution sol = c.constructSolution(instance);
                ls.improve(sol);
                return sol;
            }));
        }
        for (Future<CPDPSolution> fut : sols) {
            CPDPSolution sol = null;
            try {
                sol = fut.get();
                population.add(sol);
                checkBest(sol);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        List<CPDPSolution> populationList = new ArrayList<>(population);
        populationList.sort(Comparator.comparingInt(CPDPSolution::getOf));
        List<CPDPSolution> refSet = new ArrayList<>(refsetSize);
        // Best solutions
        for (int i = 0; i < refsetSize/2; i++) {
            refSet.add(populationList.remove(populationList.size()-1));
        }
        // Most diverse solutions
        while (refSet.size() < refsetSize && !populationList.isEmpty()) {
            int selected = -1;
            int maxDist = 0;
            for (int i = 0; i < populationList.size(); i++) {
                int dToRef = distanceToRefSet(refSet, populationList.get(i));
                if (dToRef > maxDist) {
                    maxDist = dToRef;
                    selected = i;
                }
            }
            CPDPSolution sol = populationList.remove(selected);
            refSet.add(sol);
        }

        int size = Math.min(refsetSize, refSet.size());
        sols = new ArrayList<>(size*size);
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                CPDPSolution s1 = refSet.get(i);
                CPDPSolution s2 = refSet.get(j);
                sols.add(pool.submit(() ->{
                    CPDPSolution combined = null;
                    int d = s1.distance(s2);
                    if (d < prTypeThreshold * instance.getP()) {
                        combined = epr.combine(s1, s2);
                    } else {
                        combined = ipr.combine(s1, s2);
                    }
                    if (combined != null) {
                        ls.improve(combined);
                        checkBest(combined);
                    }
                    return combined;
                }));
            }
        }
        for (Future<CPDPSolution> fut : sols) {
            CPDPSolution sol = null;
            try {
                sol = fut.get();
                if (sol != null) {
                    checkBest(sol);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        double secs = Timer.getTime()/1000.0;
        r.add("p", instance.getP());
        r.add("q", instance.getQ());
        r.add("OF", best.getOf());
        r.add("Time (s)", secs);
        System.out.println(best.getOf()+"\t"+secs);
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

    private boolean checkBest(CPDPSolution sol) {
        if (best == null) {
            best = new CPDPSolution(sol);
            return true;
        } else if (sol.getOf() > best.getOf()) {
            best.copy(sol);
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
        return this.getClass().getSimpleName()+"(" + c +", " + ls + ", " + populationSize + "," + refsetSize + "," + prTypeThreshold + ")";
    }
}
