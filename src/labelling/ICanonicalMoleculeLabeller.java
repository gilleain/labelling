package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;

public interface ICanonicalMoleculeLabeller {
    
    public IAtomContainer getCanonicalMolecule(IAtomContainer container);

    public int[] getCanonicalPermutation(IAtomContainer container);
}
