package cpdp.utils;

import java.io.*;
import java.util.*;

public class CPDPInstanceAdaptation {

    private String name;
    private int n;
    private int p;
//    private double pMult;
    private int q;
    private int[][] distance;
    private boolean round;
    private Set<Coordinate> originalQPoints;
    private List<Integer> qPoints;

    private class Coordinate {
        float x;
        float y;

        public Coordinate(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "("+x+", "+y+")";
        }
    }

    public CPDPInstanceAdaptation(String path, String tspPath, String outPath) {
//    public CPDPInstanceAdaptation(String path, String tspPath, String outPath, int p, int q) {
        System.out.println("PROCESSING "+path);
        String tspFileName = path.substring(path.lastIndexOf('/')+1, path.indexOf('_')) + ".tsp";
        String[] findPQ = path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('_')).split("_");
        p = Integer.parseInt(findPQ[1]);
        q = Integer.parseInt(findPQ[2]);
        System.out.println(path.substring(path.lastIndexOf('/')));
        Set<Coordinate> q = readFile(path); // Fichero .log
        if (q != null) {
            //        this.q = (int) Math.ceil(q.size() / 10.0);
            //        this.p = (int) Math.ceil(q.size() * pMult);
            List<Coordinate> finalCoords = readTSPFile(tspPath + "/" + tspFileName, q);
            writeFile(outPath + "/" + path.substring(path.lastIndexOf('/') + 1).replace(".log", ".txt"), finalCoords);
            //        writeFile(outPath+"/"+path.replace(".tsp",".txt"), finalCoords);
        }
    }

    private void writeFile(String outPath, List<Coordinate> coords) {
        try (PrintWriter pw = new PrintWriter(outPath)) {
            int nBefore = coords.size();
            coords.addAll(originalQPoints);
            int[][] d = euc2d(coords, round);
            int n = coords.size();
            pw.println(name);
            pw.println(n+" "+nBefore+" "+p+" "+q);
            for (int i = 0; i < n; i++) {
                for (int j = i+1; j < n; j++) {
                    pw.println(i+" "+j+" "+d[i][j]);
                }
            }
            for (Coordinate coord : coords) {
                pw.println(coord.x+" "+coord.y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<Coordinate> readFile(String path) {
        Set<Coordinate> q = new HashSet<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            while (!(line = bf.readLine()).startsWith("Q"));
            String[] tokens = line.split("\\s+");
            for (int i = 1; i < tokens.length; i+=2) {
                float x = Float.parseFloat(tokens[i]);
                float y = Float.parseFloat(tokens[i+1]);
                q.add(new Coordinate(x,y));
            }
        } catch (Exception e) {
            return null;
        }
        return q;
    }

    private List<Coordinate> readTSPFile(String path, Set<Coordinate> q) {
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line;
            String[] tokens;
            line = bf.readLine();
            name = line.split(":")[1].trim();
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("estadisticas.csv", true)));
            pw.print(name+"\t");
            System.out.println(name+"\t");
            while ((line = bf.readLine()).startsWith("COMMENT")); // Skip comments and type
            n = Integer.parseInt(bf.readLine().split(":")[1].trim());
            String edgeWeightType = bf.readLine().split(":")[1].trim();
            bf.readLine(); // Skip node_coord_section
            List<Coordinate> coordinates = new ArrayList<>(n);
            Set<Coordinate> alreadyUsedCoords = new HashSet<>(n);
            for (int i = 0; i < n; i++) {
                tokens = bf.readLine().split("\\s+");
                Coordinate c = new Coordinate(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                if (!alreadyUsedCoords.contains(c)) {
                    alreadyUsedCoords.add(c);
                    coordinates.add(c);
                }
            }
            originalQPoints = new HashSet<>();
            qPoints = new ArrayList<>(coordinates.size());
            for (int i = 0; i < coordinates.size(); i++) {
                if (q.contains(coordinates.get(i))) {
                    qPoints.add(i);
                    originalQPoints.add(coordinates.get(i));
                }
            }
            round = edgeWeightType.equals("CEIL_2D"); // If EUC_2D do not round, do it only if CEIL_2D
            distance = euc2d(coordinates, round);
            int lb = heuristicCPDP(coordinates);
            List<Coordinate> finalCoords = graphReduction(coordinates, lb);
            return finalCoords;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int[][] euc2d(List<Coordinate> coordinates, boolean round) {
        n = coordinates.size();
        int[][] distance = new int[n][n];
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate ci = coordinates.get(i);
            for (int j = i+1; j < coordinates.size(); j++) {
                Coordinate cj = coordinates.get(j);
                float xd = ci.x - cj.x;
                float yd = ci.y - cj.y;
                int d = 0;
                double sqrt = Math.sqrt(xd * xd + yd * yd);
                d = (int) (round?Math.ceil(sqrt):(sqrt+0.5f));
                distance[i][j] = d;
                distance[j][i] = d;
            }
        }
        return distance;
    }

    private void floydWarshall(int[][] distance) {
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    distance[i][j] = Math.min(distance[i][j], distance[i][k] + distance[k][j]);
                }
            }
        }
    }

    private int heuristicCPDP(List<Coordinate> coords) {
        Set<Integer> finalCoords = new HashSet<>(n);
        int minD = 0x3f3f3f;
        for (int i = 0; i < qPoints.size(); i++) {
            for (int j = i+1; j < qPoints.size(); j++) {
                int d = distance[qPoints.get(i)][qPoints.get(j)];
                minD = Math.min(d, minD);
            }
            finalCoords.add(qPoints.get(i));
        }
        int lb = minD;
        while (finalCoords.size() < p + q) {
            int bestId = -1;
            int maxDist = 0;
            for (int i = 0; i < coords.size(); i++) {
                int minDi = lb;
                if (finalCoords.contains(i)) continue;
                for (int fC : finalCoords) {
                    minDi = Math.min(minDi, distance[i][fC]);
                }
                if (minDi > maxDist) {
                    maxDist = minDi;
                    bestId = i;
                }
            }
            finalCoords.add(bestId);
            lb = Math.min(lb, maxDist);
        }
//        System.out.println("INITIAL LB "+lb);
        return lb;
    }

    private List<Coordinate> graphReduction(List<Coordinate> coords, int lb) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter("estadisticas.csv", true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Coordinate> finalCoords = new ArrayList<>(n);
        System.out.print(coords.size()+"\t");
        for (int i = 0; i < coords.size(); i++) {
            boolean add = true;
            for (Integer qPoint : qPoints) {
                if (distance[i][qPoint] < lb) {
                    add = false;
                    break;
                }
            }
            if (add) {
                finalCoords.add(coords.get(i));
            }
        }
        int removed = coords.size() - finalCoords.size();
        System.out.println(removed);
        pw.println(coords.size()+"\t"+removed);
        pw.close();
//        System.out.println("REMOVED: "+removed);
        return finalCoords;
    }

}
