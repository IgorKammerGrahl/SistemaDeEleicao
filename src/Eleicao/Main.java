package Eleicao;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "C:/Users/igork/Eleicoes/Desktop/Eleicoes/src/votos.txt";
        
        Map<String, Integer> totaisVotos = Analisador.processarVotos(caminhoArquivo);
        
        int totalVotos = totaisVotos.get("Total de Votos");
        int totalVotosBranco = totaisVotos.get("Votos em Branco");
        int totalVotosNulos = totaisVotos.get("Votos Nulos");

        System.out.println("Total de Votos: " + totalVotos);
        System.out.println("Votos em Branco: " + totalVotosBranco);
        System.out.println("Votos Nulos: " + totalVotosNulos);

        Analisador.calcularPercentuais(totalVotosBranco, totalVotosNulos, totalVotos);
    }
}