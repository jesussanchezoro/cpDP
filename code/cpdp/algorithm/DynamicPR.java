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

import java.util.HashSet;
import java.util.Set;

public class DynamicPR implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private RandomPathRelinking ipr;
    private ExteriorPathRelinking epr;
    private int refsetSize;
    private int popSize;

    private CPDPSolution best;

    public DynamicPR(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int refsetSize, int popSize) {
        this.c = c;
        this.ls = ls;
        this.refsetSize = refsetSize;
        this.popSize = popSize;
        this.ipr = new RandomPathRelinking();
        this.epr = new ExteriorPathRelinking();
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
        while (refset.size() < refsetSize && iters < maxIters) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            refset.add(sol);
            checkBest(sol);
            iters++;
        }
        for (int i = 0; i < popSize-refsetSize; i++) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            checkBest(sol);
            CPDPSolution furthest = null;
            CPDPSolution closest = null;
            int maxDist = -1;
            int minDist = 0x3f3f3f;
            for (CPDPSolution rfSol : refset) {
                int dist = sol.distance(rfSol);
                if (dist > maxDist) {
                    maxDist = dist;
                    furthest = rfSol;
                }
                if (dist < minDist) {
                    minDist = dist;
                    closest = rfSol;
                }
            }
            CPDPSolution iprSol = ipr.combine(sol, furthest);
            if (iprSol != null) {
                ls.improve(iprSol);
                checkBest(iprSol);
                addRefset(refset, iprSol);
            }

            CPDPSolution eprSol = epr.combine(sol, closest);
            if (eprSol != null) {
                ls.improve(eprSol);
                checkBest(eprSol);
                addRefset(refset, eprSol);
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
        return this.getClass().getSimpleName()+"(" + c +", " + ls + ", " + refsetSize +", " + popSize + ")";
    }
}