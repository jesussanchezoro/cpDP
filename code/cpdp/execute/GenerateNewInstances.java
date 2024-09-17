package cpdp.execute;

import java.io.*;

public class GenerateNewInstances {

    public static void main(String[] args) throws FileNotFoundException {
        String path = "../instances/cpdp/tsp";
        String[] names = new File(path).list((dir, name) -> name.endsWith(".tsp"));
        PrintWriter pw = new PrintWriter("julia_launcher.sh");
        pw.println("#!/bin/sh");
        pw.println();
        for (String name : names) {
            try (BufferedReader bf = new BufferedReader(new FileReader(path+"/"+name))) {
                String line;
                while (!(line = bf.readLine()).startsWith("DIMENSION"));
                int n = Integer.parseInt(line.split(" : ")[1]);
                System.out.println(name+"\t"+n);
                String instance = name.replace(".tsp", "");
                int q = (int) Math.ceil(n / 10.0);
                int p1 = (int) Math.ceil(n / 10.0);
                int p2 = (int) Math.ceil(2 * n / 10.0);
                int p3 = (int) Math.ceil(4 * n / 10.0);

                pw.println("julia cond_script_jesus.jl "+instance+" "+p1+" "+q);
                pw.println("julia cond_script_jesus.jl "+instance+" "+p2+" "+q);
                pw.println("julia cond_script_jesus.jl "+instance+" "+p3+" "+q);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pw.close();
    }
}
