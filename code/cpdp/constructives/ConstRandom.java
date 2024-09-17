package cpdp.constructives;

import cpdp.structure.CPDPInstance;
import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.tools.RandomManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ConstRandom implements Constructive<CPDPInstance, CPDPSolution> {


    @Override
    public CPDPSolution constructSolution(CPDPInstance instance) {
        Random rnd = RandomManager.getRandom();
        CPDPSolution sol = new CPDPSolution(instance);
        int r = instance.getR();
        int p = instance.getP();
        List<Integer> cl = new ArrayList<>(r);
        for (int i = 0; i < r; i++) {
            cl.add(i);
        }
        Collections.shuffle(cl, RandomManager.getRandom());
        for (int i = 0; i < p; i++) {
            sol.add(cl.get(i));
        }
        return sol;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
