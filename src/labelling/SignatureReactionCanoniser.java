package labelling;

import org.openscience.cdk.interfaces.IReaction;

public class SignatureReactionCanoniser 
    extends AbstractReactionLabeller implements ICanonicalReactionLabeller {
    
    private MoleculeSignatureLabellingAdaptor labeller =
        new MoleculeSignatureLabellingAdaptor();

    @Override
    public IReaction getCanonicalReaction(IReaction reaction) {
        return super.labelReaction(reaction, labeller); 
    }

}
