package cpdp.structure;

import grafo.optilib.structure.Solution;

import java.util.*;

public class CPDPSolution implements Solution {

    private CPDPInstance instance;
    private int of;
    private Set<Integer> selected;

    public CPDPSolution(CPDPInstance instance) {
        this.instance = instance;
        this.of = instance.getInitialOF();
        selected = new HashSet<>(instance.getP());
    }

    public CPDPSolution(CPDPSolution sol) {
        copy(sol);
    }

    public void copy(CPDPSolution sol) {
        this.instance = sol.instance;
        this.of = sol.of;
        this.selected = new HashSet<>(sol.selected);
    }

    public int size() {
        return selected.size();
    }

    public int minDist(int u) {
        int minDist = instance.distance(u, instance.getClosest(u));
        for (int s : selected) {
            if (s == u) continue;
            int d = instance.distance(u, s);
            minDist = Math.min(d, minDist);
        }
        return minDist;
    }

    public Set<Integer> getSelected() {
        return selected;
    }

    public int[] getCritical() {
        int[] critical = new int[2];
        int minDist = 0x3f3f3f;
        for (int s1 : selected) {
            for (int s2 : selected) {
                if (s1 < s2) {
                    int d = instance.distance(s1, s2);
                    if (d < minDist) {
                        minDist = d;
                        critical[0] = s1;
                        critical[1] = s2;
                    }
                }
            }
        }
        return critical;
    }

    public void add(int u) {
        int closestToU = instance.getClosest(u);
        int dToOrig = instance.distance(u, closestToU);
        if (dToOrig < of) {
            of = dToOrig;
        }
        for (int v : selected) {
            int d = instance.distance(u, v);
            if (d < of) {
                of = d;
            }
        }
        selected.add(u);
    }

    public CPDPInstance getInstance() {
        return instance;
    }

    public boolean contains(int u) {
        return selected.contains(u);
    }

    public void remove(int u) {
        selected.remove(u);
        of = eval();
    }

    public int eval() {
        int minDist = instance.getInitialOF();
        int n = instance.getN();
        int r = instance.getR();
        for (int u : selected) {
            for (int v = r; v < n; v++) {
                minDist = Math.min(minDist, instance.distance(u, v));
            }
            for (int v : selected) {
                if (u < v) {
                    minDist = Math.min(minDist, instance.distance(u, v));
                }
            }
        }
        return minDist;
    }

    public int getOf() {
        return of;
    }

    public int getClosest(int u) {
        int minDist = 0x3f3f3f;
        int closest = -1;
        for (int s : selected) {
            if (u != s) {
                int d = instance.distance(u, s);
                if (d < minDist) {
                    minDist = d;
                    closest = s;
                }
            }
        }
        return closest;
    }

    public int distance(CPDPSolution sol) {
        int diff = 0;
        for (int s : selected) {
            if (!sol.selected.contains(s)) diff++;
        }
        return diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPDPSolution that = (CPDPSolution) o;
        return Objects.equals(selected, that.selected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selected);
    }

    @Override
    public String toString() {
        return selected.toString() + " -> " + of;
    }
}
