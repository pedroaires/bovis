package bovisApp.firstApp.model;

import bovisApp.firstApp.model.enumeration.EstadoLote;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table
public class Lote {
    @Id
    @GeneratedValue
    private Long id;
    private Date dataCompra;
    private Date dataVenda;
    private String descricao;
    private EstadoLote estadoLote;

    public Lote() {
    }

    public Lote(Date dataCompra, EstadoLote estadoLote) {
        this.dataCompra = dataCompra;
        this.estadoLote = estadoLote;
    }

    public Lote(Date dataCompra, EstadoLote estadoLote, String descricao) {
        this.dataCompra = dataCompra;
        this.estadoLote = estadoLote;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Date dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public EstadoLote getEstado() {
        return estadoLote;
    }

    public void setEstado(EstadoLote estadoLote) {
        this.estadoLote = estadoLote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lote lote = (Lote) o;
        return Objects.equals(this.id, lote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
