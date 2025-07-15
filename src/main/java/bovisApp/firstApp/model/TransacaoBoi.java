package bovisApp.firstApp.model;


import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class TransacaoBoi {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transacaoboi_seq_gen")
    @SequenceGenerator(name = "transacaoboi_seq_gen", sequenceName = "transacao_boi_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "boi_id")
    private Boi boi;

    @ManyToOne
    @JoinColumn(name = "transacao_id")
    private Transacao transacao;

    private BigDecimal valor;

    private Double pesoNaTransacao;
    public TransacaoBoi() {
    }

    public TransacaoBoi(BigDecimal valor, Double peso, Boi boi, Transacao transacao) {
        this.valor = valor;
        this.pesoNaTransacao = peso;
        this.boi = boi;
        this.transacao = transacao;
    }

    public TransacaoBoi(Long id, BigDecimal valor, Double peso, Boi boi, Transacao transacao) {
        this.id = id;
        this.valor = valor;
        this.pesoNaTransacao = peso;
        this.boi = boi;
        this.transacao = transacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Double getPesoNaTransacao() {
        return pesoNaTransacao;
    }

    public void setPeso(Double peso) {
        this.pesoNaTransacao = peso;
    }

    public Boi getBoi() {
        return boi;
    }

    public void setBoi(Boi boi) {
        this.boi = boi;
    }

    public Transacao getTransacao() {
        return transacao;
    }

    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
    }
}
