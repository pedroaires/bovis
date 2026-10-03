package bovisApp.firstApp.model;


import org.springframework.context.annotation.EnableMBeanExport;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transacoes_bois")
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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pesagem_id", nullable = false)
    private Pesagem pesagem;
    public TransacaoBoi() {
    }

    public TransacaoBoi(BigDecimal valor, Pesagem pesagem, Boi boi, Transacao transacao) {
        this.valor = valor;
        this.pesagem = pesagem;
        this.boi = boi;
        this.transacao = transacao;
    }

    public TransacaoBoi(Long id, BigDecimal valor, Pesagem pesagem, Boi boi, Transacao transacao) {
        this.id = id;
        this.valor = valor;
        this.pesagem = pesagem;
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

    public Pesagem getPesagem() {
        return pesagem;
    }

    public void setPesagem(Pesagem pesagem) {
        this.pesagem = pesagem;
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
