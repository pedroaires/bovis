package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;

import java.math.BigDecimal;

public class VendaBoiDTO {
    private Long boiId;
    private BigDecimal valor;
    private Double pesoNaTransacao;

    public VendaBoiDTO() {
    }
    public VendaBoiDTO(Long boiId, BigDecimal valor, Double pesoNaTransacao) {
        this.boiId = boiId;
        this.valor = valor;
        this.pesoNaTransacao = pesoNaTransacao;
    }

    public Long getBoiId() {
        return boiId;
    }

    public void setBoiId(Long boiId) {
        this.boiId = boiId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Double getPesoNaTransacao() {
        return pesoNaTransacao;
    }

    public void setPesoNaTransacao(Double pesoNaTransacao) {
        this.pesoNaTransacao = pesoNaTransacao;
    }
}
