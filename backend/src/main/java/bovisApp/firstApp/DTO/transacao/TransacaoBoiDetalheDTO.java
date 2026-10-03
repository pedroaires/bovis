package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiResponseDTO;
import bovisApp.firstApp.DTO.pesagem.PesagemResponseDTO;
import bovisApp.firstApp.model.TransacaoBoi;

import java.math.BigDecimal;

public class TransacaoBoiDetalheDTO {
    private BigDecimal valor;

    private BoiResponseDTO boi;
    private PesagemResponseDTO pesagem;

    public TransacaoBoiDetalheDTO(TransacaoBoi transacaoBoi) {
        this.valor = transacaoBoi.getValor();

        this.boi = new BoiResponseDTO(transacaoBoi.getBoi());
        this.pesagem = new PesagemResponseDTO(transacaoBoi.getPesagem());
    }

    public TransacaoBoiDetalheDTO() {

    }


    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BoiResponseDTO getBoi() {
        return boi;
    }

    public void setBoi(BoiResponseDTO boi) {
        this.boi = boi;
    }

    public PesagemResponseDTO getPesagem() {
        return pesagem;
    }

    public void setPesagem(PesagemResponseDTO pesagem) {
        this.pesagem = pesagem;
    }
}
