package bovisApp.firstApp.DTO.transacao;

import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransacaoDetalhadaCompletaDTO {

    private Long id;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;
    private ModeloTransacao modeloTransacao;
    private TransacaoOperacao operacao;
    private String contraParte;

    private TransacaoResponseDTO transacaoRelacionada;

    private List<TransacaoBoiDetalheDTO> itens;

    public TransacaoDetalhadaCompletaDTO() {

    }

    public TransacaoDetalhadaCompletaDTO(Transacao transacao) {
        this.id = transacao.getId();
        this.valor = transacao.getValor();
        this.data = transacao.getData();
        this.descricao = transacao.getDescricao();
        this.modeloTransacao = transacao.getModeloTransacao();
        this.operacao = transacao.getOperacao();
        this.contraParte = transacao.getContraParte();

        // Mapeia a transação relacionada (se existir) usando o DTO mínimo
        if (transacao.getTransacaoRelacionada() != null) {
            this.transacaoRelacionada = new TransacaoResponseDTO(transacao.getTransacaoRelacionada());
        }

        // Mapeia a lista de TransacaoBoi para a lista de DTOs de item
        if (transacao.getTransacaoBois() != null) {
            this.itens = transacao.getTransacaoBois().stream()
                    .map(TransacaoBoiDetalheDTO::new) // Mapeia cada item
                    .collect(Collectors.toList());
        }
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

    public String getContraParte() {
        return contraParte;
    }

    public void setContraParte(String contraParte) {
        this.contraParte = contraParte;
    }

    public TransacaoResponseDTO getTransacaoRelacionada() {
        return transacaoRelacionada;
    }

    public void setTransacaoRelacionada(TransacaoResponseDTO transacaoRelacionada) {
        this.transacaoRelacionada = transacaoRelacionada;
    }

    public List<TransacaoBoiDetalheDTO> getItens() {
        return itens;
    }

    public void setItens(List<TransacaoBoiDetalheDTO> itens) {
        this.itens = itens;
    }
}
