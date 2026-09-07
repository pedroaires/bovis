package bovisApp.firstApp.DTO.boi;

import bovisApp.firstApp.model.Boi;

import java.util.Objects;

public class BoiResponseDTO {

    private Long id;
    private int numero;

    private Long loteId;
    private String estadoBoi;
    private String raca;

    public BoiResponseDTO(Boi boi){
        this.numero = boi.getNumero();
        this.loteId = boi.getLote().getId();
        this.estadoBoi = boi.getEstadoBoi().toString();
        this.raca = boi.getRaca().getNome();
        this.id = boi.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public String getEstadoBoi() {
        return estadoBoi;
    }

    public void setEstadoBoi(String estadoBoi) {
        this.estadoBoi = estadoBoi;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoiResponseDTO that = (BoiResponseDTO) o;
        return numero == that.numero && Objects.equals(id, that.id) && Objects.equals(loteId, that.loteId) && Objects.equals(estadoBoi, that.estadoBoi) && Objects.equals(raca, that.raca);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, loteId, estadoBoi, raca);
    }
}
