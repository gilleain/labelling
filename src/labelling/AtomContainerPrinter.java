package labelling;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomContainerPrinter {
    
    public static String toString(IAtomContainer atomContainer) {
        StringBuffer sb = new StringBuffer();
        for (IAtom atom : atomContainer.atoms()) {
            sb.append(atom.getSymbol());
        }
        sb.append(" ");
        int bondsLeft = atomContainer.getBondCount();
        for (IBond bond : atomContainer.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            int a0N = atomContainer.getAtomNumber(a0);
            int a1N = atomContainer.getAtomNumber(a1);
            if (a0N < a1N) {
                sb.append(a0N).append(":").append(a1N);
            } else {
                sb.append(a1N).append(":").append(a0N);   
            }
            
            bondsLeft--;
            if (bondsLeft > 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
