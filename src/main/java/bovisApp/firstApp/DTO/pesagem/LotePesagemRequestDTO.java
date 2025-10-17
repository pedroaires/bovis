package bovisApp.firstApp.DTO.pesagem;

import java.util.List;

public class LotePesagemRequestDTO {
    private List<PesagemRequestDTO> pesagens;

    public LotePesagemRequestDTO() {

    }
    public LotePesagemRequestDTO(List<PesagemRequestDTO> pesagens) {
        this.pesagens = pesagens;
    }

    public List<PesagemRequestDTO> getPesagens() {
        return pesagens;
    }

    public void setPesagens(List<PesagemRequestDTO> pesagens) {
        this.pesagens = pesagens;
    }
}
