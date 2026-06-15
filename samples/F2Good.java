
// F2 - good case: assignments are separated from conditional logic
// No deep nesting, no dense lines, no excessive parameters.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class F2Good {

    public void processFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String linha = reader.readLine();
        while (linha != null) {
            System.out.println(linha.trim());
            linha = reader.readLine();
        }
        reader.close();
    }

    public int somarPositivos(int[] valores) {
        int soma = 0;
        for (int i = 0; i < valores.length; i++) {
            int v = valores[i];
            soma += (v > 0) ? v : 0;
        }
        return soma;
    }

    public String buscarPrimeiro(String[] itens, String prefixo) {
        String encontrado = null;
        for (int i = 0; i < itens.length; i++) {
            boolean comecaCom = itens[i].startsWith(prefixo);
            encontrado = comecaCom ? itens[i] : encontrado;
        }
        return encontrado;
    }

    public boolean validarIdade(int idade) {
        if (idade < 0) {
            return false;
        }
        if (idade > 150) {
            return false;
        }
        return true;
    }
}
