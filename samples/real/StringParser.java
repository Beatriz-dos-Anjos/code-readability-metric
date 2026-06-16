// Expected score: 50-69
// Reason: Complex string parsing logic.
public class StringParser {
    public void processLine(String line) {
        int pointer = 0;
        char c;
        while (pointer < line.length()) {
            if ((c = line.charAt(pointer)) != ' ') {
                if (c == '"') {
                    while (pointer < line.length() && line.charAt(++pointer) != '"') {
                        System.out.println("String content");
                    }
                } else {
                    System.out.println("Other token");
                }
            }
            pointer++;
        }
    }
}
