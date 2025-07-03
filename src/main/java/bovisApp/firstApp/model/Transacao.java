package bovisApp.firstApp.model;

import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
public class Transacao {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;
    private ModeloTransacao modeloTransacao;
    private TransacaoOperacao operacao;

    @ManyToMany
    @JoinTable(
            name = "transacao_boi",
            joinColumns = @JoinColumn(name = "transacao_id"),
            inverseJoinColumns = @JoinColumn(name = "boid_id")
    )
    private List<Boi> bois;
    private String contraParte;

    @OneToOne
    private Transacao transacaoRelacionada;

    public Transacao(BigDecimal valor, LocalDate data, String descricao, ModeloTransacao modeloTransacao, TransacaoOperacao operacao, List<Boi> bois, String contraParte, Transacao transacaoRelacionada) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.modeloTransacao = modeloTransacao;
        this.operacao = operacao;
        this.bois = bois;
        this.contraParte = contraParte;
        this.transacaoRelacionada = transacaoRelacionada;
    }

    public Transacao() {

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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ModeloTransacao getModeloTransacao() {
        return modeloTransacao;
    }

    public void setModeloTransacao(ModeloTransacao modeloTransacao) {
        this.modeloTransacao = modeloTransacao;
    }

    public TransacaoOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(TransacaoOperacao operacao) {
        this.operacao = operacao;
    }

    public List<Boi> getBois() {
        return bois;
    }

    public void setBois(List<Boi> bois) {
        this.bois = bois;
    }

    public String getContraParte() {
        return contraParte;
    }

    public void setContraParte(String contraParte) {
        this.contraParte = contraParte;
    }

    public Transacao getTransacaoRelacionada() {
        return transacaoRelacionada;
    }

    public void setTransacaoRelacionada(Transacao transacaoRelacionada) {
        this.transacaoRelacionada = transacaoRelacionada;
    }
}
