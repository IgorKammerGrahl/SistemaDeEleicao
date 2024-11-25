package Eleicao;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArquivoEleitoral {

    public List<String> lerArquivo(String caminhoArquivo) {
        try {
            return Files.readAllLines(Paths.get(caminhoArquivo));
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
