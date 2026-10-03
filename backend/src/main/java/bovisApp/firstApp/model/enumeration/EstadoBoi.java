package bovisApp.firstApp.model.enumeration;

public enum EstadoBoi {
    VENDIDO,
    NA_FAZENDA,
    MORTO;

    public static EstadoBoi getEstadoBoi(String estadoBoiStr) {
        try {
            String strNorm = estadoBoiStr.toUpperCase().trim();
            return EstadoBoi.valueOf(strNorm);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado do boi inválido: " + estadoBoiStr);
        }
    }
}