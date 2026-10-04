package bovisApp.firstApp.DTO.pesagem;

import bovisApp.firstApp.model.Pesagem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PesagemDetalhadaDTO {

    private Long id;
    private Long boiId;
    private Integer numeroBoi;
    private String racaBoi;
    private BigDecimal peso;
    private List<String> medicacoes;

    private String descricao;
    private boolean vinculadaTransacao;

    public PesagemDetalhadaDTO() {

    }

    public PesagemDetalhadaDTO(Pesagem pesagem) {
        this.id = pesagem.getId();
        this.medicacoes = pesagem.getMedicacoes()
                .stream().map(medicacao -> medicacao.toString())
                .collect(Collectors.toList());
        this.descricao = pesagem.getDescricao();
        this.vinculadaTransacao = pesagem.isVinculadaTransacao();
        if(pesagem.getBoi() != null){
            this.boiId = pesagem.getBoi().getId();
            this.racaBoi = pesagem.getBoi().getRaca().toString();
            this.peso = pesagem.getPeso();
            this.numeroBoi = pesagem.getBoi().getNumero();
        }

    }

    public PesagemDetalhadaDTO(Long id, Long boiId, Integer numeroBoi, String racaBoi, BigDecimal peso, List<String> medicacoes, String descricao, boolean vinculadaTransacao) {
        this.id = id;
        this.boiId = boiId;
        this.numeroBoi = numeroBoi;
        this.racaBoi = racaBoi;
        this.peso = peso;
        this.medicacoes = medicacoes;
        this.descricao = descricao;
        this.vinculadaTransacao = vinculadaTransacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoiId() {
        return boiId;
    }

    public void setBoiId(Long boiId) {
        this.boiId = boiId;
    }

    public Integer getNumeroBoi() {
        return numeroBoi;
    }

    public void setNumeroBoi(Integer numeroBoi) {
        this.numeroBoi = numeroBoi;
    }

    public String getRacaBoi() {
        return racaBoi;
    }

    public void setRacaBoi(String racaBoi) {
        this.racaBoi = racaBoi;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public List<String> getMedicacoes() {
        return medicacoes;
    }

    public void setMedicacoes(List<String> medicacoes) {
        this.medicacoes = medicacoes;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isVinculadaTransacao() {
        return vinculadaTransacao;
    }

    public void setVinculadaTransacao(boolean vinculadaTransacao) {
        this.vinculadaTransacao = vinculadaTransacao;
    }
}
