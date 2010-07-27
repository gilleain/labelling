package test_labelling;

import junit.framework.Assert;
import labelling.AtomContainerPrinter;
import labelling.ICanonicalLabeller;
import labelling.MoleculeSignatureLabellingAdaptor;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;

public class SignatureLabellingTest extends BaseLabellingTest {
    
    private ICanonicalLabeller labeller = new MoleculeSignatureLabellingAdaptor();
    
    @Test
    public void labellingTest() {
       IAtomContainer benzene = new AtomContainer();
       for (int i = 0; i < 6 ; i++) {
           benzene.addAtom(new Atom("C"));
       }
       // test requires that we know the particular canonical order!
       benzene.addBond(0, 1, Order.SINGLE);
       benzene.addBond(0, 4, Order.SINGLE);
       benzene.addBond(1, 2, Order.SINGLE);
       benzene.addBond(2, 3, Order.SINGLE);
       benzene.addBond(3, 5, Order.SINGLE);
       benzene.addBond(4, 5, Order.SINGLE);
       
       AtomContainerPrinter printer = new AtomContainerPrinter();
       
       String original = printer.toString(benzene);
       ICanonicalLabeller labeller = new MoleculeSignatureLabellingAdaptor();
       IAtomContainer permutedBenzene = labeller.getCanonicalMolecule(benzene);
       String permuted = printer.toString(permutedBenzene);
       Assert.assertEquals(original, permuted);
    }
    
    @Test
    public void permuteBenzene() {
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
        permuteTest(labeller, benzene);
    }

}
