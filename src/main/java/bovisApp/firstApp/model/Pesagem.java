package bovisApp.firstApp.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Pesagem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pesagem_seq_gen")
    @SequenceGenerator(name = "pesagem_seq_gen", sequenceName = "pesagem_id_seq", allocationSize = 1)
    private Long id;

    private Date data;

    @ManyToOne
    private Boi boi;

    private Double peso;

    private String descricao;

    @ManyToMany
    private List<Medicacao> medicacoes;


    public Pesagem(Date data, Double peso, String descricao, List<Medicacao> medicacoes) {
        this.data = data;
        this.peso = peso;
        this.descricao = descricao;
        this.medicacoes = medicacoes;
    }

    public Pesagem(Boi boi, Date data, Double peso, String descricao, List<Medicacao> medicacoes) {
        this.boi = boi;
        this.data = data;
        this.peso = peso;
        this.descricao = descricao;
        this.medicacoes = medicacoes;
    }

    public Pesagem() {

    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
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

    public void setDescricao(String comentario) {
        this.descricao = comentario;
    }

    public List<Medicacao> getMedicacoes() {
        return medicacoes;
    }

    public void setMedicacoes(List<Medicacao> medicacoes) {
        this.medicacoes = medicacoes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setBoi(Boi boi) {
        this.boi = boi;
    }
    public Boi getBoi() {
        return boi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pesagem pesagem = (Pesagem) o;
        return Objects.equals(id, pesagem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
