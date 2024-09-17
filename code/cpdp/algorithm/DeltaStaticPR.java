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

public class DeltaStaticPR implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private RandomPathRelinking ipr;
    private ExteriorPathRelinking epr;
    private int populationSize;
    private int refsetSize;

    private CPDPSolution best;

    public DeltaStaticPR(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int populationSize, int refsetSize) {
        this.c = c;
        this.ls = ls;
        this.populationSize = populationSize;
        this.refsetSize = refsetSize;
        this.ipr = new RandomPathRelinking();
        this.epr = new ExteriorPathRelinking();
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
        best = null;
        Result r = new Result(instance.getName());
        System.out.print(instance.getName()+"\t"+instance.getP()+"\t"+instance.getQ()+"\t");
        Timer.initTimer();
        Set<CPDPSolution> population = new HashSet<>();
        int maxIters = populationSize*10;
        int iters = 0;
        while (population.size() < populationSize && iters < maxIters) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            population.add(sol);
            checkBest(sol);
            iters++;
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
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                CPDPSolution s1 = refSet.get(i);
                CPDPSolution s2 = refSet.get(j);
                CPDPSolution combined = null;
                if (s1.getOf() == s2.getOf()) {
                    combined = epr.combine(s1, s2);
                } else {
                    combined = ipr.combine(s1, s2);
                }
                if (combined != null) {
                    ls.improve(combined);
                    checkBest(combined);
                }
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
        return this.getClass().getSimpleName()+"(" + c +", " + ls + ", " + populationSize + "," + refsetSize + ")";
    }
}
