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
