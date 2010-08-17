package mapping;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

import signature.SymmetryClass;

public class Height1SignatureMapper {
    
    private class OrbitMapping {
        
    }
    
    private List<OrbitMapping> map;
    
    public Height1SignatureMapper() {
        map = new ArrayList<OrbitMapping>();
    }
    
    public void makeMapping(IAtomContainer a, IAtomContainer b) {
        int height = 1;
        MoleculeSignature signatureForA = new MoleculeSignature(a);
        List<SymmetryClass> orbitsForA = signatureForA.getSymmetryClasses(height);
        
        MoleculeSignature signatureForB = new MoleculeSignature(b);
        List<SymmetryClass> orbitsForB = signatureForA.getSymmetryClasses(height);
        
        List<SymmetryClass> unmatchedB = new ArrayList<SymmetryClass>();
        unmatchedB.addAll(orbitsForB);
        for (SymmetryClass orbitA : orbitsForA) {
            
        }
    }

}
