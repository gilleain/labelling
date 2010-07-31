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
            IChemObject keyChemObj1 = mapping.getChemObject(1);
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
        Map<IAtom, IAtom> atomAtom = new Hashtable<IAtom, IAtom>();
        IMoleculeSet reactants = reaction.getReactants();
        IMoleculeSet clonedReactants = clone.getReactants();
        for (int i = 0; i < reactants.getMoleculeCount(); ++i) {
            IMolecule mol = reactants.getMolecule(i);
            IMolecule mol2 = clonedReactants.getMolecule(i);
            for (int j = 0; j < mol.getAtomCount(); ++j) {
                atomAtom.put(mol.getAtom(j), mol2.getAtom(j));
            }
        }
        IMoleculeSet products = reaction.getProducts();
        IMoleculeSet clonedProducts = clone.getProducts();
        for (int i = 0; i < products.getMoleculeCount(); ++i) {
            IMolecule mol = products.getMolecule(i);
            IMolecule mol2 = clonedProducts.getMolecule(i);
            for (int j = 0; j < mol.getAtomCount(); ++j) {
                atomAtom.put(mol.getAtom(j), mol2.getAtom(j));
            }
        }
        
        return atomAtom;
    }
    
    private List<IMapping> cloneMappings(IReaction reaction, Map<IAtom, IAtom> atomAtomMap) {
        // clone the mappings
        int numberOfMappings = reaction.getMappingCount();
        List<IMapping> map = new ArrayList<IMapping>();
        for (int mappingIndex = 0; mappingIndex < numberOfMappings; mappingIndex++) {
            IMapping mapping = reaction.getMapping(mappingIndex);
            IChemObject keyChemObj0 = mapping.getChemObject(0);
            IChemObject keyChemObj1 = mapping.getChemObject(1);
            IChemObject co0 = (IChemObject) atomAtomMap.get(keyChemObj0);
            IChemObject co1 = (IChemObject) atomAtomMap.get(keyChemObj1);
            map.add(new Mapping(co0, co1));
        }
        return map;
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
        
        Map<IAtom, IAtom> atomAtomMap = atomAtomMap(reaction, copyOfReaction);
        List<IMapping> map = cloneMappings(reaction, atomAtomMap);
        
        Comparator<IMapping> mappingSorter = new Comparator<IMapping>() {

            @Override
            public int compare(IMapping o1, IMapping o2) {
                IChemObject o10 = o1.getChemObject(0);
                IChemObject o20 = o2.getChemObject(0);
                return indexMap.get(o10).compareTo(indexMap.get(o20));
            }
            
        };
        Collections.sort(map, mappingSorter);
        int mappingIndex = 0;
        for (IMapping mapping : map) {
            mapping.getChemObject(0).setProperty(
                    CDKConstants.ATOM_ATOM_MAPPING, mappingIndex);
            mapping.getChemObject(1).setProperty(
                    CDKConstants.ATOM_ATOM_MAPPING, mappingIndex);
            copyOfReaction.addMapping(mapping);
            mappingIndex++;
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
