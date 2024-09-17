package cpdp.constructives;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.structure.Instance;
import grafo.optilib.structure.Solution;
import grafo.optilib.tools.RandomManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConstGRASP implements Constructive<CPDPInstance, CPDPSolution> {

    public static class Candidate {
        int v;
        int c;

        public Candidate(int v, int c) {
            this.v = v;
            this.c = c;
        }

        @Override
        public String toString() {
            return "("+this.v + ", " + this.c+")";
        }
    }

    private final float alpha;

    public ConstGRASP(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public CPDPSolution constructSolution(CPDPInstance instance) {
        Random rnd = RandomManager.getRandom();
        CPDPSolution sol = new CPDPSolution(instance);
        int r = instance.getR();
        int p = instance.getP();
        List<Candidate> cl = new ArrayList<>(r);
        int[] rcl = new int[r];
        int gmax = 0;
        int gmin = 0x3f3f3f;
        for (int i = 0; i < r; i++) {
            int d = sol.minDist(i);
            gmax = Math.max(d, gmax);
            gmin = Math.min(d, gmin);
            cl.add(new Candidate(i, d));
        }
        float realAlpha = (alpha < 0) ? rnd.nextFloat() : alpha;
        while (sol.size() < p) {
            float limit = gmax - realAlpha * (gmax - gmin);
            int lastInRCL = createRCL(cl, rcl, limit);
            int next = rcl[rnd.nextInt(lastInRCL)];
            Candidate removed = cl.remove(next);
            sol.add(removed.v);
            gmax = 0;
            gmin = 0x3f3f3f;
            for (Candidate c : cl) {
                int d = instance.distance(c.v, removed.v);
                c.c = Math.min(c.c, d);
                gmax = Math.max(c.c, gmax);
                gmin = Math.min(c.c, gmin);
            }
        }
        return sol;
    }

    private int createRCL(List<Candidate> cl, int[] rcl, float limit) {
        int lastInRCL = 0;
        for (int i = 0; i < cl.size(); i++) {
            Candidate c = cl.get(i);
            if (Float.compare(c.c, limit) >= 0) {
                rcl[lastInRCL] = i;
                lastInRCL++;
            }
        }
        return lastInRCL;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+alpha+")";
    }
}
