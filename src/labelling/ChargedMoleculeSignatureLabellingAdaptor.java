package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;

import chargedsigs.ChargedMoleculeSignature;

public class ChargedMoleculeSignatureLabellingAdaptor 
       implements ICanonicalMoleculeLabeller {

    @Override
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        return AtomContainerAtomPermutor.permute(
                getCanonicalPermutation(container), container);
    }

    @Override
    public int[] getCanonicalPermutation(IAtomContainer container) {
        ChargedMoleculeSignature molSig = 
            new ChargedMoleculeSignature(container); 
        return molSig.getCanonicalLabels();
    }
}
