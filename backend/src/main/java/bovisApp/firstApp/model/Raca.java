package bovisApp.firstApp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Raca {

    @Id
    @GeneratedValue
    private Long id;
    private String nome;

    public Raca(String nome) {
        this.nome = nome;
    }

    public Raca() {

    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Raca raca = (Raca) o;
        return Objects.equals(this.nome, raca.getNome());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
