package labelling;

import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class AtomContainerAtomPermutor extends Permutor 
    implements Iterator<IAtomContainer>{

    private IAtomContainer original;
    
    public AtomContainerAtomPermutor(IAtomContainer atomContainer) {
        super(atomContainer.getAtomCount());
        original = atomContainer;
    }
    
    public IAtomContainer next() {
        int[] p = super.getNextPermutation();
        return AtomContainerAtomPermutor.permute(p, original);
    }
    
    public static IAtomContainer permute(
            int[] permutation, IAtomContainer atomContainer) {
        IAtomContainer permutedContainer = null;
        try {
            permutedContainer = (IAtomContainer) atomContainer.clone();
            IAtom[] newOrder = new IAtom[permutation.length];
            int n = atomContainer.getAtomCount();
            for (int originalIndex = 0; originalIndex < n; originalIndex++) {
                int newIndex = permutation[originalIndex];
                IAtom atom = permutedContainer.getAtom(originalIndex);
                newOrder[newIndex] = atom;
            }
            permutedContainer.setAtoms(newOrder);
        } catch (CloneNotSupportedException cne) {
            //?
            System.out.println(cne);
        }
    
        return permutedContainer;
    }

   
    @Override
    public void remove() {
        // can just increase rank....
    }

}
