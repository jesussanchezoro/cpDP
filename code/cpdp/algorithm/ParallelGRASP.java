package cpdp.algorithm;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.tools.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ParallelGRASP implements Algorithm<CPDPInstance, CPDPSolution> {

    private Constructive<CPDPInstance, CPDPSolution> c;
    private Improvement<CPDPSolution> ls;
    private int iters;
    private CPDPSolution best;

    public ParallelGRASP(Constructive<CPDPInstance, CPDPSolution> c, Improvement<CPDPSolution> ls, int iters) {
        this.c = c;
        this.ls = ls;
        this.iters = iters;
    }

    @Override
    public Result execute(CPDPInstance instance) {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        best = null;
        System.out.print(instance.getName()+"\t");
        Timer.initTimer();
        List<Future<CPDPSolution>> sols = new ArrayList<>(iters);
        for (int i = 0; i < iters; i++) {
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
                if (best == null || sol.getOf() > best.getOf()) {
                    best = sol;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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

    @Override
    public CPDPSolution getBestSolution() {
        return best;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+c+","+ls+","+iters+")";
    }
}
