package labelling;

import java.util.Hashtable;
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
    
    private IReaction cloneReaction(IReaction reaction) throws CloneNotSupportedException {
        System.out.println("about to clone");
        Reaction clone = new Reaction();
        // clone the reactants, products and agents
        clone.setReactants((MoleculeSet)((MoleculeSet)reaction.getReactants()).clone());
//        clone.setAgents((MoleculeSet)((MoleculeSet)reaction.getAgents()).clone());
        clone.setProducts((MoleculeSet)((MoleculeSet)reaction.getProducts()).clone());
        
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
        
        // clone the maps
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
    
    public IReaction labelReaction(
            IReaction reaction, ICanonicalMoleculeLabeller labeller) {
        System.out.println("labelling");
        try {
//            IReaction canonReaction = (IReaction) reaction.clone();
            IReaction canonReaction = cloneReaction(reaction);
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
            return canonReaction;
        } catch (CloneNotSupportedException cns) {
            return null;
        }
    }

}
