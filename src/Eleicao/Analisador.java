package Eleicao;

import java.util.*;

public class Analisador {

    private Map<Integer, Map<String, Integer>> dadosDasUrnas;
    private Map<String, Integer> votosPorLegenda;

    public void setVotosPorLegenda(Map<String, Integer> votosPorLegenda) {
        this.votosPorLegenda = votosPorLegenda;
    }

    public Map<String, Integer> getVotosPorLegenda() {
        return votosPorLegenda;
    }

    public void setDadosDasUrnas(Map<Integer, Map<String, Integer>> dadosDasUrnas) {
        this.dadosDasUrnas = dadosDasUrnas;
    }

    public Map<Integer, Map<String, Integer>> getDadosDasUrnas() {
        return dadosDasUrnas;
    }

    public Map<String, Integer> processarVotos(List<String> linhas) {
        Map<Integer, Map<String, Integer>> contagemUrnas = new HashMap<>();
        int totalVotos = 0;
        int votosBrancos = 0;
        int votosNulos = 0;

        for (String linha : linhas) {
            String[] partes = linha.split(";");
            if (partes.length < 2) continue;

            int numeroUrna = Integer.parseInt(partes[0]);
            String codigo = partes[1];
            String tipoVoto = "";

            if (codigo.equals("1")) {
                tipoVoto = "Em Branco";
                votosBrancos++;
            } else if (codigo.equals("0")) {
                tipoVoto = "Nulo";
                votosNulos++;
            } else {
                tipoVoto = "Candidato " + codigo;
            }

            contagemUrnas.putIfAbsent(numeroUrna, new HashMap<>());
            Map<String, Integer> votosPorTipo = contagemUrnas.get(numeroUrna);
            votosPorTipo.put(tipoVoto, votosPorTipo.getOrDefault(tipoVoto, 0) + 1);

            setDadosDasUrnas(contagemUrnas);
            totalVotos++;
        }

        Map<String, Integer> totaisVotos = new HashMap<>();
        totaisVotos.put("Total de Votos", totalVotos);
        totaisVotos.put("Votos em Branco", votosBrancos);
        totaisVotos.put("Votos Nulos", votosNulos);

        return totaisVotos;
    }

    public static void calcularPercentuais(int totalVotosBranco, int totalVotosNulos, int totalVotos) {
        if (totalVotos == 0) {
            System.out.println("Total de votos é zero, não é possível calcular percentuais.");
            return;
        }

        double percentualBranco = (double) totalVotosBranco / totalVotos * 100;
        double percentualNulo = (double) totalVotosNulos / totalVotos * 100;
        double percentualValidos = (double) (totalVotos - (totalVotosNulos + totalVotosBranco)) / totalVotos * 100;

        System.out.println("\nPercentuais de votos:");
        System.out.println("Votos em Branco: " + String.format("%.2f", percentualBranco) + "%");
        System.out.println("Votos Nulos: " + String.format("%.2f", percentualNulo) + "%");
        System.out.println("Votos válidos: " + String.format("%.2f", percentualValidos) + "%");
    }

    public Map<String, Integer> VotosPorCandidato(Map<Integer, Map<String, Integer>> dadosDasUrnas) {
        Map<String, Integer> votosCandidatos = new HashMap<>();

        for (Map<String, Integer> votos : dadosDasUrnas.values()) {
            for (Map.Entry<String, Integer> entrada : votos.entrySet()) {
                String candidato = entrada.getKey();
                int qtdVotos = entrada.getValue();

                if (candidato.startsWith("Candidato")) {
                    votosCandidatos.put(candidato, votosCandidatos.getOrDefault(candidato, 0) + qtdVotos);
                }
            }
        }

        return votosCandidatos;
    }

