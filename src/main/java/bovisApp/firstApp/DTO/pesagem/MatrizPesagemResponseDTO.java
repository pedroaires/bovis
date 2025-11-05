package bovisApp.firstApp.DTO.pesagem;

import java.time.LocalDate;
import java.util.List;

public class MatrizPesagemResponseDTO {
    private Integer totalBois;
    private Integer totalDias;
    private List<LocalDate> dates;
    private List<LinhaMatrizDTO> rows;

    public MatrizPesagemResponseDTO() {
    }

    public MatrizPesagemResponseDTO(Integer totalBois, Integer totalDias, List<LocalDate> dates, List<LinhaMatrizDTO> rows) {
        this.totalBois = totalBois;
        this.totalDias = totalDias;
        this.dates = dates;
        this.rows = rows;
    }

    // Getters e Setters
    public Integer getTotalBois() {
        return totalBois;
    }

    public void setTotalBois(Integer totalBois) {
        this.totalBois = totalBois;
    }

    public Integer getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(Integer totalDias) {
        this.totalDias = totalDias;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public List<LinhaMatrizDTO> getRows() {
        return rows;
    }

    public void setRows(List<LinhaMatrizDTO> rows) {
        this.rows = rows;
    }
}