package bovisApp.firstApp.DTO.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EditaTransacaoRequestDTO {
    private BigDecimal valor;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
    private String descricao;
    private String modeloTransacao;
    private String contraparte;

    public EditaTransacaoRequestDTO() {

    }

    public EditaTransacaoRequestDTO(BigDecimal valor, LocalDate data, String descricao, String modeloTransacao, String contraparte) {
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.modeloTransacao = modeloTransacao;
        this.contraparte = contraparte;
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

    public String getModeloTransacao() {
        return modeloTransacao;
    }

    public void setModeloTransacao(String modeloTransacao) {
        this.modeloTransacao = modeloTransacao;
    }

    public String getContraparte() {
        return contraparte;
    }

    public void setContraparte(String contraparte) {
        this.contraparte = contraparte;
    }
}
