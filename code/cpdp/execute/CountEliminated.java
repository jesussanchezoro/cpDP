package cpdp.execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CountEliminated {

    public static void main(String[] args) {
        String path = "output_julia";
        String[] files = new File(path).list((dir, name) -> name.endsWith(".log"));
        for (String file : files) {
            try (BufferedReader bf = new BufferedReader(new FileReader(path+"/"+file))) {
                String[] tokens = file.split("_");
                String name = tokens[0];
                int p = Integer.parseInt(tokens[1]);
                int q = Integer.parseInt(tokens[2]);
                System.out.print(name+"\t"+p+"\t"+q+"\t");
                String line = bf.readLine();
                while (!line.startsWith("Eliminated")) {
                    line = bf.readLine();
                }
                int elim = Integer.parseInt(line.split("\\s+")[1]);
                System.out.println(elim);
            } catch (Exception e) {
                System.out.println();
//                throw new RuntimeException(e);
                System.err.println("ERROR READING: "+file);
            }
        }
    }
}
