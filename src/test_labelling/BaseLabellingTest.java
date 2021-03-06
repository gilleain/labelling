package test_labelling;

import java.util.Arrays;

import junit.framework.Assert;
import labelling.AtomContainerAtomPermutor;
import labelling.AtomContainerPrinter;
import labelling.ICanonicalMoleculeLabeller;

import org.openscience.cdk.interfaces.IAtomContainer;

public class BaseLabellingTest {
    
    public void permuteTest(
            ICanonicalMoleculeLabeller labeller, IAtomContainer atomContainer) {
        AtomContainerPrinter printer = new AtomContainerPrinter();
        String original = printer.toString(atomContainer);
        String canonOriginal = printer.toString(
                labeller.getCanonicalMolecule(atomContainer));
        AtomContainerAtomPermutor acap = new 
            AtomContainerAtomPermutor(atomContainer);
        System.out.println(original + " -> " 
                + Arrays.toString(labeller.getCanonicalPermutation(atomContainer))
                + " -> " + canonOriginal);
        while (acap.hasNext()) {
            IAtomContainer permutation = acap.next();
            IAtomContainer canonical = labeller.getCanonicalMolecule(permutation);
            String permutedString = printer.toString(permutation);
            String canonicalString = printer.toString(canonical);
            int[] p = labeller.getCanonicalPermutation(permutation);
            System.out.println(
                   permutedString 
                   + " -> " + Arrays.toString(p) + " -> "
                   + canonicalString);
//            Assert.assertEquals(original, canonicalString);
        }
    }

}
