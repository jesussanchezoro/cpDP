package cpdp.structure;

import grafo.optilib.structure.InstanceFactory;

public class CPDPInstanceFactory extends InstanceFactory<CPDPInstance> {
    @Override
    public CPDPInstance readInstance(String s) {
        return new CPDPInstance(s);
    }
}
