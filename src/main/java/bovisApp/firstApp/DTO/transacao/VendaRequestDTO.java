package bovisApp.firstApp.DTO.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VendaRequestDTO extends TransacaoRequestDTO{
    private List<VendaBoiDTO> vendaBoiDTOS;

    public VendaRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }

    public VendaRequestDTO(){

    }

    public List<VendaBoiDTO> getVendaBoiDTOS() {
        return vendaBoiDTOS;
    }

    public void setVendaBoiDTOS(List<VendaBoiDTO> vendaBoiDTOS) {
        this.vendaBoiDTOS = vendaBoiDTOS;
    }
}
