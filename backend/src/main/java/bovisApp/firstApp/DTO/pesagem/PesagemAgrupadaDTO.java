package bovisApp.firstApp.DTO.pesagem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PesagemAgrupadaDTO {
    private LocalDate data;
    private Integer totalBois;
    private BigDecimal pesoMedio;
    private BigDecimal pesoTotal;

    public PesagemAgrupadaDTO() {

    }
    public PesagemAgrupadaDTO(LocalDate data, Long totalBois, Double pesoMedio, BigDecimal pesoTotal) {
        this.data = data;
        this.totalBois = totalBois.intValue();
        this.pesoMedio = pesoMedio != null ? BigDecimal.valueOf(pesoMedio) : BigDecimal.ZERO;
        this.pesoTotal = pesoTotal != null ? pesoTotal : BigDecimal.ZERO;
    }
    public PesagemAgrupadaDTO(LocalDate data, Integer totalBois, BigDecimal pesoMedio, BigDecimal pesoTotal) {
        this.data = data;
        this.totalBois = totalBois;
        this.pesoMedio = pesoMedio;
        this.pesoTotal = pesoTotal;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getTotalBois() {
        return totalBois;
    }

    public void setTotalBois(Integer totalBois) {
        this.totalBois = totalBois;
    }

    public BigDecimal getPesoMedio() {
        return pesoMedio;
    }

    public void setPesoMedio(BigDecimal pesoMedio) {
        this.pesoMedio = pesoMedio;
    }

    public BigDecimal getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(BigDecimal pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

}
