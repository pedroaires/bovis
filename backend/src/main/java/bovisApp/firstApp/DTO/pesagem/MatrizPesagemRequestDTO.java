package bovisApp.firstApp.DTO.pesagem;

import java.time.LocalDate;
import java.util.List;

public class MatrizPesagemRequestDTO {
    private List<LocalDate> datasSelecionadas;

    public MatrizPesagemRequestDTO() {

    }

    public MatrizPesagemRequestDTO(List<LocalDate> datasSelecionadas) {
        this.datasSelecionadas = datasSelecionadas;
    }

    public List<LocalDate> getDatasSelecionadas() {
        return datasSelecionadas;
    }

    public void setDatasSelecionadas(List<LocalDate> datasSelecionadas) {
        this.datasSelecionadas = datasSelecionadas;
    }
}
