package bovisApp.firstApp.service;


import bovisApp.firstApp.DTO.boi.BoiFilterRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiSumarioPerformanceDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.util.PerformanceCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoiPesagemFacadeServiceImpl implements BoiPesagemFacadeService{

    private final BoiService boiService;
    private final PesagemService pesagemService;

    public BoiPesagemFacadeServiceImpl(BoiService boiService, PesagemService pesagemService) {
        this.boiService = boiService;
        this.pesagemService = pesagemService;
    }

    @Override
    public List<Pair<Boi, Optional<Pesagem>>> getBoisAtivosComUltimaPesagem() {

        return boiService.getBoisByEstado(EstadoBoi.NA_FAZENDA).stream()
                .map(
                        boi -> Pair.of(boi, pesagemService.getUltimaPesagemPorBoiId(boi.getId()))
                ).collect(Collectors.toList());
    }

    @Override
    public Page<BoiSumarioPerformanceDTO> getCattlePerformanceSummary(BoiFilterRequestDTO filterDTO, Pageable pageable) {
        Page<Boi> boiPage = boiService.searchBois(filterDTO, pageable);

        List<BoiSumarioPerformanceDTO> dtoList = boiPage.getContent().stream()
                .map(boi -> {
                    List<Pesagem> ultimasPesagens = pesagemService.getUltimasDuasPesagensPorBoiId(boi.getId());
                    return buildSummaryDTO(boi, ultimasPesagens);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, boiPage.getPageable(), boiPage.getTotalElements());
    }

    @Override
    public List<Pesagem> getHistoricoDePesagem(Long boiId) {
        return pesagemService.getPesagemByBoiId(boiId);
    }

    private BoiSumarioPerformanceDTO buildSummaryDTO(Boi boi, List<Pesagem> pesagens) {
        BoiSumarioPerformanceDTO dto = new BoiSumarioPerformanceDTO();

        dto.setId(boi.getId());
        dto.setNumero(boi.getNumero());
        dto.setRaca(boi.getRaca().toString());
        dto.setEstadoBoi(boi.getEstadoBoi().toString());

        if (!pesagens.isEmpty()) {
            // A primeira pesagem na lista (índice 0) é a mais recente
            Pesagem ultimaPesagem = pesagens.get(0);

            dto.setPesoAtual(ultimaPesagem.getPeso());
            dto.setUltimaPesagem(ultimaPesagem.getData().toString());

            // Calcula o GMD se houver pelo menos duas pesagens
            if (pesagens.size() >= 2) {
                Pesagem penultimaPesagem = pesagens.get(1);

                BigDecimal gmd = PerformanceCalculator.calculateGMD(
                        ultimaPesagem.getPeso(), ultimaPesagem.getData(),
                        penultimaPesagem.getPeso(), penultimaPesagem.getData()
                );
                dto.setGmd(gmd);
            } else {
                dto.setGmd(BigDecimal.ZERO);
            }
        } else {

            dto.setPesoAtual(null);
            dto.setUltimaPesagem(null);
            dto.setGmd(BigDecimal.ZERO);
        }

        return dto;
    }
}

