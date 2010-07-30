package test_labelling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.Mapping;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

public class ReactionTestUtility {
    
    private static boolean useCDK = false;
    
    public static IReaction getReaction(String filename) throws
        FileNotFoundException, CDKException {
        if (useCDK) {
            return useCDK(filename);
        } else {
            return useReactionBlast(filename);
        }
    }
    
    
    /**
     * Return a multi-level association between (at the top level) atom
     * containers and (at the lower level) atoms in those containers. There may
     * be multiple IMapping objects that refer to the same container; for 
     * example the reaction A -> B + C will have a mapping (A, B) and (A, C).
     * 
     * @param reaction
     * @return
     */
    public static Map<IMapping, List<IMapping>> getContainerContainerMap(
            IReaction reaction) {
        
        // the multi-level association between atom containers and atoms
        Map<IMapping, List<IMapping>> containerContainerMap = 
            new HashMap<IMapping, List<IMapping>>();
        
        for (IMapping atomMapping : reaction.mappings()) {
            Set<IMapping> containerMappings = containerContainerMap.keySet();
            IMapping containerMapping;
            List<IMapping> atomMappings;
            
            containerMapping = findContainerMapping(atomMapping, containerMappings);
            
            // make a new container-container map if one does not yet exist
            if (containerMapping == null) {
                IAtom atom0 = (IAtom) atomMapping.getChemObject(0);
                IAtom atom1 = (IAtom) atomMapping.getChemObject(1);

                IAtomContainer ac0 = 
                    ReactionManipulator.getRelevantAtomContainer(reaction, atom0);
                IAtomContainer ac1 = 
                    ReactionManipulator.getRelevantAtomContainer(reaction, atom1);
                containerMapping = new Mapping(ac0, ac1);
                atomMappings = new ArrayList<IMapping>(); 
                containerContainerMap.put(containerMapping, atomMappings);
            } else {
                atomMappings = containerContainerMap.get(containerMapping);
            }
            atomMappings.add(atomMapping);
        }
        return containerContainerMap;
    }
    
    private static IMapping findContainerMapping(
            IMapping atomMapping, Set<IMapping> containerMappings) {
        IAtom atom0 = (IAtom) atomMapping.getChemObject(0);
        IAtom atom1 = (IAtom) atomMapping.getChemObject(1);
        
        for (IMapping containerMapping : containerMappings) {
            IAtomContainer ac0 = (IAtomContainer) containerMapping.getChemObject(0);
            IAtomContainer ac1 = (IAtomContainer) containerMapping.getChemObject(1);
            if ((ac0.contains(atom0) && ac1.contains(atom1))
                    || (ac0.contains(atom1) && ac1.contains(atom0))) {
                return containerMapping;
            }
        }
        return null;
    }
    
    private static IReaction useReactionBlast(String filename) 
        throws FileNotFoundException, CDKException {
        org.openscience.reactionblast.tools.rxnfile.MDLRXNV2000Reader reader= 
            new org.openscience.reactionblast.tools.rxnfile.MDLRXNV2000Reader(
                    new FileReader(filename));
        IReactionSet reactionSet = (IReactionSet) reader.read(new ReactionSet());
        return reactionSet.getReaction(0);
    }

    private static IReaction useCDK(String filename) 
        throws FileNotFoundException, CDKException {
        org.openscience.cdk.io.MDLRXNReader reader = 
            new org.openscience.cdk.io.MDLRXNReader(new FileReader(filename));
        IReactionSet reactionSet = (IReactionSet) reader.read(new ReactionSet());
        return reactionSet.getReaction(0);    
    }
}
