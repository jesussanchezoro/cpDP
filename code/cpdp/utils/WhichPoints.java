package cpdp.utils;

import cpdp.structure.CPDPInstance;

public class WhichPoints {

    public static int findPoint(float[][] coords, double x, double y) {
        for (int i = 0; i < coords.length; i++) {
            if (Math.abs(coords[i][0]-x) <= 1 && Math.abs(coords[i][1]-y) <= 1) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        String path = "./instances/ar9152_5_20_greedy.txt";
        CPDPInstance instance = new CPDPInstance(path);
//        double[] points = {5276.0, 2787.0, 4485.0, 8348.0, 3528.0, 6149.0, 4725.0, 5421.0, 5391.0, 9583.0};
//        double[] points = {6431.0, 10411.0, 4015.0, 2667.0, 8555.0, 4116.0, 2951.0, 7293.0, 5879.0, 5946.0, 8104.0, 8072.0, 6525.0, 2595.0, 3997.0, 9559.0, 3177.0, 4911.0, 5800.0, 8222.0, 5256.0, 4134.0, 6914.0, 4567.0, 4606.0, 7042.0, 4887.0, 10833.0, 7986.0, 2771.0};
        double[] points = {42566.6667, 64283.3333, 30152.2222, 68660.2778, 24933.3333, 66183.3333, 36316.6667, 59433.3333, 50283.3333, 70150.0, 33016.6667, 61050.0, 43750.0, 69666.6667, 23250.0, 62166.6667, 37366.6667, 67616.6667, 38883.3333, 60350.0, 52300.0, 68550.0, 46550.0, 68950.0, 31233.3333, 59216.6667, 29866.6667, 65800.0, 35866.6667, 70066.6667, 40300.0, 68966.6667, 34816.6667, 66450.0, 35814.7222, 61905.2778, 27266.6667, 58250.0, 27433.3333, 61016.6667, 33766.6667, 63483.3333, 48433.3333, 71850.0, 41566.6667, 71533.3333, 39466.6667, 65500.0, 28533.3333, 63416.6667};
        System.out.println(points.length);

        float[][] coords = instance.getCoords();

        for (int i = 0; i < points.length/2; i+=2) {
            int s = findPoint(coords, points[i], points[i+1]);
//            System.out.println(s);
        }
    }
}
