package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface CanonicalLabeller {
    
    public IAtomContainer getCanonicalMolecule(IAtomContainer container);

}
