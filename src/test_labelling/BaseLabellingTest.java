package test_labelling;

import java.util.Arrays;

import junit.framework.Assert;
import labelling.AtomContainerAtomPermutor;
import labelling.AtomContainerPrinter;
import labelling.ICanonicalLabeller;

import org.openscience.cdk.interfaces.IAtomContainer;

public class BaseLabellingTest {
    
    public void permuteTest(
            ICanonicalLabeller labeller, IAtomContainer atomContainer) {
        AtomContainerPrinter printer = new AtomContainerPrinter();
        String original = printer.toString(
                labeller.getCanonicalMolecule(atomContainer));
        AtomContainerAtomPermutor acap = new 
            AtomContainerAtomPermutor(atomContainer);
        
        while (acap.hasNext()) {
            IAtomContainer permutation = acap.next();
            IAtomContainer canonical = labeller.getCanonicalMolecule(permutation);
            String permutedString = printer.toString(permutation);
            String canonicalString = printer.toString(canonical);
            int[] p = labeller.getCanonicalPermutation(permutation);
            System.out.println(
                   canonicalString + " " + permutedString + Arrays.toString(p));
//            Assert.assertEquals(original, canonicalString);
        }
    }

}
