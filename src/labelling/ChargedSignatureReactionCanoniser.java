package labelling;

import org.openscience.cdk.interfaces.IReaction;

public class ChargedSignatureReactionCanoniser 
       extends AbstractReactionLabeller implements ICanonicalReactionLabeller {
    
    private ChargedMoleculeSignatureLabellingAdaptor labeller =
        new ChargedMoleculeSignatureLabellingAdaptor();

    @Override
    public IReaction getCanonicalReaction(IReaction reaction) {
        return super.labelReaction(reaction, labeller); 
    }
}
