package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.pesagem.PesagemTransacaoRequestDTO;

import java.math.BigDecimal;

public class ItemVendaRequestDTO {
    private Long boiId;
    private BigDecimal valor;
    private PesagemTransacaoRequestDTO pesagem;

    public ItemVendaRequestDTO() {
    }
    public ItemVendaRequestDTO(Long boiId, BigDecimal valor, PesagemTransacaoRequestDTO pesagem) {
        this.boiId = boiId;
        this.valor = valor;
        this.pesagem = pesagem;
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

    public PesagemTransacaoRequestDTO getPesagemDTO() {
        return pesagem;
    }

    public void setPesagemDTO(PesagemTransacaoRequestDTO pesagem) {
        this.pesagem = pesagem;
    }
}
