package bovisApp.firstApp.DTO.boi;

import bovisApp.firstApp.DTO.pesagem.PesagemResponseDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class BoiComPesagensPorDataDTO {
    private Long id;
    private Integer numero;
    private String estadoBoi;
    private String raca;

    private Map<String, PesagemResponseDTO> pesagens;

    public BoiComPesagensPorDataDTO(Boi boi) {
        this.id = boi.getId();
        this.numero = boi.getNumero();
        this.estadoBoi = boi.getEstadoBoi().toString();
        this.raca = boi.getRaca().toString();
        this.pesagens = new HashMap<>();
        if (boi.getPesagens() != null) {
            for (Pesagem pesagem : boi.getPesagens()) {
                String dataStr = pesagem.getData()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString();

                pesagens.put(dataStr, new PesagemResponseDTO(pesagem));
            }
        }


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

    public Map<String, PesagemResponseDTO> getPesagens() {
        return pesagens;
    }

    public void setPesagens(Map<String, PesagemResponseDTO> pesagens) {
        this.pesagens = pesagens;
    }

}
