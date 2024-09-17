package cpdp.combinations;

import cpdp.structure.CPDPSolution;
import grafo.optilib.metaheuristics.Combiner;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.tools.RandomManager;

import java.util.*;

public class ExteriorPathRelinking implements Combiner<CPDPSolution> {

    private Improvement<CPDPSolution> ls;

    public ExteriorPathRelinking(Improvement<CPDPSolution> ls) {
        this.ls = ls;
    }

    public ExteriorPathRelinking() {
        ls = null;
    }

    @Override
    public CPDPSolution combine(CPDPSolution iniSol, CPDPSolution guidingSol) {
        CPDPSolution bestPath = null;
        int r = iniSol.getInstance().getR();
        List<Integer> commonElems = new ArrayList<>(r);
        List<Integer> diffElems = new ArrayList<>(r);
        for (int i = 0; i < r; i++) {
            if (iniSol.contains(i) && guidingSol.contains(i)) {
                commonElems.add(i);
            } else if (!iniSol.contains(i)) {
                diffElems.add(i);
            }
        }
        CPDPSolution pathSol = new CPDPSolution(iniSol);
        while (!commonElems.isEmpty() && !diffElems.isEmpty()) {
            int common = commonElems.remove(RandomManager.getRandom().nextInt(commonElems.size()));
            pathSol.remove(common);
            int replace = diffElems.remove(RandomManager.getRandom().nextInt(diffElems.size()));
            pathSol.add(replace);
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
