package labelling;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

public class MoleculeSignatureLabellingAdaptor implements CanonicalLabeller {

    @Override
    public IAtomContainer getCanonicalMolecule(IAtomContainer container) {
        MoleculeSignature molSig = new MoleculeSignature(container); 
        int[] labelling = molSig.getCanonicalLabels();
        return AtomContainerAtomPermutor.permute(labelling, container);
    }

}
