package cpdp.execute;

import java.io.*;
import java.nio.file.Files;

public class ReadJuliaResults {

    public static void main(String[] args) throws FileNotFoundException {
//        String path = "output_julia";
//        String path = "newpvals_julia";
        String path = "resultados_julia_large_pvals";
        PrintWriter pw = new PrintWriter("julia_large.txt");
        String[] files = new File(path).list((dir, name) -> name.endsWith(".log"));
        for (String file : files) {
            try (BufferedReader bf = new BufferedReader(new FileReader(path+"/"+file))) {
                String[] tokens = file.split("_");
                String name = tokens[0];
                int p = Integer.parseInt(tokens[1]);
                int q = Integer.parseInt(tokens[2]);
                System.out.print(name+"\t"+p+"\t"+q+"\t");
                pw.print(name+"\t"+p+"\t"+q+"\t");
                tokens = bf.readLine().split("\\s+");
                int of = Integer.parseInt(tokens[1]);
                tokens = bf.readLine().split("\\s+");
                float time = Float.parseFloat(tokens[1]);
                System.out.print(of+"\t"+time);
                pw.println(of+"\t"+time);
                bf.readLine();
            } catch (Exception e) {
                System.out.println();
                pw.println();
//                throw new RuntimeException(e);
                System.err.println("ERROR READING: "+file);
            }
        }
        pw.close();
    }
}
