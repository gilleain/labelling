package test_labelling;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IBond.Order;

public class SignatureReactionCanoniserTest {
    
    public int[] mappingToPermutation(Map<Integer, Integer> map) {
        int n = map.size();
        int[] permutation = new int[n];
        for (Integer key : map.keySet()) {
            permutation[key] = map.get(key);
        }
        return permutation;
    }
    
    
    // this is why we can't have nice things...
    public int indexFromContainers(List<IAtomContainer> containers, IAtom atom) {
        for (IAtomContainer container : containers) {
            int index = container.getAtomNumber(atom);
            if (index != -1) return index; 
        }
        return -1;
    }
    
    public void testMappingPermutation(String filename) throws FileNotFoundException, CDKException {
        IReaction reaction = ReactionTestUtility.getReaction(filename);
        testMappingPermutation(reaction);
    }
    
    public void testMappingPermutation(IReaction reaction) throws FileNotFoundException, CDKException {
        Map<IMapping, List<IMapping>> containerMappings = 
            ReactionTestUtility.getContainerContainerMap(reaction);
        for (IMapping containerMapping : containerMappings.keySet()) {
            Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
            IAtomContainer ac0 = (IAtomContainer)containerMapping.getChemObject(0);
            IAtomContainer ac1 = (IAtomContainer)containerMapping.getChemObject(1);
            for (IMapping atomMapping : containerMappings.get(containerMapping)) {
                IAtom a0 = (IAtom) atomMapping.getChemObject(0);
                IAtom a1 = (IAtom) atomMapping.getChemObject(1);
                
                // XXX - this assumes the mapping is not tangled!
                indexMap.put(ac0.getAtomNumber(a0), ac1.getAtomNumber(a1));
            }
            System.out.println(Arrays.toString(mappingToPermutation(indexMap)));
        }
    }
    
    
    @Test
    public void testMappingPermutationA() 
           throws FileNotFoundException, CDKException {
        String filename = "data/TestPOX.rxn";
       testMappingPermutation(filename);
    }
    
    @Test
    public void testMappingPermutationB() 
           throws FileNotFoundException, CDKException {
        String filename = "data/CanonicalTest.rxn";
       testMappingPermutation(filename);
    }
    
    @Test
    public void testCustomReaction() throws Exception {
        IMolecule cycloButaneA = new Molecule();
        cycloButaneA.addAtom(new Atom("C"));
        cycloButaneA.addAtom(new Atom("O"));
        cycloButaneA.addAtom(new Atom("C"));
        cycloButaneA.addAtom(new Atom("C"));
        cycloButaneA.addBond(0, 1, Order.SINGLE);
        cycloButaneA.addBond(0, 2, Order.SINGLE);
        cycloButaneA.addBond(1, 3, Order.SINGLE);
        cycloButaneA.addBond(2, 3, Order.SINGLE);
        
        IMolecule cycloButaneB = new Molecule();
        cycloButaneB.addAtom(new Atom("C"));
        cycloButaneB.addAtom(new Atom("O"));
        cycloButaneB.addAtom(new Atom("O"));
        cycloButaneB.addAtom(new Atom("C"));
        cycloButaneB.addBond(0, 2, Order.SINGLE);
        cycloButaneB.addBond(0, 3, Order.SINGLE);
        cycloButaneB.addBond(1, 2, Order.SINGLE);
        cycloButaneB.addBond(1, 3, Order.SINGLE);
        
        IMolecule fusedAB = new Molecule();
        fusedAB.addAtom(new Atom("C"));
        fusedAB.addAtom(new Atom("O"));
        fusedAB.addAtom(new Atom("C"));
        fusedAB.addAtom(new Atom("C"));
        
        fusedAB.addAtom(new Atom("C"));
        fusedAB.addAtom(new Atom("O"));
        fusedAB.addAtom(new Atom("O"));
        fusedAB.addAtom(new Atom("C"));
        
        fusedAB.addBond(0, 1, Order.SINGLE);
        fusedAB.addBond(0, 2, Order.SINGLE);
        fusedAB.addBond(1, 3, Order.SINGLE);
        fusedAB.addBond(2, 3, Order.SINGLE);
        
        fusedAB.addBond(3, 4, Order.SINGLE);  // the new bond
        
        fusedAB.addBond(4, 6, Order.SINGLE);
        fusedAB.addBond(4, 7, Order.SINGLE);
        fusedAB.addBond(5, 6, Order.SINGLE);
        fusedAB.addBond(5, 7, Order.SINGLE);
        
        IReaction reaction = new Reaction();
        reaction.addReactant(cycloButaneA);
        reaction.addReactant(cycloButaneB);
        reaction.addProduct(fusedAB);
        
        int n = cycloButaneA.getAtomCount();
        for (int index = 0; index < n; index++) {
            IAtom cycloButaneAAtom = cycloButaneA.getAtom(index);
            IAtom fusedAtom = fusedAB.getAtom(index);
            reaction.addMapping(new Mapping(cycloButaneAAtom, fusedAtom));
        }
        
        int m = cycloButaneA.getAtomCount();
        for (int index = 0; index < m; index++) {
            IAtom cycloButaneBAtom = cycloButaneB.getAtom(index);
            IAtom fusedAtom = fusedAB.getAtom(index + n);
            reaction.addMapping(new Mapping(cycloButaneBAtom, fusedAtom));
        }
        
//        ReactionTestUtility.printReactionToStdout(reaction);
        testMappingPermutation(reaction);
    }


}
