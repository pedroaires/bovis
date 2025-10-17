package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface BoiPesagemFacadeService {
    List<Pair<Boi, Optional<Pesagem>>> getBoisAtivosComUltimaPesagem();

}
