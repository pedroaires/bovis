package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompraRequestDTO extends TransacaoRequestDTO{
    private List<CompraBoiDTO> compraBoiDTOS;

    public CompraRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }
    public CompraRequestDTO(){

    }
    public List<CompraBoiDTO> getCompraBoiDTOS() {
        return compraBoiDTOS;
    }

    public void setCompraBoiDTOS(List<CompraBoiDTO> compraBoiDTOS) {
        this.compraBoiDTOS = compraBoiDTOS;
    }
}
