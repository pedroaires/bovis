package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.pesagem.PesagemTransacaoRequestDTO;

import java.math.BigDecimal;

public class ItemCompraRequestDTO {
    private BoiRequestDTO boiRequestDTO;
    private BigDecimal valor;
    private PesagemTransacaoRequestDTO pesagemDTO;

    public ItemCompraRequestDTO() {

    }

    public ItemCompraRequestDTO(BoiRequestDTO boiRequestDTO, BigDecimal valor, PesagemTransacaoRequestDTO pesagemDTO) {
        this.boiRequestDTO = boiRequestDTO;
        this.valor = valor;
        this.pesagemDTO = pesagemDTO;
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

    public PesagemTransacaoRequestDTO getPesagemDTO() {
        return pesagemDTO;
    }

    public void setPesagemDTO(PesagemTransacaoRequestDTO pesagemDTO) {
        this.pesagemDTO = pesagemDTO;
    }

    @Override
    public String toString() {
        return "ItemCompraRequestDTO{" +
                "boiRequestDTO=" + boiRequestDTO +
                ", valor=" + valor +
                ", " + pesagemDTO +
                '}';
    }
}
