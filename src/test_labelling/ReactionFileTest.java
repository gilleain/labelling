package test_labelling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import labelling.AtomContainerPrinter;
import labelling.ICanonicalLabeller;
import labelling.MoleculeSignatureLabellingAdaptor;

import org.junit.Test;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLRXNReader;

public class ReactionFileTest {
    
    private ICanonicalLabeller labeller = 
        new MoleculeSignatureLabellingAdaptor();
    
    @Test
    public void testRxnFile() throws FileNotFoundException, CDKException {
        String filename = "data/CanonicalTest.rxn";
        MDLRXNReader reader = new MDLRXNReader(new FileReader(filename));
        IReactionSet reactionSet = (IReactionSet) reader.read(new ReactionSet());
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>(); 
        for (IReaction reaction : reactionSet.reactions()) {
            System.out.println("reaction");
            for (IAtomContainer product : reaction.getProducts().molecules()) {
                System.out.println("product");
                containers.add(product);
            }
            for (IAtomContainer reactant : reaction.getReactants().molecules()) {
                System.out.println("product");
                containers.add(reactant);
            }
            
        }
        
        AtomContainerPrinter printer = new AtomContainerPrinter();
        List<String> canonicalStrings = new ArrayList<String>();
        for (IAtomContainer atomContainer : containers) {
            System.out.println(printer.toString(atomContainer));
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(atomContainer);
            String canonicalString = printer.toString(canonicalForm); 
            System.out.println(canonicalString);
            canonicalStrings.add(canonicalString);
        }
        String last = null;
        int i = 0;
        for (String cS : canonicalStrings) {
            if (last == null) {
                last = cS;
            } else {
                boolean equal = last.equals(cS);
                System.out.println(i - 1 + " " + i + " areEqual? " + equal);
                Assert.assertEquals(last, cS);
            }
            i++;
        }
    }

}
