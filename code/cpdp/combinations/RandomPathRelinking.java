package cpdp.combinations;

import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;

import java.util.*;

public class RandomPathRelinking implements Combiner<CPDPSolution> {

    private Improvement<CPDPSolution> ls;

    public RandomPathRelinking() {
        ls = null;
    }

    public RandomPathRelinking(Improvement<CPDPSolution> ls) {
        this.ls = ls;
    }

    @Override
    public CPDPSolution combine(CPDPSolution iniSol, CPDPSolution guidingSol) {
        CPDPSolution bestPath = null;
        Set<Integer> iniElemsSet = new HashSet<>(iniSol.getSelected());
        iniElemsSet.removeAll(guidingSol.getSelected());
        Set<Integer> guidingElemsSet = new HashSet<>(guidingSol.getSelected());
        guidingElemsSet.removeAll(iniSol.getSelected());
        List<Integer> iniElems = new ArrayList<>(iniElemsSet);
        Collections.shuffle(iniElems, RandomManager.getRandom());
        List<Integer> guidingElems = new ArrayList<>(guidingElemsSet);
        Collections.shuffle(guidingElems, RandomManager.getRandom());

        CPDPSolution pathSol = new CPDPSolution(iniSol);
        while (iniElems.size() > 1) {
            pathSol.remove(iniElems.remove(0));
            pathSol.add(guidingElems.remove(0));
            if (ls != null) {
                CPDPSolution lsPathSol = new CPDPSolution(pathSol);
                ls.improve(lsPathSol);
                if (bestPath == null) {
                    bestPath = new CPDPSolution(lsPathSol);
                } else if (lsPathSol.getOf() > bestPath.getOf()) {
                    bestPath.copy(lsPathSol);
                }
            } else {
                if (bestPath == null) {
                    bestPath = new CPDPSolution(pathSol);
                } else if (pathSol.getOf() > bestPath.getOf()) {
                    bestPath.copy(pathSol);
                }
            }

        }
        return bestPath;
    }
}
