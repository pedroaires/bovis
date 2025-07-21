package bovisApp.firstApp.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "medicacoes")
public class Medicacao {

    private String nome;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medicacao_seq_gen")
    @SequenceGenerator(name = "medicacao_seq_gen", sequenceName = "medicacao_id_seq", allocationSize = 1)
    private Long id;

    public Medicacao(String nome) {
        this.nome = nome;
    }

    public Medicacao() {

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicacao medicacao = (Medicacao) o;
        return nome.equals(medicacao.getNome());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
