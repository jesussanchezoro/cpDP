package cpdp.improvement;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Improvement;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class LS1x1 implements Improvement<CPDPSolution> {

    private class Candidate {
        int v;
        int c;

        public Candidate(int v, int c) {
            this.v = v;
            this.c = c;
        }
    }

    @Override
    public void improve(CPDPSolution sol) {
        boolean improve = true;
        while (improve) {
            improve = tryImprove(sol);
        }

    }

    private boolean tryImprove(CPDPSolution sol) {
        PriorityQueue<Candidate> clOut = createCLOut(sol);
        while (!clOut.isEmpty()) {
            int ofBefore = sol.getOf();
            Candidate cOut = clOut.remove();
            sol.remove(cOut.v);
            PriorityQueue<Candidate> clIn = createCLIn(sol);
            while (!clIn.isEmpty()) {
                Candidate cIn = clIn.remove();
                sol.add(cIn.v);
                if (sol.getOf() > ofBefore) {
                    return true;
                }
                sol.remove(cIn.v);
            }
            sol.add(cOut.v);
        }
        return false;
    }

    private PriorityQueue<Candidate> createCLIn(CPDPSolution sol) {
        int r = sol.getInstance().getR();
        PriorityQueue<Candidate> clIn = new PriorityQueue<>(r, Comparator.comparingInt(c -> -c.c));
        Set<Integer> selected = new HashSet<>(sol.getSelected());
        for (int u = 0; u < r; u++) {
            if (selected.contains(u)) continue;
            int d = sol.minDist(u);
            clIn.add(new Candidate(u, d));
        }
        return clIn;
    }

    private PriorityQueue<Candidate> createCLOut(CPDPSolution sol) {
        int p = sol.getInstance().getP();
        CPDPInstance instance = sol.getInstance();
        PriorityQueue<Candidate> clOut = new PriorityQueue<>(p, Comparator.comparingInt(c -> c.c));
        Set<Integer> selected = sol.getSelected();
        for (int s : selected) {
            int d = instance.distance(s, sol.getClosest(s));
            clOut.add(new Candidate(s, d));
        }
        return clOut;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
