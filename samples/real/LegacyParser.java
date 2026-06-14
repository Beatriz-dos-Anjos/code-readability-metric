public class LegacyParser {
    public void parse(String text) {
        int len;
        if ((len = text.length()) > 0)
            for(int i=0; i<len; i++)
                if(text.charAt(i) == 'A')
                    System.out.println("Found");
    }
}
