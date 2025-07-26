package bovisApp.firstApp.DTO.pesagem;

import bovisApp.firstApp.model.Medicacao;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class PesagemRequestDTO {
    private Long boiId;
    private Double peso;
    private String descricao;

    private LocalDate data;

    private List<String> medicacoes;

    public PesagemRequestDTO(Long boiId, Double peso, String descricao, LocalDate data, List<String> medicacoes) {
        this.boiId = boiId;
        this.peso = peso;
        this.descricao = descricao;
        this.data = data;
        this.medicacoes = medicacoes;
    }

    public Long getBoiId() {
        return boiId;
    }

    public void setBoiId(Long boiId) {
        this.boiId = boiId;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<String> getMedicacoes() {
        return medicacoes;
    }

    public void setMedicacoes(List<String> medicacoes) {
        this.medicacoes = medicacoes;
    }
}
