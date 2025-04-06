package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompraRequestDTO extends TransacaoRequestDTO{
    private List<BoiRequestDTO> bois;

    public CompraRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }
    public CompraRequestDTO(){

    }

    public List<BoiRequestDTO> getBois() {
        return bois;
    }

    public void setBois(List<BoiRequestDTO> bois) {
        this.bois = bois;
    }

}
