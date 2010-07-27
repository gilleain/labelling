package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ICanonicalLabeller {
    
    public IAtomContainer getCanonicalMolecule(IAtomContainer container);

}
