package bovisApp.firstApp.DTO.boi;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BoiFilterRequestDTO {
    private Long racaId;
    private BigDecimal pesoMin;
    private BigDecimal pesoMax;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPesagemInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPesagemFim;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCompraInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCompraFim;

    private Integer numeroBoi;
    private String estadoBoi;

    public BoiFilterRequestDTO() {

    }

    public BoiFilterRequestDTO(Long racaId, BigDecimal pesoMin, BigDecimal pesoMax, LocalDate dataPesagemInicio, LocalDate dataPesagemFim, LocalDate dataCompraInicio, LocalDate dataCompraFim, Integer numeroBoi, String estadoBoi) {
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

    public BigDecimal getPesoMin() {
        return pesoMin;
    }

    public void setPesoMin(BigDecimal pesoMin) {
        this.pesoMin = pesoMin;
    }

    public BigDecimal getPesoMax() {
        return pesoMax;
    }

    public void setPesoMax(BigDecimal pesoMax) {
        this.pesoMax = pesoMax;
    }

    public LocalDate getDataPesagemInicio() {
        return dataPesagemInicio;
    }

    public void setDataPesagemInicio(LocalDate dataPesagemInicio) {
        this.dataPesagemInicio = dataPesagemInicio;
    }

    public LocalDate getDataPesagemFim() {
        return dataPesagemFim;
    }

    public void setDataPesagemFim(LocalDate dataPesagemFim) {
        this.dataPesagemFim = dataPesagemFim;
    }

    public LocalDate getDataCompraInicio() {
        return dataCompraInicio;
    }

    public void setDataCompraInicio(LocalDate dataCompraInicio) {
        this.dataCompraInicio = dataCompraInicio;
    }

    public LocalDate getDataCompraFim() {
        return dataCompraFim;
    }

    public void setDataCompraFim(LocalDate dataCompraFim) {
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
