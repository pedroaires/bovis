package bovisApp.firstApp.model;

import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "transacoes")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transacao_seq_gen")
    @SequenceGenerator(name = "transacao_seq_gen", sequenceName = "transacao_id_seq", allocationSize = 1)
    private Long id;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;
    private ModeloTransacao modeloTransacao;
    private TransacaoOperacao operacao;

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransacaoBoi> transacaoBois;

    private String contraParte;

    @OneToOne
    private Transacao transacaoRelacionada;

    public Transacao(BigDecimal valor, LocalDate data, String descricao, ModeloTransacao modeloTransacao, TransacaoOperacao operacao, List<TransacaoBoi> transacaoBois, String contraParte, Transacao transacaoRelacionada) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.modeloTransacao = modeloTransacao;
        this.operacao = operacao;
        this.transacaoBois = transacaoBois;
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

    public List<TransacaoBoi> getTransacaoBois() {
        return transacaoBois;
    }

    public void setTransacaoBois(List<TransacaoBoi> transacaoBois) {
        this.transacaoBois = transacaoBois;
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
