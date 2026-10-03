package bovisApp.firstApp.DTO.boi;

import java.util.Objects;

public class BoiRequestDTO {
    private Integer numero;
    private String raca;
    private String estadoBoi;

    public BoiRequestDTO(Integer numero, String raca, String estadoBoi) {
        this.numero = numero;
        this.raca = raca;
        this.estadoBoi = estadoBoi;
    }

    public BoiRequestDTO() {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoiRequestDTO that = (BoiRequestDTO) o;
        return Objects.equals(numero, that.numero) && Objects.equals(raca, that.raca)  && Objects.equals(estadoBoi, that.estadoBoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, raca, estadoBoi);
    }

    @Override
    public String toString() {
        return "BoiRequestDTO{" +
                "numero=" + numero +
                ", raca='" + raca + '\'' +
                ", estadoBoi='" + estadoBoi + '\'' +
                '}';
    }
}
