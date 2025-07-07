package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;

import java.math.BigDecimal;

public class TransacaoBoiDTO {
    private BoiRequestDTO boiRequestDTO;
    private BigDecimal valor;
    private Double pesoNaTransacao;

    public TransacaoBoiDTO(BoiRequestDTO boiRequestDTO, BigDecimal valor, Double pesoNaTransacao) {
        this.boiRequestDTO = boiRequestDTO;
        this.valor = valor;
        this.pesoNaTransacao = pesoNaTransacao;
    }

    public BoiRequestDTO getBoiRequestDTO() {
        return boiRequestDTO;
    }

    public void setBoiRequestDTO(BoiRequestDTO boiRequestDTO) {
        this.boiRequestDTO = boiRequestDTO;
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
