// F2 - caso bom: atribuições separadas da lógica condicional. Score esperado: 100.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class F2Good {

    public void processFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String linha = reader.readLine();
        while (linha != null) {
            String resultado = processar(linha);
            if (resultado != null && !resultado.isEmpty()) {
                System.out.println(resultado);
            }
            linha = reader.readLine();
        }
        reader.close();
    }

    private String processar(String linha) {
        return linha.trim();
    }

    public int somarPositivos(int[] valores) {
        int soma = 0;
        for (int i = 0; i < valores.length; i++) {
            int v = valores[i];
            if (v > 0) {
                soma += v;
            }
        }
        return soma;
    }

    public String buscarPrimeiro(String[] itens, String prefixo) {
        for (int i = 0; i < itens.length; i++) {
            boolean comecaCom = itens[i].startsWith(prefixo);
            if (comecaCom) {
                return itens[i];
            }
        }
        return null;
    }
}
