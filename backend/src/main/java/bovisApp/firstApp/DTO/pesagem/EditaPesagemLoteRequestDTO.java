package bovisApp.firstApp.DTO.pesagem;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EditaPesagemLoteRequestDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate novaData;


    public EditaPesagemLoteRequestDTO() {

    }

    public EditaPesagemLoteRequestDTO(LocalDate novaData) {
        this.novaData = novaData;
    }

    public LocalDate getNovaData() {
        return novaData;
    }

    public void setNovaData(LocalDate novaData) {
        this.novaData = novaData;
    }
}
