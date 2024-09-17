package cpdp.structure;

import grafo.optilib.structure.Instance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CPDPInstance implements Instance {

    private String name;
    private int n;
    private int r;
    private int p;
    private int q;
    private int[][] distance;
    private int initialOF;
    private int[] closest;
    private Map<Integer, Set<Integer>> closestTo;
    private float[][] coords;

    public CPDPInstance(String path) {
        readInstance(path);
    }

    public int distance(int u, int v) {
        return distance[u][v];
    }

    public int getN() {
        return n;
    }

    public int getP() {
        return p;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public String getName() {
        return name;
    }

    public int getInitialOF() {
        return initialOF;
    }

    public int getClosest(int u) {
        return closest[u];
    }

    public int[] getClosest() {
        return closest;
    }

    public Map<Integer, Set<Integer>> getClosestTo() {
        return closestTo;
    }

    public float[][] getCoords() {
        return coords;
    }

    public void readInstance(String path) {
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            System.out.println("Open: "+path);
            name = path.substring(path.lastIndexOf('/')+1).replace(".txt","").split("_")[0];
            String line = bf.readLine();
//            System.out.println("Line1: "+line);
            line = bf.readLine();
//            System.out.println("Line2: "+line);
            String[] tokens = line.trim().split("\\s+");
            n = Integer.parseInt(tokens[0]);
            r = Integer.parseInt(tokens[1]);
            p = Integer.parseInt(tokens[2]);
//            p = 25;
            q = Integer.parseInt(tokens[3]);
            distance = new int[n][n];
            closest = new int[n];
            closestTo = new HashMap<>(n);
            for (int i = r; i < n; i++) {
                closestTo.put(i, new HashSet<>(n));
            }
            Arrays.fill(closest, -1);
            initialOF = 0x3f3f3f;
            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    line = bf.readLine();
                    tokens = line.trim().split("\\s+");
                    if (tokens.length < 3) {
                        System.out.println("LINE MAL: |"+line+"|");
                    }
                    int u = Integer.parseInt(tokens[0]);
                    int v = Integer.parseInt(tokens[1]);
                    int d = Integer.parseInt(tokens[2]);
//                    if (u >= r && v >= r) {
//                        System.out.println(u+"\t"+v+"\t"+d);
//                    }
                    if (u >= r || v >= r) {
//                        if (d < initialOF) {
                        if (u>=r && v>=r && d < initialOF) {
                            initialOF = d;
                        }
                        if (v >= r && (closest[u] < 0 || d < distance[u][closest[u]])) {
                            closest[u] = v;
                            closestTo.get(v).add(u);
                        }
                        if (u >= r && (closest[v] < 0 || d < distance[v][closest[v]])) {
                            closest[v] = u;
                            closestTo.get(u).add(v);
                        }
                    }
                    distance[u][v] = d;
                    distance[v][u] = d;
                }
            }

            coords = new float[n][2];
            for (int i = 0; i < n; i++) {
                tokens = bf.readLine().split("\\s+");
                coords[i][0] = Float.parseFloat(tokens[0]);
                coords[i][1] = Float.parseFloat(tokens[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
