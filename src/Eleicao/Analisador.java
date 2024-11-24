package Eleicao;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
                String[] partes = candidato.split(" ");

                if(partes[0].equalsIgnoreCase("Candidato")){
                    votosCandidatos.put(candidato, votosCandidatos.getOrDefault(candidato, 0) + qtd_votos);
                }
                
            }

            
        }
        for(Map.Entry<String,Integer> entry: votosCandidatos.entrySet()){

            System.out.print(entry.getKey() + ": ");
            System.out.println(" Total de votos = " + entry.getValue());
        }            
        
        return votosCandidatos;

    }


    public List<String> obterTopCandidatos(String caminhoArquivo, int n) {
		Map<String, Integer> votosCandidatos = new HashMap<>();

		try {
			List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));

			for (String linha : linhas) {
				String[] partes = linha.split(";");

				if (partes.length < 2) continue;

				String codigo = partes[1];

				// Se o código não for 0 ou 1 (códigos inválidos), processa o voto
				if (!codigo.equals("0") && !codigo.equals("1")) {
					String candidato = "Candidato " + codigo;

					// Conta os votos
					int votos = votosCandidatos.getOrDefault(candidato, 0) + 1;

					// Atualiza o mapa com o novo número de votos
					votosCandidatos.put(candidato, votos);
				}
			}

			List<String> topCandidatos = new ArrayList<>();

			// Ordena os candidatos pela quantidade de votos (do maior para o menor)
			// E pega apenas os 'n' mais votados
			List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(votosCandidatos.entrySet());
			listaOrdenada.sort((a, b) -> b.getValue().compareTo(a.getValue()));

			// Adiciona os candidatos mais votados na lista final
			for (int i = 0; i < Math.min(n, listaOrdenada.size()); i++) {
				Map.Entry<String, Integer> candidato = listaOrdenada.get(i);
				topCandidatos.add(candidato.getKey() + " - " + candidato.getValue() + " votos");
			}

			return topCandidatos;

		} catch (IOException e) {
			System.err.println("Erro ao ler o arquivo: " + e.getMessage());
		}
		return new ArrayList<>();
	}


	public Map<String, String> calcularVotosPorLegenda(String caminhoArquivo) {
		Map<String, Integer> votosPorLegenda = new HashMap<>();
		int totalVotosValidos = 0;

		try {
			List<String> linhas = Files.readAllLines(Paths.get(caminhoArquivo));

			for (String linha : linhas) {
				String[] partes = linha.split(";");

				if (partes.length < 2) continue;

				String codigo = partes[1];

				if (codigo.equals("0") || codigo.equals("1")) continue;

				// Pega os dois primeiros caracteres como a legenda
				String legenda = codigo.substring(0, 2);

				// Conta os votos por legenda
				votosPorLegenda.put(legenda, votosPorLegenda.getOrDefault(legenda, 0) + 1);
				totalVotosValidos++;
			}

            setVotosPorLegenda(votosPorLegenda);

			Map<String, String> resultado = new HashMap<>();

			// Para cada legenda, calcula o número de votos e o percentual
			for (String legenda : votosPorLegenda.keySet()) {
				int votos = votosPorLegenda.get(legenda);
				double percentual = (double) votos / totalVotosValidos * 100;

				resultado.put(legenda, votos + " votos (" + Math.round(percentual) + "%)");
			}
			// Retorna o mapa com as informações das legendas
			return resultado;

		} catch (IOException e) {
			System.err.println("Erro ao ler o arquivo: " + e.getMessage());
		}

		return new HashMap<>();
	}


    public int calculo_Qe(int votosValidos, int num_cadeiras){

        int q_e = votosValidos / num_cadeiras;
        
        return q_e;

    }

    public Map<String,Integer> calculo_Qp(int q_e){

        
        Map<String, Integer> cadeiras = new HashMap<>();

        int cont = 0;
        int q_p;
        int vagas = 0;

        for(Map.Entry<String,Integer> partido : votosPorLegenda.entrySet()){

            int qtd_votos = partido.getValue();
            q_p = 0;
            q_p = qtd_votos / q_e;

            if(q_p != 0){
                vagas += q_p;
            }

            cadeiras.put(partido.getKey(), q_p);
            
        }

        while(vagas < 10){

            double maiorProporcao = -1;
            String partidoEscolhido = null;

            for(Map.Entry<String, Integer> entry : cadeiras.entrySet()){

                double totalVotosP = votosPorLegenda.get(entry.getKey());


                if(cont > 0){

                    double proporcaoAtual =  totalVotosP / (entry.getValue() + 1);
        
                    if(proporcaoAtual > maiorProporcao){
                        maiorProporcao = proporcaoAtual;
                        partidoEscolhido = entry.getKey();
                    }

                }


            }
            if(partidoEscolhido!= null){

                cadeiras.put(partidoEscolhido, cadeiras.get(partidoEscolhido) + 1);
                vagas++;
            }

            cont++;
        }

        for(Map.Entry<String, Integer> entry : cadeiras.entrySet()){
            System.out.print("Partido: " + entry.getKey());
            System.out.println(" Cadeiras disponíveis: " + entry.getValue());
        }

        return cadeiras;

    }



    public Map<String,Integer> Eleitos(Map<String,Integer> cadeiras, Map<String,Integer> votosPorCandidato, int q_e){

        Map<String, Integer> candidatosEleitos = new HashMap<>();
        String Eleito = " ";
        List<String> num_cadeira = new ArrayList<>();


        for(Map.Entry<String,Integer> entry : cadeiras.entrySet()){
            String partido = entry.getKey();
            int qtd = entry.getValue();

            for(Map.Entry<String, Integer> entrada : votosPorCandidato.entrySet()){
    
                int dez_p = (q_e * 10) / 100;
    
                
                if(entry.getValue() > dez_p){
                    System.out.println(entrada.getKey() + " Eleito");
                } else {
                    System.out.println(entrada.getKey() + " Não eleito");
    
                }
    
            }
        }




        return candidatosEleitos;

    }

}
