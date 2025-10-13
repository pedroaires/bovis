package bovisApp.firstApp.DTO.boi;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;

import java.time.ZoneId;
import java.util.Optional;

public class BoiComUltimaPesagemDTO {
    private Long id;
    private Integer numero;
    private String estadoBoi;
    private String raca;
    private String dataUltimaPesagem;
    private Double peso;

    public BoiComUltimaPesagemDTO(Long id, Integer numero, String estadoBoi, String raca, String dataUltimaPesagem, Double peso) {
        this.id = id;
        this.numero = numero;
        this.estadoBoi = estadoBoi;
        this.raca = raca;
        this.dataUltimaPesagem = dataUltimaPesagem;
        this.peso = peso;
    }

    public static BoiComUltimaPesagemDTO from(Boi boi, Optional<Pesagem> pesagemOptional) {
        String data = pesagemOptional.map(p -> p.getData().toString()).orElse(null);
        Double peso = pesagemOptional.map(Pesagem::getPeso).orElse(null);

        return new BoiComUltimaPesagemDTO(
                boi.getId(),
                boi.getNumero(),
                boi.getEstadoBoi().toString(),
                boi.getRaca().toString(),
                data,
                peso
        );
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

    public String getDataUltimaPesagem() {
        return dataUltimaPesagem;
    }

    public void setDataUltimaPesagem(String dataUltimaPesagem) {
        this.dataUltimaPesagem = dataUltimaPesagem;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }
}
