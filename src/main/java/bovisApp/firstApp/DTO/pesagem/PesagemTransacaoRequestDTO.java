package bovisApp.firstApp.DTO.pesagem;

import java.math.BigDecimal;

public class PesagemTransacaoRequestDTO {
    private BigDecimal peso;
    private String descricao;

    public PesagemTransacaoRequestDTO() {

    }

    public PesagemTransacaoRequestDTO(BigDecimal peso, String descricao) {
        this.peso = peso;
        this.descricao = descricao;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
