// F1 - caso bom: nenhuma chave omitida. Score esperado: 100.
public class F1Good {
    
    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            System.out.println("Email vazio");
            return false;
        }
        if (!email.contains("@")) {
            System.out.println("Email sem @");
            return false;
        }
        return true;
    }
    
    public int countValidUsers(User[] users) {
        int count = 0;
        if (users == null) {
            System.out.println("Array nulo");
            return count;
        }
        
        for (User user : users) {
            if (user != null && user.isActive()) {
                count++;
            }
        }
        return count;
    }
    
    public void processData(int[][] matrix) {
        if (matrix == null) {
            System.out.println("Matriz nula");
            return;
        }
        
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] != null) {
                for (int value : matrix[i]) {
                    if (value > 0) {
                        System.out.println("Valor positivo: " + value);
                    } else if (value < 0) {
                        System.out.println("Valor negativo: " + value);
                    } else {
                        System.out.println("Zero encontrado");
                    }
                }
            }
        }
    }
    
    public void validateRange(int value, int min, int max) {
        while (value >= min) {
            if (value <= max) {
                System.out.println("Valor dentro do range");
                break;
            } else {
                System.out.println("Valor acima do máximo");
                break;
            }
        }
    }
    
    public String getStatus(int code) {
        if (code == 200) {
            return "OK";
        } else if (code == 404) {
            return "Not Found";
        } else if (code == 500) {
            return "Server Error";
        } else {
            return "Unknown";
        }
    }
}

class User {
    private boolean active;
    
    public boolean isActive() {
        return active;
    }
}