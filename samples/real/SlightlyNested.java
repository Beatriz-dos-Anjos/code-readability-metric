// Expected score: 70-89
// Reason: Penalized for having somewhat nested control structures.
public class SlightlyNested {
    public void processItems(List<String> items) {
        for (String item : items) {
            if (item != null) {
                if (item.length() > 5) {
                    if (item.startsWith("A")) {
                        System.out.println("Item found: " + item);
                    }
                }
            }
        }
    }
}
