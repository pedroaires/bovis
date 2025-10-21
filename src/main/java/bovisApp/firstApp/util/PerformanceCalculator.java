package bovisApp.firstApp.util;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


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
                2
        );

        return gmdBigDecimal;
    }
}
