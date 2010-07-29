package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

public class MoleculeSignatureLabellingAdaptor implements ICanonicalMoleculeLabeller {

    @Override
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        return AtomContainerAtomPermutor.permute(
                getCanonicalPermutation(container), container);
    }

    @Override
    public int[] getCanonicalPermutation(IAtomContainer container) {
        MoleculeSignature molSig = new MoleculeSignature(container); 
        return molSig.getCanonicalLabels();
    }

}
