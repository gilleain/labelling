package test_labelling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import labelling.AtomContainerPrinter;
import labelling.ChargedSignatureReactionCanoniser;
import labelling.ICanonicalMoleculeLabeller;
import labelling.ICanonicalReactionLabeller;
import labelling.MoleculeSignatureLabellingAdaptor;
import labelling.SignatureReactionCanoniser;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

public class ReactionFileTest {
    
    private ICanonicalMoleculeLabeller labeller = 
        new MoleculeSignatureLabellingAdaptor();
    
    public void testFile(String filename) throws
            FileNotFoundException, CDKException {
        IReaction reaction = ReactionTestUtility.getReaction(filename, false);
        List<IAtomContainer> containers = new ArrayList<IAtomContainer>(); 
        
        System.out.println("reaction");
        for (IAtomContainer product : reaction.getProducts().molecules()) {
            System.out.println("product");
            containers.add(product);
        }
        for (IAtomContainer reactant : reaction.getReactants().molecules()) {
            System.out.println("product");
            containers.add(reactant);
        }
        
        // tmp
        SmilesGenerator smilesGen = new SmilesGenerator();
        
        
        AtomContainerPrinter printer = new AtomContainerPrinter();
        List<String> canonicalStrings = new ArrayList<String>();
        for (IAtomContainer atomContainer : containers) {
            String smiles = smilesGen.createSMILES(
                    AtomContainerManipulator.removeHydrogens(atomContainer));
            System.out.println(smiles);
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
    
    public void writeCanonicalRxnFile(
            String filename, String outPrefix, ICanonicalReactionLabeller reactionLabeller) 
    throws CDKException, IOException {
        IReaction reaction = ReactionTestUtility.getReaction(filename,false);
        checkCharge(reaction);
        IReaction canonReaction = reactionLabeller.getCanonicalReaction(reaction);
        
//        ReactionTestUtility.printReactionToStdout(reaction);
        
        String file_root = filename.substring(0, filename.indexOf("."));
        String outfileName = file_root + outPrefix + "canonical.rxn";
        File outfile = new File(outfileName);
        outfile.createNewFile();
        FileWriter writer = new FileWriter(outfile); 
        MDLRXNWriter rxnWriter = new MDLRXNWriter(writer);
        rxnWriter.write(canonReaction);
        rxnWriter.close();
    }
    
    public void checkCharge(IReaction reaction) {
        int index = 0;
        for (IAtomContainer ac : 
            ReactionManipulator.getAllAtomContainers(reaction)) {
            for (IAtom atom : ac.atoms()) {
//                Double charge = atom.getCharge();
                Integer charge = atom.getFormalCharge();
                if (charge != null) {
                    System.out.println("Atom " + index + " has charge " + charge);
                }
                index++;
            }
        }
    }
    
    @Test
    public void testRxnFileA() throws FileNotFoundException, CDKException {
        String filename = "data/CanonicalTest.rxn";
        testFile(filename);
    }
    
    @Test
    public void testRxnFileB() throws FileNotFoundException, CDKException {
        String filename = "data/R01179.rxn";
        testFile(filename);
    }
    
    @Test
    public void testRxnFileCWithCharges() throws CDKException, IOException {
        String filename = "data/TestPOX.rxn";
//        testFile(filename);
        ICanonicalReactionLabeller reactionLabeller =
            new ChargedSignatureReactionCanoniser();
        writeCanonicalRxnFile(filename, "CHARGED", reactionLabeller);
    }
    
    @Test
    public void testRxnFileCNoCharges() throws CDKException, IOException {
        String filename = "data/TestPOX.rxn";
//        testFile(filename);
        ICanonicalReactionLabeller reactionLabeller =
            new SignatureReactionCanoniser();
        writeCanonicalRxnFile(filename, "UNCHARGED", reactionLabeller);
    }

}
