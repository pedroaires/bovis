package bovisApp.firstApp.DTO.boi;

import java.math.BigDecimal;

public class BoiSumarioPerformanceDTO {
    private Long id;
    private Integer numero;
    private String raca;
    private String estadoBoi;
    private BigDecimal pesoAtual;
    private String ultimaPesagem;
    private BigDecimal gmd;

    public BoiSumarioPerformanceDTO() {

    }

    public BoiSumarioPerformanceDTO(Long id, Integer numero, String raca, String estadoBoi, BigDecimal pesoAtual, String ultimaPesagem, BigDecimal gmd) {
        this.id = id;
        this.numero = numero;
        this.raca = raca;
        this.estadoBoi = estadoBoi;
        this.pesoAtual = pesoAtual;
        this.ultimaPesagem = ultimaPesagem;
        this.gmd = gmd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getEstadoBoi() {
        return estadoBoi;
    }

    public void setEstadoBoi(String estadoBoi) {
        this.estadoBoi = estadoBoi;
    }

    public BigDecimal getPesoAtual() {
        return pesoAtual;
    }

    public void setPesoAtual(BigDecimal pesoAtual) {
        this.pesoAtual = pesoAtual;
    }

    public String getUltimaPesagem() {
        return ultimaPesagem;
    }

    public void setUltimaPesagem(String ultimaPesagem) {
        this.ultimaPesagem = ultimaPesagem;
    }

    public BigDecimal getGmd() {
        return gmd;
    }

    public void setGmd(BigDecimal gmd) {
        this.gmd = gmd;
    }
}
