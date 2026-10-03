package bovisApp.firstApp.DTO.pesagem;

import java.math.BigDecimal;

public class PesagemTransacaoRequestDTO {
    private BigDecimal peso;

    public PesagemTransacaoRequestDTO() {

    }

    public PesagemTransacaoRequestDTO(BigDecimal peso, String descricao) {
        this.peso = peso;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

}
