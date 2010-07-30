package test_labelling;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;

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


}
