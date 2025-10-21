package bovisApp.firstApp.DTO.raca;

import bovisApp.firstApp.model.Raca;

public class RacaResponseDTO {

    private Long id;
    private String nome;

    public RacaResponseDTO() {

    }

    public RacaResponseDTO(Long id, String nome) {
        this.id = id;
        this.nome = nome;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static RacaResponseDTO fromRaca(Raca raca){
        RacaResponseDTO dto = new RacaResponseDTO(raca.getId(), raca.getNome());

        return dto;
    }
}
