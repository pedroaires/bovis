package bovisApp.firstApp.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pesagens")
public class Pesagem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pesagem_seq_gen")
    @SequenceGenerator(name = "pesagem_seq_gen", sequenceName = "pesagem_id_seq", allocationSize = 1)
    private Long id;

    private LocalDate data;

    @ManyToOne
    private Boi boi;

    private BigDecimal peso;

    private String descricao;

    @ManyToMany
    private List<Medicacao> medicacoes;


    public Pesagem(LocalDate data, BigDecimal peso, String descricao, List<Medicacao> medicacoes) {
        this.data = data;
        this.peso = peso;
        this.descricao = descricao;
        this.medicacoes = medicacoes;
    }

    public Pesagem(Boi boi, LocalDate data, BigDecimal peso, String descricao, List<Medicacao> medicacoes) {
        this.boi = boi;
        this.data = data;
        this.peso = peso;
        this.descricao = descricao;
        this.medicacoes = medicacoes;
    }

    public Pesagem() {

    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
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