    public List<String> obterTopCandidatos(List<String> linhas, int n) {
        Map<String, Integer> votosCandidatos = new HashMap<>();

        for (String linha : linhas) {
            String[] partes = linha.split(";");
            if (partes.length < 2) continue;

            String codigo = partes[1];
            if (!codigo.equals("0") && !codigo.equals("1")) {
                String candidato = "Candidato " + codigo;
                votosCandidatos.put(candidato, votosCandidatos.getOrDefault(candidato, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(votosCandidatos.entrySet());
        listaOrdenada.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> topCandidatos = new ArrayList<>();
        for (int i = 0; i < Math.min(n, listaOrdenada.size()); i++) {
            Map.Entry<String, Integer> candidato = listaOrdenada.get(i);
            topCandidatos.add(candidato.getKey() + " - " + candidato.getValue() + " votos");
        }

        return topCandidatos;
    }

    public Map<String, String> calcularVotosPorLegenda(List<String> linhas) {
        votosPorLegenda = new HashMap<>(); 
        int totalVotosValidos = 0;

        for (String linha : linhas) {
            String[] partes = linha.split(";");
            if (partes.length < 2) continue;

            String codigo = partes[1];
            if (codigo.equals("0") || codigo.equals("1")) continue;

            String legenda = codigo.substring(0, 2);
            votosPorLegenda.put(legenda, votosPorLegenda.getOrDefault(legenda, 0) + 1);
            totalVotosValidos++;
        }

        Map<String, String> resultado = new HashMap<>();
        for (String legenda : votosPorLegenda.keySet()) {
            int votos = votosPorLegenda.get(legenda);
            double percentual = (double) votos / totalVotosValidos * 100;
            resultado.put(legenda, votos + " votos (" + Math.round(percentual) + "%)");
        }

        return resultado;
    }

    public Map<String, Integer> calculo_Qp(int quocienteEleitoral) {
        Map<String, Integer> cadeiras = new HashMap<>();
        int vagas = 0;

        for (Map.Entry<String, Integer> partido : votosPorLegenda.entrySet()) {
            int qtdVotos = partido.getValue();
            int q_p = qtdVotos / quocienteEleitoral;

            if (q_p > 0) vagas += q_p;
            cadeiras.put(partido.getKey(), q_p);
        }

        while (vagas < 10) {
            double maiorProporcao = -1;
            String partidoEscolhido = null;

            for (Map.Entry<String, Integer> entry : cadeiras.entrySet()) {
                String partido = entry.getKey();
                double proporcaoAtual = (double) votosPorLegenda.get(partido) / (entry.getValue() + 1);

                if (proporcaoAtual > maiorProporcao) {
                    maiorProporcao = proporcaoAtual;
                    partidoEscolhido = partido;
                }
            }

            if (partidoEscolhido != null) {
                cadeiras.put(partidoEscolhido, cadeiras.get(partidoEscolhido) + 1);
                vagas++;
            }
        }

        return cadeiras;
    }

    public Map<String, Integer> Eleitos(Map<String, Integer> cadeiras, Map<String, Integer> votosPorCandidato, int quocienteEleitoral) {
        Map<String, Integer> candidatosEleitos = new HashMap<>();
        int limiteDesempenho = (int) Math.ceil(0.1 * quocienteEleitoral); // 10% do QE

        // Mapa auxiliar para agrupar candidatos por partido
        Map<String, List<Map.Entry<String, Integer>>> candidatosPorPartido = new HashMap<>();

        // Agrupa os candidatos por partido
        for (Map.Entry<String, Integer> entrada : votosPorCandidato.entrySet()) {
            String candidato = entrada.getKey();
            int votos = entrada.getValue();

            // Extrai o código do partido (os dois primeiros dígitos após "Candidato ")
            String[] partes = candidato.split(" ");
            if (partes.length < 2) continue; // Validação para evitar erros em nomes malformados

            String codigoCompleto = partes[1];
            String partido = codigoCompleto.substring(0, 2); // Os dois primeiros caracteres representam o partido

            // Apenas candidatos que atingem o limite de desempenho individual são considerados
            if (votos >= limiteDesempenho) {
                candidatosPorPartido.putIfAbsent(partido, new ArrayList<>());
                candidatosPorPartido.get(partido).add(entrada);
            } else {
                System.out.println(candidato + " não atingiu o limite de desempenho individual (votos: " + votos + ", necessário: " + limiteDesempenho + ")");
            }
        }

        // Ordena os candidatos dentro de cada partido pelos votos (do maior para o menor)
        for (Map.Entry<String, List<Map.Entry<String, Integer>>> entry : candidatosPorPartido.entrySet()) {
            entry.getValue().sort((a, b) -> b.getValue().compareTo(a.getValue())); // Ordena por votos decrescentes
        }

        // Distribui as cadeiras para os partidos
        for (Map.Entry<String, Integer> partido : cadeiras.entrySet()) {
            String legenda = partido.getKey();
            int cadeirasDisponiveis = partido.getValue();

            // Verifica se há candidatos no partido e distribui as cadeiras
            List<Map.Entry<String, Integer>> candidatos = candidatosPorPartido.get(legenda);
            if (candidatos != null) {
                for (int i = 0; i < Math.min(cadeirasDisponiveis, candidatos.size()); i++) {
                    Map.Entry<String, Integer> candidatoEleito = candidatos.get(i);
                    candidatosEleitos.put(candidatoEleito.getKey(), candidatoEleito.getValue());
                }
            } else {
                System.out.println("Partido " + legenda + " não tem candidatos elegíveis para preencher " + cadeirasDisponiveis + " cadeiras.");
            }
        }

        return candidatosEleitos;
    }
}
