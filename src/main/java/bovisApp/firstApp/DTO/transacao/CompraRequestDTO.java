package bovisApp.firstApp.DTO.transacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CompraRequestDTO extends TransacaoRequestDTO{
    private List<ItemCompraRequestDTO> itemCompraRequestDTOS;

    public CompraRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraParte) {
        super(valor, data, descricao, modeloTransacao, contraParte);
    }
    public CompraRequestDTO(){

    }
    public List<ItemCompraRequestDTO> getCompraBoiDTOS() {
        return itemCompraRequestDTOS;
    }

    public void setCompraBoiDTOS(List<ItemCompraRequestDTO> itemCompraRequestDTOS) {
        this.itemCompraRequestDTOS = itemCompraRequestDTOS;
    }

    @Override
    public String toString() {
        return "CompraRequestDTO{" +
                "itemCompraRequestDTOS=" + itemCompraRequestDTOS +
                '}';
    }
}
