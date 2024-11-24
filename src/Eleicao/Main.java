package Eleicao;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "C:/Users/igork/Eleicoes/Desktop/Eleicoes/src/votos.txt";

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

        int q_e = analisador.calculo_Qe(votoValidos, cadeiras);
        cadeirasPorPartido = analisador.calculo_Qp(q_e);


        analisador.Eleitos(cadeirasPorPartido, votosPorCandidato, q_e);
    }
}
