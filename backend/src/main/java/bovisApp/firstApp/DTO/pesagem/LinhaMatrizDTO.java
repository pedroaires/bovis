package bovisApp.firstApp.DTO.pesagem;

import java.math.BigDecimal;
import java.util.List;

public class LinhaMatrizDTO {
    private Long boiId;
    private Integer boiNumero;
    private String raca;
    private List<PesoComGmdDTO> pesos;
    private BigDecimal gmdMedio;
    private BigDecimal ganhoTotal;

    public LinhaMatrizDTO() {
    }

    public LinhaMatrizDTO(Long boiId, Integer boiNumero, String raca, List<PesoComGmdDTO> pesos, BigDecimal gmdMedio, BigDecimal ganhoTotal) {
        this.boiId = boiId;
        this.boiNumero = boiNumero;
        this.raca = raca;
        this.pesos = pesos;
        this.gmdMedio = gmdMedio;
        this.ganhoTotal = ganhoTotal;
    }

    public Long getBoiId() {
        return boiId;
    }

    public void setBoiId(Long boiId) {
        this.boiId = boiId;
    }

    public Integer getBoiNumero() {
        return boiNumero;
    }

    public void setBoiNumero(Integer boiNumero) {
        this.boiNumero = boiNumero;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public List<PesoComGmdDTO> getPesos() {
        return pesos;
    }

    public void setPesos(List<PesoComGmdDTO> pesos) {
        this.pesos = pesos;
    }

    public BigDecimal getGmdMedio() {
        return gmdMedio;
    }

    public void setGmdMedio(BigDecimal gmdMedio) {
        this.gmdMedio = gmdMedio;
    }

    public BigDecimal getGanhoTotal() {
        return ganhoTotal;
    }

    public void setGanhoTotal(BigDecimal ganhoTotal) {
        this.ganhoTotal = ganhoTotal;
    }
}
