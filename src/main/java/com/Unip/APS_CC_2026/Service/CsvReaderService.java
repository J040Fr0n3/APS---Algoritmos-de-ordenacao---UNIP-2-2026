package com.Unip.APS_CC_2026.Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.Unip.APS_CC_2026.model.Registro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CsvReaderService {

    // Formatador completo com hora
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Registro> carregarRegistrosPorEstado(String siglaEstado) {
        List<Registro> registros = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        String estadoFormatado = siglaEstado.trim().toUpperCase();

        try {
            String localizacaoPattern = String.format("classpath*:data/%s/*.csv", estadoFormatado);
            Resource[] resources = resolver.getResources(localizacaoPattern);

            if (resources.length == 0) {
                System.out.println("[AVISO] Nenhum arquivo CSV encontrado para o estado: " + siglaEstado);
                return registros;
            }

            for (Resource resource : resources) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    String linha;
                    boolean primeiraLinha = true; // Pula o cabeçalho

                    while ((linha = br.readLine()) != null) {
                        if (primeiraLinha) {
                            primeiraLinha = false;
                            continue;
                        }

                        if (linha.trim().isEmpty()) continue;

                        String[] campos = linha.split(",");
                        
                        if (campos.length >= 9) {
                            try {
                                // Lê a string completa (data + hora)
                                String dataString = campos[4].trim(); 
                                LocalDateTime dataPas = LocalDateTime.parse(dataString, DATE_FORMATTER);

                                Registro reg = new Registro(
                                    campos[0].trim(), // id_bdq
                                    campos[1].trim(), // foco_id
                                    Double.parseDouble(campos[2].trim()), // lat
                                    Double.parseDouble(campos[3].trim()), // lon
                                    dataPas,
                                    campos[5].trim(), // pais
                                    campos[6].trim(), // estado
                                    campos[7].trim(), // municipio
                                    campos[8].trim()  // bioma
                                );
                                registros.add(reg);
                            } catch (Exception e) {
                                // Ignora linha malformatada sem quebrar o loop inteiro
                                System.err.println("Erro ao ler linha do CSV: " + linha + " | " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ler arquivos do estado " + siglaEstado + ": " + e.getMessage());
            e.printStackTrace();
        }

        return registros;
    }
    
    public List<String> listarEstadosDisponiveis() {
        List<String> estados = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources("classpath*:data/**/*.csv");

            for (Resource res : resources) {
                String uri = res.getURI().toString();
                String[] partes = uri.split("/");
                if (partes.length >= 2) {
                    String nomePasta = partes[partes.length - 2];
                    if (!nomePasta.equalsIgnoreCase("data") && !estados.contains(nomePasta.toUpperCase())) {
                        estados.add(nomePasta.toUpperCase());
                    }
                }
            }
            Collections.sort(estados);
        } catch (Exception e) {
            System.err.println("Erro ao listar estados em data/: " + e.getMessage());
            e.printStackTrace();
        }

        return estados;
    }
}