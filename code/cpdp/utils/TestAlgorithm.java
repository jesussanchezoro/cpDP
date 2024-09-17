package cpdp.utils;

import cpdp.algorithm.ParallelEvolutionaryPR_v2;
import cpdp.constructives.ConstGRASP;
import cpdp.improvement.LS1x1Constraint;
import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.tools.RandomManager;

import java.util.Random;

public class TestAlgorithm {

    public static void main(String[] args) {
        RandomManager.setSeed(1234);
        Algorithm<CPDPInstance, CPDPSolution> alg = new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 250);
        String instanceFile = "instances/brd14051_5_15_greedy.txt";
        CPDPInstance instance = new CPDPInstance(instanceFile);
        alg.execute(instance);
    }
}
