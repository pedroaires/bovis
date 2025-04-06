package bovisApp.firstApp.DTO.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VendaRequestDTO extends TransacaoRequestDTO{
    private List<Long> boisIds;

    public VendaRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }

    public VendaRequestDTO(){

    }


    public List<Long> getBoisIds() {
        return boisIds;
    }

    public void setBoisIds(List<Long> boisIds) {
        this.boisIds = boisIds;
    }
}
