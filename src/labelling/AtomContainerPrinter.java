package labelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

public class AtomContainerPrinter {
    
    private class Edge implements Comparable<Edge> {
        public String firstString;
        public String lastString;
        public int first;
        public int last;
        public int order;
        public Edge(int first, int last, int order, String firstString, String lastString) {
            this.first = first;
            this.last = last;
            this.order = order;
            this.firstString = firstString;
            this.lastString = lastString;
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
            return firstString + first + ":" + lastString + last + "(" + order + ")";
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
            String a0S = a0.getSymbol();
            String a1S = a1.getSymbol();
            int o = bond.getOrder().ordinal();
            if (a0N < a1N) {
                edges.add(new Edge(a0N, a1N, o, a0S, a1S));
            } else {
                edges.add(new Edge(a1N, a0N, o, a1S, a0S));
            }
        }
        Collections.sort(edges);
        sb.append(edges.toString());
        return sb.toString();
    }

}
