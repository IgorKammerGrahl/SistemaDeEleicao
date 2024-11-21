package Eleicao;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analisador {

    private Map<Integer, Map<String, Integer>> dadosDasUrnas;

    public void setDadosDasUrnas(Map<Integer, Map<String, Integer>> dadosDasUrnas) {
        this.dadosDasUrnas = dadosDasUrnas;
    }

    public Map<Integer, Map<String, Integer>> getDadosDasUrnas() {
        return dadosDasUrnas;
    }


    // Método para processar os votos e retornar totais por tipo
    public Map<String, Integer> processarVotos(String caminhoArquivo) {
        // Mapa para armazenar a contagem de votos por urna
        Map<Integer, Map<String, Integer>> contagemUrnas = new HashMap<>();
        
        // Contadores para votos totais, votos em branco e votos nulos
        int totalVotos = 0;
        int votosBrancos = 0;
        int votosNulos = 0;

        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));
            
            for (String linha : linhas) {
                // Extrai a urna e o código do partido/candidato
                String[] partes = linha.split(";");
                
                // Verifica se a linha tem a estrutura correta antes de processar
                if (partes.length < 2) {
                    continue; // Se a linha não tem pelo menos 2 partes, pula ela
                }

                // Extrai o número da urna
                int numeroUrna = Integer.parseInt(partes[0]);

                // Verifica o código do partido ou candidato
                String codigo = partes[1];

                // Identifica o voto
                String tipoVoto = "";

                if (codigo.equals("1")) {
                    tipoVoto = "Em Branco";
                    votosBrancos++; // Incrementa votos em branco
                } else if (codigo.equals("0")) {
                    tipoVoto = "Nulo";
                    votosNulos++; // Incrementa votos nulos
                } else {
                    tipoVoto = "Candidato " + codigo;
                }

                // Verifica se a urna já está no mapa; caso contrário, cria a entrada
                contagemUrnas.putIfAbsent(numeroUrna, new HashMap<>());
                
                // Atualiza a contagem de votos para o tipo (branco, nulo ou candidato) na urna atual
                Map<String, Integer> votosPorTipo = contagemUrnas.get(numeroUrna);
                votosPorTipo.put(tipoVoto, votosPorTipo.getOrDefault(tipoVoto, 0) + 1);

                setDadosDasUrnas(contagemUrnas);

                // Incrementa o total de votos
                totalVotos++;
            }

            // Exibe o resumo de votos por urna
            System.out.println("Resumo de votos por urna:");
            for (Map.Entry<Integer, Map<String, Integer>> urnaEntry : contagemUrnas.entrySet()) {
                int urna = urnaEntry.getKey();
                System.out.println("Urna " + urna + ":");
                Map<String, Integer> votosPorTipo = urnaEntry.getValue();
                
                for (Map.Entry<String, Integer> tipoEntry : votosPorTipo.entrySet()) {
                    String tipoVoto = tipoEntry.getKey();
                    int votos = tipoEntry.getValue();

                    // Exibe o resultado de acordo com o tipo de voto
                    System.out.println("  " + tipoVoto + ": " + votos + " votos");
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter número: " + e.getMessage());
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


    public Map<String, Integer> VotosPorCandidato (Map<Integer, Map<String , Integer>> dadosDasUrnas){

        Map<String, Integer> votosCandidatos = new HashMap<>();
        String candidato = "";
        int qtd_votos = 0;
        
        for(Map.Entry<Integer, Map<String, Integer>> entrada : dadosDasUrnas.entrySet()){
            
            Map<String, Integer> dadosVotos = entrada.getValue();
            
            for(Map.Entry<String,Integer> dados : dadosVotos.entrySet()){
                candidato = dados.getKey();
                qtd_votos = dados.getValue();
                
                votosCandidatos.put(candidato, votosCandidatos.getOrDefault(candidato, 0) + qtd_votos);
            }


        }
        
        



        return votosCandidatos;



    }


}