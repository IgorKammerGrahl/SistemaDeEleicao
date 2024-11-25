package Eleicao;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "C:/Users/igork/Eleicoes/Desktop/Eleicoes/src/votos.txt";
        int numeroCadeiras = 10;

        // Instancia os objetos necessários
        ArquivoEleitoral arquivoEleitoral = new ArquivoEleitoral();
        Analisador analisador = new Analisador();

        // Lê o arquivo e obtém as linhas
        List<String> linhas = arquivoEleitoral.lerArquivo(caminhoArquivo);

        // Processa os votos e obtém totais
        Map<String, Integer> totaisVotos = analisador.processarVotos(linhas);
        int totalVotos = totaisVotos.get("Total de Votos");
        int totalVotosBranco = totaisVotos.get("Votos em Branco");
        int totalVotosNulos = totaisVotos.get("Votos Nulos");
        int votosValidos = totalVotos - (totalVotosBranco + totalVotosNulos);

        // Exibe os totais
        System.out.println("Total de Votos: " + totalVotos);
        System.out.println("Votos em Branco: " + totalVotosBranco);
        System.out.println("Votos Nulos: " + totalVotosNulos);

        // Calcula e exibe os percentuais
        Analisador.calcularPercentuais(totalVotosBranco, totalVotosNulos, totalVotos);

        // Consolida os votos por candidato
        Map<Integer, Map<String, Integer>> dadosDaUrna = analisador.getDadosDasUrnas();
        Map<String, Integer> votosPorCandidato = analisador.VotosPorCandidato(dadosDaUrna);
        System.out.println("\nVotos por candidato (consolidado):");
        votosPorCandidato.forEach((candidato, votos) -> 
            System.out.println(candidato + ": " + votos + " votos")
        );

        // Obtém e exibe os top 10 candidatos mais votados
        List<String> topCandidatos = analisador.obterTopCandidatos(linhas, 10);
        System.out.println("\nTop 10 candidatos mais votados:");
        topCandidatos.forEach(System.out::println);

        // Calcula votos por legenda
        Map<String, String> votosPorLegenda = analisador.calcularVotosPorLegenda(linhas);
        System.out.println("\nVotos por legenda:");
        votosPorLegenda.forEach((legenda, detalhes) -> 
            System.out.println("Legenda: " + legenda + " - " + detalhes)
        );

        // Calcula o quociente eleitoral
        double quocienteEleitoral = (double) votosValidos / numeroCadeiras;
        System.out.println("\nQuociente Eleitoral: " + quocienteEleitoral);

        // Calcula o quociente partidário
        Map<String, Integer> cadeirasPorPartido = analisador.calculo_Qp((int) quocienteEleitoral);
        System.out.println("\nQuociente Partidário (Cadeiras por Partido):");
        cadeirasPorPartido.forEach((partido, cadeiras) -> 
            System.out.println("Legenda: " + partido + " - Cadeiras: " + cadeiras)
        );

     // Determina os candidatos eleitos com base no quociente eleitoral e no limite de desempenho
        System.out.println("\nCandidatos Eleitos (com cláusula de desempenho individual):");
        Map<String, Integer> eleitos = analisador.Eleitos(cadeirasPorPartido, votosPorCandidato, (int) quocienteEleitoral);
        eleitos.forEach((candidato, votos) -> 
            System.out.println(candidato + " - " + votos + " votos")
        );
    }
}
