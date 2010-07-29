package labelling;

import org.openscience.cdk.interfaces.IReaction;

public class SmilesReactionCanoniser 
    extends AbstractReactionLabeller implements ICanonicalReactionLabeller {
    
    private CanonicalLabellingAdaptor labeller = new CanonicalLabellingAdaptor();

    @Override
    public IReaction getCanonicalReaction(IReaction reaction) {
        return super.labelReaction(reaction, labeller); 
    }

}
