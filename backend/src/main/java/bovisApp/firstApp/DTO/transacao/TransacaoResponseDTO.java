package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.TransacaoBoi;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransacaoResponseDTO {

    private Long id;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;
    private ModeloTransacao modeloTransacao;
    private TransacaoOperacao operacao;
    private List<Boi> bois;
    private String contraParte;
    private Long transacaoRelacionadaId;

    public TransacaoResponseDTO(Long id, BigDecimal valor, LocalDate data, String descricao, ModeloTransacao modeloTransacao, TransacaoOperacao operacao, List<Boi> bois, String contraParte, Long transacaoRelacionadaId) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.modeloTransacao = modeloTransacao;
        this.operacao = operacao;
        this.bois = bois;
        this.contraParte = contraParte;
        this.transacaoRelacionadaId = transacaoRelacionadaId;
    }

    public TransacaoResponseDTO(Transacao transacao) {
        this.id = transacao.getId();
        this.valor = transacao.getValor();
        this.data = transacao.getData();
        this.descricao = transacao.getDescricao();
        this.modeloTransacao = transacao.getModeloTransacao();
        this.operacao = transacao.getOperacao();
        this.contraParte = transacao.getContraParte();
        if (Optional.ofNullable(transacao.getTransacaoRelacionada()).isPresent()){
            this.transacaoRelacionadaId = transacao.getTransacaoRelacionada().getId();
        }
        else{
            this.transacaoRelacionadaId = null;
        }
    }

    public TransacaoResponseDTO(TransacaoBoi transacaoBoi) {
        this.id = transacaoBoi.getTransacao().getId();
        this.valor = transacaoBoi.getTransacao().getValor();
        this.descricao = transacaoBoi.getTransacao().getDescricao();
        this.modeloTransacao = transacaoBoi.getTransacao().getModeloTransacao();
        this.operacao = transacaoBoi.getTransacao().getOperacao();
        this.contraParte = transacaoBoi.getTransacao().getContraParte();
        this.transacaoRelacionadaId = transacaoBoi.getTransacao().getTransacaoRelacionada().getId();
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

    public Long getTransacaoRelacionada() {
        return transacaoRelacionadaId;
    }

    public void setTransacaoRelacionada(Long transacaoRelacionadaId) {
        this.transacaoRelacionadaId = transacaoRelacionadaId;
    }
}
