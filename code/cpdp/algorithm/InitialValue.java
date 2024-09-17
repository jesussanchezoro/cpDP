package cpdp.algorithm;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.Timer;

public class InitialValue implements Algorithm<CPDPInstance, CPDPSolution> {

    private CPDPSolution best;

    @Override
    public Result execute(CPDPInstance instance) {
        System.out.print(instance.getName()+"\t");
        Timer.initTimer();
        best = new CPDPSolution(instance);
        double secs = Timer.getTime() / 1000.0f;
        String secsSt = String.format("%.4f", secs);
        System.out.println(best.getOf()+"\t"+best.eval()+"\t"+secsSt);
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
        return this.getClass().getSimpleName();
    }
}
