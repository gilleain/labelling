package test_labelling;

import labelling.AtomContainerAtomPermutor;
import labelling.AtomContainerPrinter;
import labelling.ICanonicalLabeller;

import org.openscience.cdk.interfaces.IAtomContainer;

public class BaseLabellingTest {
    
    public void permuteTest(
            ICanonicalLabeller labeller, IAtomContainer atomContainer) {
        AtomContainerPrinter printer = new AtomContainerPrinter();
        String original = printer.toString(atomContainer);
        AtomContainerAtomPermutor acap = new 
            AtomContainerAtomPermutor(atomContainer);
        
        while (acap.hasNext()) {
            IAtomContainer permutation = acap.next();
            IAtomContainer canonical = labeller.getCanonicalMolecule(permutation);
            String permutedString = printer.toString(permutation);
            String canonicalString = printer.toString(canonical);
            System.out.println(canonicalString + " " + permutedString);
        }
    }

}
