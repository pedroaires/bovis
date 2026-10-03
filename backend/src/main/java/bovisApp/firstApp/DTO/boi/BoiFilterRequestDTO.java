package bovisApp.firstApp.DTO.boi;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BoiFilterRequestDTO {
    private Long racaId;
    private Double pesoMin;
    private Double pesoMax;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataPesagemInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataPesagemFim;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataCompraInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataCompraFim;

    private Integer numeroBoi;
    private String estadoBoi;

    public BoiFilterRequestDTO() {

    }

    public BoiFilterRequestDTO(Long racaId, Double pesoMin, Double pesoMax, Date dataPesagemInicio, Date dataPesagemFim, Date dataCompraInicio, Date dataCompraFim, Integer numeroBoi, String estadoBoi) {
        this.racaId = racaId;
        this.pesoMin = pesoMin;
        this.pesoMax = pesoMax;
        this.dataPesagemInicio = dataPesagemInicio;
        this.dataPesagemFim = dataPesagemFim;
        this.dataCompraInicio = dataCompraInicio;
        this.dataCompraFim = dataCompraFim;
        this.numeroBoi = numeroBoi;
        this.estadoBoi = estadoBoi;
    }

    public Long getRacaId() {
        return racaId;
    }

    public void setRacaId(Long racaId) {
        this.racaId = racaId;
    }

    public Double getPesoMin() {
        return pesoMin;
    }

    public void setPesoMin(Double pesoMin) {
        this.pesoMin = pesoMin;
    }

    public Double getPesoMax() {
        return pesoMax;
    }

    public void setPesoMax(Double pesoMax) {
        this.pesoMax = pesoMax;
    }

    public Date getDataPesagemInicio() {
        return dataPesagemInicio;
    }

    public void setDataPesagemInicio(Date dataPesagemInicio) {
        this.dataPesagemInicio = dataPesagemInicio;
    }

    public Date getDataPesagemFim() {
        return dataPesagemFim;
    }

    public void setDataPesagemFim(Date dataPesagemFim) {
        this.dataPesagemFim = dataPesagemFim;
    }

    public Date getDataCompraInicio() {
        return dataCompraInicio;
    }

    public void setDataCompraInicio(Date dataCompraInicio) {
        this.dataCompraInicio = dataCompraInicio;
    }

    public Date getDataCompraFim() {
        return dataCompraFim;
    }

    public void setDataCompraFim(Date dataCompraFim) {
        this.dataCompraFim = dataCompraFim;
    }

    public Integer getNumeroBoi() {
        return numeroBoi;
    }

    public void setNumeroBoi(Integer numeroBoi) {
        this.numeroBoi = numeroBoi;
    }

    public String getEstadoBoi() {
        return estadoBoi;
    }

    public void setEstadoBoi(String estadoBoi) {
        this.estadoBoi = estadoBoi;
    }
}
