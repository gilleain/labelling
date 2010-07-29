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
    
    public IReaction labelReaction(
            IReaction reaction, ICanonicalMoleculeLabeller labeller) {
        IReaction canonReaction = new Reaction();
        IMoleculeSet canonicalProducts = new MoleculeSet();
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            IAtomContainer canonicalForm = 
                labeller.getCanonicalMolecule(product);
            for (IAtom a : canonicalForm.atoms()) { 
                String v = (String) a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                if (v != null) {
                    a.setProperty(
                            CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
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
                    a.setProperty(
                            CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
                }
             }
            canonicalReactants.addMolecule(
                    canonicalForm.getBuilder().newInstance(
                            IMolecule.class, canonicalForm));
        }
        canonReaction.setProducts(canonicalProducts);
        canonReaction.setReactants(canonicalReactants);
        return canonReaction; 
    }

}
