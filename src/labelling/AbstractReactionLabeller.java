package labelling;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
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
    
    public IReaction labelReaction(
            IReaction reaction, ICanonicalMoleculeLabeller labeller) {
        try {
            IReaction canonReaction = (IReaction) reaction.clone();
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
