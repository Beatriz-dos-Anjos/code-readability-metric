// Expected score: 50-69
// Reason: Deeply nested validation logic reduces readability.
public class DeepValidator {
    public boolean isValid(int x) {
        boolean valid = false;
        if (x > 0)
            if (x < 100)
                if (x % 2 == 0)
                    if (x % 3 != 0)
                        if (x != 50)
                            if (x != 42)
                                valid = true;
        return valid;
    }
}
