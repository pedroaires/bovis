package bovisApp.firstApp.service;


import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

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
    public Pesagem cadastraPesagem(PesagemRequestDTO pesagemRequestDTO) {
        Boi boi = boiService.getBoiById(pesagemRequestDTO.getBoiId());
        return pesagemService.cadastraPesagem(pesagemRequestDTO, boi);
    }

    @Override
    public Pesagem editaPesagem(PesagemRequestDTO pesagemRequestDTO, Long pesagemId) {
        Boi boi = boiService.getBoiById(pesagemRequestDTO.getBoiId());
        return pesagemService.editaPesagem(pesagemRequestDTO, pesagemId, boi);
    }
}
