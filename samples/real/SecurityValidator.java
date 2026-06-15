// Expected score: Below 50
// Reason: Huge and unreadable validation logic.
public class SecurityValidator {
    public boolean validateUserCredentials(String u, String p, String t, boolean m, int r, String i, String s, String c, int d) {
        boolean v = false;
        if (u != null)
            if (p != null)
                if (t != null)
                    if (m)
                        if (r > 0)
                            if (i != null)
                                if (s != null)
                                    if (c != null)
                                        if (d > 0)
                                            if ((v = u.length() > 5) == true)
                                                System.out.println("Valid " + u + p + t + m + r + i + s + c + d);
        return v;
    }
}
