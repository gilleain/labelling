package labelling;

import org.openscience.cdk.interfaces.IReaction;

public interface ICanonicalReactionLabeller {
    
    /**
     * Convert a reaction into a canonical form by canonizing each of the
     * structures in the reaction in turn.
     * 
     * @param reaction
     * @return
     */
    public IReaction getCanonicalReaction(IReaction reaction);

}
