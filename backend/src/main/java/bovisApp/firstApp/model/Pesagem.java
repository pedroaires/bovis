package bovisApp.firstApp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Entity
public class Pesagem {
    @Id
    @GeneratedValue
    private Long id;

    private Date data;

    private Double peso;

    private String comentario;

    @ManyToMany
    private List<Medicacao> medicacoes;

    public Pesagem(Date data, Double peso, String comentario, List<Medicacao> medicacoes) {
        this.data = data;
        this.peso = peso;
        this.comentario = comentario;
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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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
}
