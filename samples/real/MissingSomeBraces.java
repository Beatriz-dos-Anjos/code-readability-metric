// Expected score: 70-89
// Reason: Penalized for missing curly braces on single-line conditionals.
public class MissingSomeBraces {
    public void check(boolean a, boolean b) {
        if (a)
            System.out.println("A");
        
        if (b) {
            System.out.println("B");
        } else
            System.out.println("Not B");
    }
}
