package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.util.Pair;

import java.util.List;

public interface BoiPesagemFacadeService {
    List<Pair<Boi, Pesagem>> getBoisAtivosComUltimaPesagem();

    Pesagem cadastraPesagem(PesagemRequestDTO pesagemRequestDTO);

    Pesagem editaPesagem(PesagemRequestDTO pesagemRequestDTO, Long pesagemId);
}
