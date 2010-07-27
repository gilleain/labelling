package test_labelling;

import junit.framework.Assert;
import labelling.AtomContainerPrinter;
import labelling.CanonicalLabellingAdaptor;
import labelling.ICanonicalLabeller;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;

public class CanonLabelerTest {
    
    @Test
    public void test() {
        IAtomContainer benzene = new AtomContainer();
        for (int i = 0; i < 6 ; i++) {
            benzene.addAtom(new Atom("C"));
        }
        // test requires that we know the particular canonical order!
        benzene.addBond(0, 1, Order.SINGLE);
        benzene.addBond(0, 2, Order.SINGLE);
        benzene.addBond(1, 3, Order.SINGLE);
        benzene.addBond(2, 4, Order.SINGLE);
        benzene.addBond(3, 5, Order.SINGLE);
        benzene.addBond(4, 5, Order.SINGLE);
        
        String original = AtomContainerPrinter.toString(benzene);
        ICanonicalLabeller labeller = new CanonicalLabellingAdaptor();
        IAtomContainer permutedBenzene = labeller.getCanonicalMolecule(benzene);
        String permuted = AtomContainerPrinter.toString(permutedBenzene);
        Assert.assertEquals(original, permuted);
    }

}