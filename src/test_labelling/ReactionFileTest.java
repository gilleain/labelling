package test_labelling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import labelling.AtomContainerPrinter;
import labelling.ICanonicalLabeller;
import labelling.MoleculeSignatureLabellingAdaptor;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class ReactionFileTest {
    
    private ICanonicalLabeller labeller = 
        new MoleculeSignatureLabellingAdaptor();
    
    public IReaction getReaction(String filename) throws
    FileNotFoundException, CDKException {
        MDLRXNReader reader = new MDLRXNReader(new FileReader(filename));
        IReactionSet reactionSet = (IReactionSet) reader.read(new ReactionSet());
        return reactionSet.getReaction(0);
    }
    
    public void testFile(String filename) throws
            FileNotFoundException, CDKException {
        IReaction reaction = getReaction(filename);
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
    
    public void writeCanonicalRxnFile(String filename) throws CDKException, IOException {
        IReaction reaction = getReaction(filename);
        IReaction canonReaction = new Reaction();
        IMoleculeSet canonicalProducts = new MoleculeSet();
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(product);
            for (IAtom a : canonicalForm.atoms()) { 
                String v = (String) a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (v != null) {
                    a.setProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
                }
            }
            canonicalProducts.addMolecule(
                    canonicalForm.getBuilder().newInstance(
                            IMolecule.class, canonicalForm));
        }
        IMoleculeSet canonicalReactants = new MoleculeSet();
        for (IAtomContainer reactant: reaction.getReactants().atomContainers()) {
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(reactant);
            for (IAtom a : canonicalForm.atoms()) {
                String v = (String) a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (v != null) {
                    a.setProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
                }
             }
            canonicalReactants.addMolecule(
                    canonicalForm.getBuilder().newInstance(
                            IMolecule.class, canonicalForm));
        }
        canonReaction.setProducts(canonicalProducts);
        canonReaction.setReactants(canonicalReactants);
//        reaction.setProducts(canonicalProducts);
//        reaction.setProducts(canonicalReactants);
        String file_root = filename.substring(0, filename.indexOf("."));
        String outfile = file_root + "canonical.rxn";
        FileWriter writer = new FileWriter(outfile); 
        MDLRXNWriter rxnWriter = new MDLRXNWriter(writer);
//        MDLRXNWriter rxnWriter = new MDLRXNWriter(System.out);
        rxnWriter.write(canonReaction);
//        rxnWriter.write(reaction);
        rxnWriter.close();
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
    public void testRxnFileC() throws CDKException, IOException {
        String filename = "data/TestPOX.rxn";
//        testFile(filename);
        writeCanonicalRxnFile(filename);
    }

}
