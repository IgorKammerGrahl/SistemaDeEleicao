package Eleicao;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "C:/Users/igork/Eleicoes/Desktop/Eleicoes/src/votos.txt";
        int numeroCadeiras = 10;

        final int cadeiras = 10;
        
        Analisador analisador = new Analisador();
        
        Map<String, Integer> totaisVotos = analisador.processarVotos(caminhoArquivo);
        Map<String, Integer> votosPorCandidato;
        Map<String, Integer> cadeirasPorPartido;

        int totalVotos = totaisVotos.get("Total de Votos");
        int totalVotosBranco = totaisVotos.get("Votos em Branco");
        int totalVotosNulos = totaisVotos.get("Votos Nulos");
        int votoValidos = totalVotos - (totalVotosBranco + totalVotosNulos);

        System.out.println("Total de Votos: " + totalVotos);
        System.out.println("Votos em Branco: " + totalVotosBranco);
        System.out.println("Votos Nulos: " + totalVotosNulos);

        Analisador.calcularPercentuais(totalVotosBranco, totalVotosNulos, totalVotos);

        Map<Integer, Map<String, Integer>> dadosDaUrna = analisador.getDadosDasUrnas();
        System.out.println("Votos por candidato (consolidado):");
        votosPorCandidato = analisador.VotosPorCandidato(dadosDaUrna);


        System.out.println("Top 10 candidatos mais votados:");
        List<String> topCandidatos = analisador.obterTopCandidatos(caminhoArquivo, 10);

        for (String candidato : topCandidatos) {
            System.out.println(candidato); 
        }
        
        Map<String, String> votosPorLegenda = analisador.calcularVotosPorLegenda(caminhoArquivo);
        System.out.println("Votos por legenda:");
        for (Map.Entry<String, String> entrada : votosPorLegenda.entrySet()) {
            System.out.println("Legenda: " + entrada.getKey() + " - " + entrada.getValue());
        }
        
        // Quociente Eleitoral
        double quocienteEleitoral = analisador.calcularQuocienteEleitoral(caminhoArquivo, numeroCadeiras);
        System.out.println("Quociente Eleitoral: " + quocienteEleitoral);

        // Quociente Partidário
        Map<String, Integer> quocientePartidario = analisador.calcularQuocientePartidario(caminhoArquivo, numeroCadeiras);

        System.out.println("Quociente Partidário (Cadeiras por Partido):");
        for (Map.Entry<String, Integer> entry : quocientePartidario.entrySet()) {
            System.out.println("Legenda: " + entry.getKey() + " - Cadeiras: " + entry.getValue());
        }
    }
}
