package labelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomContainerPrinter {
    
    private class Edge implements Comparable<Edge> {
        public int first;
        public int last;
        public Edge(int first, int last) {
            this.first = first;
            this.last = last;
        }
        
        @Override
        public int compareTo(Edge o) {
            if (first < o.first || (first == o.first && last < o.last)) {
                return -1;
            } else {
                return 1;
            }
        }
        
        public String toString() {
            return first + ":" + last;
        }
    }
    
    public String toString(IAtomContainer atomContainer) {
        StringBuffer sb = new StringBuffer();
        for (IAtom atom : atomContainer.atoms()) {
            sb.append(atom.getSymbol());
        }
        sb.append(" ");
        List<Edge> edges = new ArrayList<Edge>();
        for (IBond bond : atomContainer.bonds()) {
            IAtom a0 = bond.getAtom(0);
            IAtom a1 = bond.getAtom(1);
            int a0N = atomContainer.getAtomNumber(a0);
            int a1N = atomContainer.getAtomNumber(a1);
            if (a0N < a1N) {
                edges.add(new Edge(a0N, a1N));
            } else {
                edges.add(new Edge(a1N, a0N));
            }
        }
        Collections.sort(edges);
        sb.append(edges.toString());
        return sb.toString();
    }

}
