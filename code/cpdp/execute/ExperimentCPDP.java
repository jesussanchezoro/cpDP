package cpdp.execute;


import cpdp.algorithm.*;
import cpdp.constructives.ConstGRASP;
import cpdp.constructives.ConstRandom;
import cpdp.improvement.LS1x1;
import cpdp.improvement.LS1x1Constraint;
import cpdp.improvement.LS1x1Critical;
import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPInstanceFactory;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.results.Experiment;

import java.io.*;

public class ExperimentCPDP {

    public static void main(String[] args) throws IOException {
        int graspIters = 100;
        int popSize = 100;
        int refsetSize = 10;

        Algorithm<CPDPInstance, CPDPSolution>[] execution = new Algorithm[] {
//                GRASP
//                new ParallelGRASP(new ConstGRASP(0.1f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.2f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.3f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.4f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.5f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.6f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.7f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.8f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(0.9f), new LS1x1Constraint(), graspIters),
//                new ParallelGRASP(new ConstGRASP(-1), new LS1x1Constraint(), graspIters),
//
////                DELTA STATIC PR
//                new DeltaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize),
//                new DeltaStaticPR(new ConstGRASP(0.9f), new LS1x1Constraint(), popSize, refsetSize),

//                GAMMA STATIC PR
//                new GammaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize, 0.1f),
//                new GammaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize, 0.2f),
//                new GammaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize, 0.3f),
//                new GammaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize, 0.4f),
//                new GammaStaticPR(new ConstGRASP(-1f), new LS1x1Constraint(), popSize, refsetSize, 0.25f),
//                new GammaStaticPR(new ConstGRASP(0.9f), new LS1x1Constraint(), popSize, refsetSize, 0.25f),

//                DYNAMIC
//                new DynamicPR_v2(new ConstGRASP(-1F), new LS1x1Constraint(), 10, 100),

//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 100),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 1000),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 1000),

//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Critical(), 50, 100),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1(), 50, 100),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 100),

//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 100, 10),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 100, 10),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 1000, 10),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 10000, 10),
//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 10000, 1),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 100, 1000),

//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 100, 1000),

//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 1000),


//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 100),

//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 100),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 250),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 250),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 10, 500),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 500),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 50, 500),


//                new EvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 250, 10),
//                new EvolutionaryPR_v2(new ConstRandom(), new LS1x1Constraint(), 25, 250, 10),
//                new AlgConstructive(new ConstRandom(), 1000)
//                new AlgConstructive(new ConstGRASP(-1f), 1000)
                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 250, 1),
//                new ParallelEvolutionaryPR_v2(new ConstGRASP(-1f), new LS1x1Constraint(), 25, 500),
        };
        String folder = "realcase-output";
//        String folder = "pruebas";
        String instancesPath = args.length>1 ? args[0] : "../instances/cpdp/"+folder;
//        String instancesPath = args.length>1 ? args[0] : "./instances/"+folder;
        Experiment<CPDPInstance, CPDPInstanceFactory, CPDPSolution> experiment = new Experiment<>(execution, new CPDPInstanceFactory());
        experiment.launch(instancesPath, new String[]{".txt"});


    }
}
