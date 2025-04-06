package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TransacaoResponseDTO {

    private Long id;
    private BigDecimal valor;
    private Date data;
    private String descricao;
    private ModeloTransacao modeloTransacao;
    private TransacaoOperacao operacao;
    private List<Boi> bois;
    private String contraParte;
    private Transacao transacaoRelacionada;

    public TransacaoResponseDTO(Long id, BigDecimal valor, Date data, String descricao, ModeloTransacao modeloTransacao, TransacaoOperacao operacao, List<Boi> bois, String contraParte, Transacao transacaoRelacionada) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.modeloTransacao = modeloTransacao;
        this.operacao = operacao;
        this.bois = bois;
        this.contraParte = contraParte;
        this.transacaoRelacionada = transacaoRelacionada;
    }

    public TransacaoResponseDTO(Transacao transacao) {
        this.id = transacao.getId();
        this.valor = transacao.getValor();
        this.descricao = transacao.getDescricao();
        this.modeloTransacao = transacao.getModeloTransacao();
        this.operacao = transacao.getOperacao();
        this.contraParte = transacao.getContraParte();
        this.transacaoRelacionada = transacao.getTransacaoRelacionada();
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
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
