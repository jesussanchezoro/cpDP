package cpdp.utils;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;

import java.util.ArrayList;
import java.util.List;

public class EvaluateOutput {

    public static void main(String[] args) {
//        [0, 8083, 1672, 3258, 4844] -> 1460
        String instanceFile = "instances/brd14051_5_15_greedy.txt";
        CPDPInstance instance = new CPDPInstance(instanceFile);
        CPDPSolution sol = new CPDPSolution(instance);
        int[] sel = new int[]{0, 8083, 1672, 3258, 4844};
        int r = instance.getR();
        for (int i = r; i < instance.getN(); i++) {
            sol.add(i);
        }
        for (int s : sel) {
            sol.add(s);
        }
        for (int s1 : sol.getSelected()) {
            for (int s2 : sol.getSelected()) {
                if (s1 < s2) {
                    int d = instance.distance(s1, s2);
                    System.out.println(s1+ " -> "+s2 + " = "+d);
                }
            }
        }
    }
}
