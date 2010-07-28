package labelling;

import java.util.Iterator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

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
    
    public static IAtomContainer permute(int[] p, IAtomContainer atomContainer) {
        IAtomContainer permutedContainer = null;
        try {
            permutedContainer = 
                atomContainer.getBuilder().newInstance(IAtomContainer.class);
            for (int i = 0; i < p.length; i++) {
                IAtom atom = atomContainer.getAtom(p[i]);
                permutedContainer.addAtom((IAtom) atom.clone());
            }
            for (IBond bond : atomContainer.bonds()) {
                IBond clonedBond = (IBond) bond.clone();
                clonedBond.setAtoms(new IAtom[clonedBond.getAtomCount()]);
                int i = 0;
                for (IAtom atom : bond.atoms()) {
                    int index = atomContainer.getAtomNumber(atom);
                    IAtom permutedAtom = permutedContainer.getAtom(p[index]);
                    clonedBond.setAtom(permutedAtom, i++);
                }
                permutedContainer.addBond(clonedBond);
            }

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
