package com.Unip.APS_CC_2026.model;

import java.util.Comparator;
import java.util.List;

public class RegistroComparatorFactory {

    public static Comparator<Registro> criarComparator(List<String> criterios) {
        Comparator<Registro> comparatorResultante = null;

        for (String criterio : criterios) {
            Comparator<Registro> compAtual = extrairComparator(criterio);

            if (compAtual != null) {
                if (comparatorResultante == null) {
                    comparatorResultante = compAtual;
                } else {
                    // Encadeia com o próximo critério de desempate
                    comparatorResultante = comparatorResultante.thenComparing(compAtual);
                }
            }
        }

        // Se nenhum critério for fornecido, não altera a ordem
        return comparatorResultante != null ? comparatorResultante : (a, b) -> 0;
    }

    private static Comparator<Registro> extrairComparator(String criterio) {
        switch (criterio.toLowerCase().trim()) {
            case "id_bdq":
                return Comparator.comparing(Registro::getIdBdq, Comparator.nullsLast(Comparator.naturalOrder()));
            case "foco_id":
                return Comparator.comparing(Registro::getFocoId, Comparator.nullsLast(Comparator.naturalOrder()));
            case "lat":
                return Comparator.comparingDouble(Registro::getLat);
            case "lon":
                return Comparator.comparingDouble(Registro::getLon);
            case "data_pas":
            case "data":
                return Comparator.comparing(Registro::getDataPas, Comparator.nullsLast(Comparator.naturalOrder()));
            case "pais":
                return Comparator.comparing(Registro::getPais, Comparator.nullsLast(Comparator.naturalOrder()));
            case "estado":
                return Comparator.comparing(Registro::getEstado, Comparator.nullsLast(Comparator.naturalOrder()));
            case "municipio":
                return Comparator.comparing(Registro::getMunicipio, Comparator.nullsLast(Comparator.naturalOrder()));
            case "bioma":
                return Comparator.comparing(Registro::getBioma, Comparator.nullsLast(Comparator.naturalOrder()));
            default:
            	return Comparator.comparing(Registro::getEstado, Comparator.nullsLast(Comparator.naturalOrder()));
        }
    }
}
