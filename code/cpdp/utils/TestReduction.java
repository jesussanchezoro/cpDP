package cpdp.utils;

import java.io.File;

public class TestReduction {

    public static void main(String[] args) {
        String tspPath = "instancesnewformat";
        String dir = "instancesnewformat/ar9152_25_20_greedy_y.log";
        String outPath = "instancesnewformat";
        CPDPInstanceAdaptation instance1 = new CPDPInstanceAdaptation(dir, tspPath, outPath);

//        String[] fileNames = new File(tspPath).list((dir1, name) -> name.endsWith(".tsp"));
//        String[] fileNames = new File(dir).list((dir1, name) -> name.endsWith(".log"));
//        for (String fileName : fileNames) {
//            CPDPInstanceAdaptation instance1 = new CPDPInstanceAdaptation(dir+"/"+fileName, tspPath, outPath);
////            CPDPInstanceAdaptation instance1 = new CPDPInstanceAdaptation(dir+"/"+fileName, tspPath, outPath, 1.0/10.0);
////            CPDPInstanceAdaptation instance2 = new CPDPInstanceAdaptation(dir+"/"+fileName, tspPath, outPath, 2.0/10.0);
////            CPDPInstanceAdaptation instance3 = new CPDPInstanceAdaptation(dir+"/"+fileName, tspPath, outPath, 4.0/10.0);
//        }
    }
}
