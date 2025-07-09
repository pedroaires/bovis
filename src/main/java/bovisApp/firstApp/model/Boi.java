package bovisApp.firstApp.model;

import bovisApp.firstApp.model.enumeration.EstadoBoi;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table
public class Boi {
    @Id
    @GeneratedValue
    private Long id;
    private Integer numero;
    @OneToMany
    private Map<Date, Pesagem> pesagens;
    @OneToOne
    private Raca raca;

    @OneToMany(mappedBy = "boi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransacaoBoi> transacaoBois;

    private EstadoBoi estadoBoi;

    public Boi(int numero, String raca){ }


    public Boi(int numero, Raca raca, EstadoBoi estadoBoi) {
        this.numero = numero;
        this.raca = raca;
        this.estadoBoi = estadoBoi;
    }

    public Boi() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }

    public Map<Date, Pesagem> getPesagens() {
        return pesagens;
    }

    public void setPesagens(Map<Date, Pesagem> pesagens) {
        this.pesagens = pesagens;
    }


    public EstadoBoi getEstadoBoi() {
        return estadoBoi;
    }

    public void setEstadoBoi(EstadoBoi estadoBoi) {
        this.estadoBoi = estadoBoi;
    }

    public List<TransacaoBoi> getTransacaoBois() {
        return transacaoBois;
    }

    public void setTransacaoBois(List<TransacaoBoi> transacaoBois) {
        this.transacaoBois = transacaoBois;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boi boi = (Boi) o;
        return Objects.equals(this.id, boi.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Boi{" +
                "id=" + id +
                ", numero=" + numero +
                ", raca='" + raca + '\'' +
                ", estadoBoi=" + estadoBoi +
                '}';
    }
}
