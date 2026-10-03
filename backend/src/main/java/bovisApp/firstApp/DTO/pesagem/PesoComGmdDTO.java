package bovisApp.firstApp.DTO.pesagem;

import java.math.BigDecimal;

public class PesoComGmdDTO {
    private BigDecimal peso;
    private BigDecimal gmd;
    private Long pesagemId;

    public PesoComGmdDTO() {
    }

    public PesoComGmdDTO(BigDecimal peso, BigDecimal gmd, Long pesagemId) {
        this.peso = peso;
        this.gmd = gmd;
        this.pesagemId = pesagemId;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getGmd() {
        return gmd;
    }

    public void setGmd(BigDecimal gmd) {
        this.gmd = gmd;
    }

    public Long getPesagemId() {
        return pesagemId;
    }

    public void setPesagemId(Long pesagemId) {
        this.pesagemId = pesagemId;
    }
}