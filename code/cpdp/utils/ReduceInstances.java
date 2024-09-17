package cpdp.utils;

import cpdp.utils.CPDPInstanceAdaptation;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReduceInstances {

    public static Set<String> instancesDone(String output) {
        Set<String> done = new HashSet<>();
        String[] fileNames = new File(output).list((dir1, name) -> name.endsWith(".txt"));
        for (String fileName : fileNames) {
            done.add(fileName.replace(".txt", ""));
        }
        return done;
    }

    public static void main(String[] args) {
//        String tspPath = "../instances/cpdp/tsp";
        String tspPath = "../instances/cpdp/realcase";
//        String tspPath = args[0];
//        String dir = "../instances/cpdp/greedy-optimal";
        String dir = "../instances/cpdp/realcase-optimal";
//        String dir = "../instances/cpdp/greedy-all";
//        String dir = "../instances/cpdp/largep";
//        String dir = args[1];
//        String outPath = "../instances/cpdp/newinstances";
        String outPath = "../instances/cpdp/realcase-output";
//        String outPath = args[2];
        Set<String> done = instancesDone(outPath);
//        String[] fileNames = new File(tspPath).list((dir1, name) -> name.endsWith(".tsp"));
        String[] fileNames = new File(dir).list((dir1, name) -> name.endsWith(".log"));
        for (String fileName : fileNames) {
            String name = fileName.replace(".log", "");
            if (done.contains(name)) {
                System.out.println("SKIPPING "+name);
            } else {
                CPDPInstanceAdaptation instance1 = new CPDPInstanceAdaptation(dir + "/" + fileName, tspPath, outPath);
            }
        }
    }
}
