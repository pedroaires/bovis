package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiFilterRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiSumarioPerformanceDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface BoiPesagemFacadeService {
    List<Pair<Boi, Optional<Pesagem>>> getBoisAtivosComUltimaPesagem();

    Page<BoiSumarioPerformanceDTO> getCattlePerformanceSummary(BoiFilterRequestDTO filterDTO, Pageable pageable);

    List<Pesagem> getHistoricoDePesagem(Long boiId);
}
