package cpdp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.SortedSet;
import java.util.TreeSet;

public class GenerateJuliaScript {

    public static void main(String[] args) throws FileNotFoundException {
        SortedSet<String> names = new TreeSet<>();
        String dir = "../instances/cpdp/newinstances";
        String[] fileNames = new File(dir).list((dir1, name) -> name.endsWith(".txt"));
        for (String fileName : fileNames) {
            names.add(fileName.substring(0, fileName.indexOf("_")));
        }
        String outFile = "./julia_big_instances.sh";
        int[] pVals = {25, 30};
        int[] qVals = {5, 10, 15, 20};
        String constExec = "julia cond_script_jesus.jl";
        PrintWriter pw = new PrintWriter(outFile);
        pw.println("#!/bin/sh");
        pw.println();
        for (String name : names) {
            for (int p : pVals) {
                for (int q : qVals) {
                    pw.println(constExec+" "+name+" "+p+" "+q);
                }
            }
        }
        pw.close();
    }
}
