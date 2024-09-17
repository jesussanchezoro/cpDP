package cpdp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ConvertRealCaseScenario {

    static final int EARTH_RADIUS = 6371;

    record LatLon(double lat, double lon) { }

    public static void main(String[] args) {
        String path = "/Users/jesussanchez-orocalvo/IdeaProjects/instances/cpdp/hospitalesSpain.csv";
        String outPath = "/Users/jesussanchez-orocalvo/IdeaProjects/instances/cpdp/hospitalesSpain.tsp";
        List<LatLon> latLon = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(path))) {
            String line = bf.readLine();
            while ((line = bf.readLine()) != null) {
                String[] tokens = line.split(";");
                int id = Integer.parseInt(tokens[0]);
                double lat = Float.parseFloat(tokens[10].replace(",", "."));
                double lon = Float.parseFloat(tokens[11].replace(",", "."));
                latLon.add(new LatLon(lat, lon));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = latLon.size();
        int[][] dist = new int[n][n];
        double[][] location = new double[n][2];
        double minLocX = 0x3f3f3f3f;
        double minLocY = 0x3f3f3f3f;
        for (int i = 0; i < n; i++) {
            LatLon li = latLon.get(i);
            //x = R * cos(latitud) * cos(longitud) \\
            //y = R * cos(latitud) * sin(longitud) \\
            location[i][0] = EARTH_RADIUS * Math.cos(li.lat) * Math.cos(li.lon);
            location[i][1] = EARTH_RADIUS * Math.cos(li.lat) * Math.sin(li.lon);
            minLocX = Math.min(minLocX, location[i][0]);
            minLocY = Math.min(minLocY, location[i][1]);
            for (int j = i+1; j < n; j++) {
                LatLon lj = latLon.get(j);

                // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1))*6371
                int d = (int) Math.ceil(Math.acos(Math.sin(li.lat) * Math.sin(lj.lat) + Math.cos(li.lat) * Math.cos(lj.lat) * Math.cos(lj.lon - li.lon)) * EARTH_RADIUS);
                dist[i][j] = d;
                dist[j][i] = d;
            }
        }
        try (PrintWriter pw = new PrintWriter(outPath); PrintWriter pwMatching = new PrintWriter("matching.csv")) {
            pw.println("NAME : HOSPITAL_SPAIN");
            pw.println("TYPE : TSP");
            pw.println("DIMENSION : "+n);
            pw.println("EDGE_WEIGHT_TYPE : EUC_2D");
            pw.println("NODE_COORD_SECTION");
            pwMatching.println("POINT\tCARTX\tCARTY\tLAT\tLON");
            for (int i = 0; i < n; i++) {
                double locX = location[i][0] + Math.abs(minLocX);
                double locY = location[i][1] + Math.abs(minLocY);
                pw.println((i+1) + " " + locX + " " + locY);
                // Matching
                LatLon li = latLon.get(i);
                pwMatching.println((i+1) + "\t" + locX + "\t" + locY + "\t" + li.lat + "\t" + li.lon);
            }
            pw.println("EOF");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
