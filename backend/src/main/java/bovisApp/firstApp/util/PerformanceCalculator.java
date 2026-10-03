package bovisApp.firstApp.util;


import bovisApp.firstApp.DTO.pesagem.PesoComGmdDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class PerformanceCalculator {

    public static BigDecimal calculateGMD(BigDecimal currentWeight, LocalDate currentDate,
                                      BigDecimal previousWeight, LocalDate previousDate) {

        if (currentWeight == null || previousWeight == null || currentDate == null || previousDate == null) {
            return BigDecimal.ZERO;
        }

        long days = ChronoUnit.DAYS.between(previousDate, currentDate);

        if (days <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightDifference = currentWeight.subtract(previousWeight);
        BigDecimal daysBigDecimal = BigDecimal.valueOf(days);

        BigDecimal gmdBigDecimal = weightDifference.divide(
                daysBigDecimal,
                2,
                RoundingMode.HALF_UP
        );

        return gmdBigDecimal;
    }

    public static BigDecimal calcularGmdMedio(List<PesoComGmdDTO> pesos) {
        List<BigDecimal> gmdsValidos = pesos.stream()
                .map(PesoComGmdDTO::getGmd)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (gmdsValidos.isEmpty()) {
            return null;
        }

        BigDecimal somaGmds = gmdsValidos.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return somaGmds.divide(
                BigDecimal.valueOf(gmdsValidos.size()),
                3,
                RoundingMode.HALF_UP
        );
    }
}
