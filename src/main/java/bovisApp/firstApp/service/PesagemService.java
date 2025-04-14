package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.model.Pesagem;

import java.util.List;

public interface PesagemService {
    Pesagem getPesagemById(Long id);

    List<Pesagem> getPesagens();

    List<Pesagem> getPesagemByBoiId(Long boiId);

    Pesagem cadastraPesagem(PesagemRequestDTO pesagemRequestDTO);

    Pesagem deletaPesagem(Long id);

    Pesagem editaPesagem(PesagemRequestDTO pesagemRequestDTO, Long pesagemId);
}
