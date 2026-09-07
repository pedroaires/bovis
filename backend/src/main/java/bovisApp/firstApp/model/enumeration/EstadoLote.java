package bovisApp.firstApp.model.enumeration;

public enum EstadoLote {
    VENDIDO,
    PARCIALMENTE_VENDIDO,
    NADA_VENDIDO;

    public static EstadoLote getEstadoLote(String estadoLoteStr) {
        if(estadoLoteStr == null || estadoLoteStr.isEmpty()) {
            throw new IllegalArgumentException("Estado do lote não pode ser nulo ou vazio");
        }
        String strNorm = estadoLoteStr.toUpperCase().trim();
        return EstadoLote.valueOf(strNorm);
    }
}
