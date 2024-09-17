package cpdp.algorithm;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.Timer;

public class GRASP implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private int iters;
    private CPDPSolution best;

    public GRASP(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
    }

    @Override
    public Result execute(CPDPInstance instance) {
        best = null;
        System.out.print(instance.getName()+"\t");
        Timer.initTimer();
        for (int i = 0; i < iters; i++) {
            CPDPSolution sol = c.constructSolution(instance);
            ls.improve(sol);
            if (best == null || sol.getOf() > best.getOf()) {
                best = sol;
            }
        }
        double secs = Timer.getTime() / 1000.0f;
        String secsSt = String.format("%.4f", secs);
        System.out.println(instance.getP()+"\t"+instance.getQ()+"\t"+best.getOf()+"\t"+best.eval()+"\t"+secsSt);
        Result r = new Result(instance.getName());
        r.add("p", instance.getP());
        r.add("q", instance.getQ());
        r.add("Time (s)", secs);
        r.add("OF", best.getOf());
        return r;
    }

    @Override
    public CPDPSolution getBestSolution() {
        return best;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+c+","+ls+","+iters+")";
    }
}
