// Expected score: 50-69
// Reason: Legacy code with somewhat confusing logic and structure.
public class LegacyParser {
    public void parse(String text) {
        int len;
        if ((len = text.length()) > 0)
            for(int i=0; i<len; i++)
                if(text.charAt(i) == 'A')
                    System.out.println("Found");
    }
}
