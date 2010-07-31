package labelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

public class AbstractReactionLabeller {
    
    /**
     * A nasty hack necessary to get around a bug in the CDK
     */
    private boolean fixAtomMappingCastType = false;
    
    private void fixAtomMapping(IAtomContainer canonicalForm) {
        for (IAtom a : canonicalForm.atoms()) { 
            String v = (String) a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
            if (v != null) {
                a.setProperty(
                        CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
            }
        }
    }
    
    private IReaction cloneReactionWithoutMappings(
            IReaction reaction) throws CloneNotSupportedException {
        Reaction clone = new Reaction();
        // clone the reactants, products and agents
        clone.setReactants((MoleculeSet)((MoleculeSet)reaction.getReactants()).clone());
//        clone.setAgents((MoleculeSet)((MoleculeSet)reaction.getAgents()).clone());
        clone.setProducts((MoleculeSet)((MoleculeSet)reaction.getProducts()).clone());
        
        return clone;
    }
    
    private IReaction cloneReaction(IReaction reaction) throws CloneNotSupportedException {
        System.out.println("about to clone");
        IReaction clone = cloneReactionWithoutMappings(reaction);
        Map<IAtom, IAtom> atomatom = atomAtomMap(reaction, clone);
        
        // clone the maps - in the original code this gives mappings with
        // a null second chemobject; no idea why. Interfaces?
        int m = reaction.getMappingCount();
        IMapping[] map = new Mapping[m];
        for (int f = 0; f < reaction.getMappingCount(); f++) {
            IMapping mapping = reaction.getMapping(f);
            IChemObject keyChemObj0 = mapping.getChemObject(0);
            IChemObject keyChemObj1 = mapping.getChemObject(0);
            ChemObject co0 = (ChemObject)atomatom.get(keyChemObj0);
            ChemObject co1 = (ChemObject)atomatom.get(keyChemObj1);
            map[f] = new Mapping(co0, co1);
        }
        for (IMapping mapping : map) { clone.addMapping(mapping); }
        return clone;
    }
    
    private Map<IAtom, IAtom> atomAtomMap(IReaction reaction, IReaction clone) {
     // create a Map of corresponding atoms for molecules (key: original Atom, 
        // value: clone Atom)
        Map<IAtom, IAtom> atomatom = new Hashtable<IAtom, IAtom>();
        for (int i = 0; i < reaction.getReactants().getMoleculeCount(); ++i) {
            IMolecule mol = reaction.getReactants().getMolecule(i);
            IMolecule mol2 = clone.getReactants().getMolecule(i);
            for (int j = 0; j < mol.getAtomCount(); ++j) {
                atomatom.put(mol.getAtom(j), mol2.getAtom(j));
            }
        }
        return atomatom;
    }
    
    /**
     * Clone and Sort the mappings based on the order of the first object 
     * in the mapping (which is assumed to be the reactant).
     * 
     * @param reaction
     */
    private void cloneAndSortMappings(IReaction reaction, IReaction copyOfReaction) {
        
        // make a lookup for the indices of the atoms in the copy
        final Map<IChemObject, Integer> indexMap = 
            new HashMap<IChemObject, Integer>();
        List<IAtomContainer> all = 
            ReactionManipulator.getAllAtomContainers(copyOfReaction);
        int globalIndex = 0;
        for (IAtomContainer ac : all) { 
            for (IAtom atom : ac.atoms()) {
                indexMap.put(atom, globalIndex);
                globalIndex++;
            }
        }
        Map<IAtom, IAtom> atomatom = atomAtomMap(reaction, copyOfReaction);
        
        // clone the mappings
        int numberOfMappings = reaction.getMappingCount();
        List<IMapping> map = new ArrayList<IMapping>();
        for (int mappingIndex = 0; mappingIndex < numberOfMappings; mappingIndex++) {
            IMapping mapping = reaction.getMapping(mappingIndex);
            IChemObject keyChemObj0 = mapping.getChemObject(0);
            IChemObject keyChemObj1 = mapping.getChemObject(0);
            IChemObject co0 = (IChemObject)atomatom.get(keyChemObj0);
            IChemObject co1 = (IChemObject)atomatom.get(keyChemObj1);
            map.add(new Mapping(co0, co1));
        }
        
        Comparator<IMapping> mappingSorter = new Comparator<IMapping>() {

            @Override
            public int compare(IMapping o1, IMapping o2) {
                IChemObject o10 = o1.getChemObject(0);
                IChemObject o20 = o2.getChemObject(0);
                return indexMap.get(o10).compareTo(indexMap.get(o20));
            }
            
        };
        Collections.sort(map, mappingSorter);
        for (IMapping mapping : map) {
            copyOfReaction.addMapping(mapping);
        }
        
    }
    
    public IReaction labelReaction(
            IReaction reaction, ICanonicalMoleculeLabeller labeller) {
        System.out.println("labelling");
        IReaction canonReaction = new Reaction();
        IMoleculeSet canonicalProducts = new MoleculeSet();
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(product);
            if (fixAtomMappingCastType) { fixAtomMapping(canonicalForm); }
            canonicalProducts.addMolecule(
                    canonicalForm.getBuilder().newInstance(
                            IMolecule.class, canonicalForm));
        }
        IMoleculeSet canonicalReactants = new MoleculeSet();
        for (IAtomContainer reactant: reaction.getReactants().atomContainers()) {
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(reactant);
            if (fixAtomMappingCastType) { fixAtomMapping(canonicalForm); }
            canonicalReactants.addMolecule(
                    canonicalForm.getBuilder().newInstance(
                            IMolecule.class, canonicalForm));
        }
        canonReaction.setProducts(canonicalProducts);
        canonReaction.setReactants(canonicalReactants);
        cloneAndSortMappings(reaction, canonReaction);
        return canonReaction;
    }

}
