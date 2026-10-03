package bovisApp.firstApp.DTO.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VendaRequestDTO extends TransacaoRequestDTO{
    private List<ItemVendaRequestDTO> itemVendaRequestDTOS;

    public VendaRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }

    public VendaRequestDTO(){

    }

    public List<ItemVendaRequestDTO> getVendaBoiDTOS() {
        return itemVendaRequestDTOS;
    }

    public void setVendaBoiDTOS(List<ItemVendaRequestDTO> itemVendaRequestDTOS) {
        this.itemVendaRequestDTOS = itemVendaRequestDTOS;
    }
}
