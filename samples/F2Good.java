// F2 — caso bom: atribuições separadas da lógica condicional. Score esperado: 100.
// Sem aninhamento profundo (max depth 2), sem linhas densas, sem params excessivos.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class F2Good {

    // depth: body=1, while=2 → max=2 ✅
    public void processFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String linha = reader.readLine();
        while (linha != null) {
            System.out.println(linha.trim());
            linha = reader.readLine();
        }
        reader.close();
    }

    // depth: body=1, for=2 → max=2 ✅ (ternary avoids if-inside-for)
    public int somarPositivos(int[] valores) {
        int soma = 0;
        for (int i = 0; i < valores.length; i++) {
            int v = valores[i];
            soma += (v > 0) ? v : 0;
        }
        return soma;
    }

    // depth: body=1, for=2 → max=2 ✅ (early return via flag, no if-inside-for)
    public String buscarPrimeiro(String[] itens, String prefixo) {
        String encontrado = null;
        for (int i = 0; i < itens.length; i++) {
            boolean comecaCom = itens[i].startsWith(prefixo);
            encontrado = comecaCom ? itens[i] : encontrado;
        }
        return encontrado;
    }

    // depth: body=1, if=2 → max=2 ✅
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
