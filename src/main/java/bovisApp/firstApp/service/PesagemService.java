package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;

import java.util.List;
import java.util.Optional;

public interface PesagemService {
    Pesagem getPesagemById(Long id);

    List<Pesagem> getPesagens();

    List<Pesagem> getPesagemByBoiId(Long boiId);

    Pesagem cadastraPesagem(PesagemRequestDTO pesagemRequestDTO, Boi boi);

    Pesagem deletaPesagem(Long id);

    Pesagem editaPesagem(PesagemRequestDTO pesagemRequestDTO, Long pesagemId, Boi boi);

    Optional<Pesagem> getUltimaPesagemPorBoiId(Long boiId);
}
