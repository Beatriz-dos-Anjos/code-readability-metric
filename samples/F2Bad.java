
// F2 - bad case: assignments embedded in conditionals
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class F2Bad {

    private int erros = 0;
    private int limite = 3;

    public void processFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String linha;
        while ((linha = reader.readLine()) != null) {
            String resultado;
            if ((resultado = processar(linha)) != null) {
                System.out.println(resultado);
            }
            if (++erros > limite) {
                System.out.println("Muitos erros, reiniciando contador.");
                erros = 0;
            }
        }
        reader.close();
    }

    private String processar(String linha) {
        return linha.trim().isEmpty() ? null : linha.trim();
    }

    public int buscar(String[] dados, String alvo) {
        int index = -1;
        while (++index < dados.length) {
            if (dados[index].equals(alvo)) {
                return index;
            }
        }
        return -1;
    }

    public boolean validar(int x) {
        int temp;
        if ((temp = calcular(x)) > 0) {
            return temp < 100;
        }
        return false;
    }

    private int calcular(int x) {
        return x * 2;
    }
}
