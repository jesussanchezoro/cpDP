package cpdp.improvement;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Improvement;

import java.awt.event.WindowStateListener;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class LS1x1Constraint implements Improvement<CPDPSolution> {

    @Override
    public void improve(CPDPSolution sol) {
        boolean improve = true;
        while (improve) {
            improve = tryImprove(sol);
        }

    }

    private boolean tryImprove(CPDPSolution sol) {
        int[] critical = sol.getCritical().clone();
        int oldOF = sol.getOf();
        int r = sol.getInstance().getR();
        for (int crit : critical) {
            if (crit >= r) continue; // Fixed node selected by the instance
            sol.remove(crit);
            int furthest = findFurthestNotSelected(sol);
            sol.add(furthest);
            if (sol.getOf() > oldOF) {
                return true;
            }
            sol.remove(furthest);
            sol.add(crit);
        }
        return false;
    }

    private int findFurthestNotSelected(CPDPSolution sol) {
        int r = sol.getInstance().getR();
        int furthest = -1;
        int maxDist = 0;
        for (int u = 0; u < r; u++) {
            if (sol.getSelected().contains(u)) continue;
            int d = sol.minDist(u);
            if (d > maxDist) {
                maxDist = d;
                furthest = u;
            }
        }
        return furthest;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
