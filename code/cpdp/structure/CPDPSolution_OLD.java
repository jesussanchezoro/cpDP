package cpdp.structure;

import grafo.optilib.structure.Solution;

import java.util.*;

public class CPDPSolution_OLD implements Solution {

    private CPDPInstance instance;
    private int of;
    private Set<Integer> selected;
    private int[] closest;
    private Map<Integer, Set<Integer>> closestTo;

    private int[] critical;

    public CPDPSolution_OLD(CPDPInstance instance) {
        this.instance = instance;
        this.of = instance.getInitialOF();
        selected = new HashSet<>(instance.getP());
        int n = instance.getN();
        int r = instance.getR();
        closest = new int[n];
        Arrays.fill(closest, -1);
        for (int i = r; i < n; i++) {
            closest[i] = instance.getClosest(i);
        }
        critical = new int[2];
        critical[0] = -1;
        critical[1] = -1;
        closestTo = new HashMap<>(n);
        for (Map.Entry<Integer, Set<Integer>> entry : instance.getClosestTo().entrySet()) {
            closestTo.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    public CPDPSolution_OLD(CPDPSolution_OLD sol) {
        copy(sol);
    }

    public void copy(CPDPSolution_OLD sol) {
        this.instance = sol.instance;
        this.of = sol.of;
        this.selected = new HashSet<>(sol.selected);
        this.closest = sol.closest.clone();
        this.critical = sol.critical.clone();
        this.closestTo = new HashMap<>(instance.getN());
        for (Map.Entry<Integer, Set<Integer>> entry : sol.closestTo.entrySet()) {
            this.closestTo.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
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
        return critical;
    }

    public void add(int u) {
        int closestToU = instance.getClosest(u);
        int dToOrig = instance.distance(u, closestToU);
        if (dToOrig < of) {
            of = dToOrig;
            critical[0] = u;
            critical[1] = closestToU;
        }
        closestTo.put(u, new HashSet<>(instance.getN()));
        updateClosest(u, closestToU, dToOrig);
        for (int v : selected) {
            int d = instance.distance(u, v);
            if (d < of) {
                critical[0] = u;
                critical[1] = v;
                of = d;
            }
            updateClosest(u, v, d);
        }
        selected.add(u);
    }

    public int getClosest(int v) {
        return closest[v];
    }

    private void updateClosest(int u, int v, int d) {
        if (closest[u] < 0 || d < instance.distance(u, closest[u])) {
            closest[u] = v;
            closestTo.get(v).add(u);
        }
        if (closest[v] < 0 || d < instance.distance(v, closest[v])) {
            closest[v] = u;
            closestTo.get(u).add(v);
        }
    }

    public CPDPInstance getInstance() {
        return instance;
    }

    public boolean contains(int u) {
        return selected.contains(u);
    }

    // TODO
    public void remove(int u) {
        // Remove the node
        selected.remove(u);
        // Update all the nodes to which u was the closest node
//        int distToClosestU = instance.distance(u, closest[u]);
        if (closestTo.get(u) != null) {
            for (int v : closestTo.get(u)) {
                int closestToV = instance.getClosest(v);
                int minDist = instance.distance(v, closestToV);
                for (int s : selected) {
                    if (v == s) continue; // It is the same vertex, do not compute distance
                    int d = instance.distance(v, s);
                    if (d < minDist) {
                        minDist = d;
                        closestToV = s;
                    }
                }
                closest[v] = closestToV;
                closestTo.get(closestToV).add(v);
            }
            // U is not the closest node to any other one
            closestTo.remove(u);
        }
        // Update objective function if necessary
//        if (distToClosestU == of) {
            // Reevaluate Obj. Function since it is the critical distance
            evalOFEfficient();
//        }
    }

    private void evalOFEfficient() {
        of = instance.getInitialOF();
        for (int s1 : selected) {
            int closestToS1 = closest[s1];
            int d = instance.distance(s1, closestToS1);
            if (d < of) {
                of = d;
                critical[0] = s1;
                critical[1] = closestToS1;
            }
        }
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

    public int distance(CPDPSolution_OLD sol) {
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
        CPDPSolution_OLD that = (CPDPSolution_OLD) o;
        return Objects.equals(selected, that.selected);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selected);
    }

    @Override
    public String toString() {
        return selected.toString();
    }
}
