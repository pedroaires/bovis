package bovisApp.firstApp.DTO.pesagem;

import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class PesagemResponseDTO {
private Long id;
    private LocalDate data;
    private Double peso;
    private String descricao;
    private List<Medicacao> medicacoes;

    public PesagemResponseDTO(Long id, LocalDate data, Double peso, String descricao, List<Medicacao> medicacoes) {
        this.id = id;
        this.data = data;
        this.peso = peso;
        this.descricao = descricao;
        this.medicacoes = medicacoes;
    }

    public PesagemResponseDTO(Pesagem pesagem){
        this.id = pesagem.getId();
        this.data = pesagem.getData();
        this.peso = pesagem.getPeso();
        this.descricao = pesagem.getDescricao();
        this.medicacoes = pesagem.getMedicacoes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
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

    public List<Medicacao> getMedicacoes() {
        return medicacoes;
    }

    public void setMedicacoes(List<Medicacao> medicacoes) {
        this.medicacoes = medicacoes;
    }
}
