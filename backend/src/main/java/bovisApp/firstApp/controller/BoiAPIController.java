package bovisApp.firstApp.controller;

import bovisApp.firstApp.DTO.boi.*;
import bovisApp.firstApp.DTO.pesagem.PesagemResponseDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.service.BoiPesagemFacadeService;
import bovisApp.firstApp.service.BoiService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/boi")
public class BoiAPIController {
    private final BoiService boiService;

    private final BoiPesagemFacadeService boiPesagemService;

    @Autowired
    public BoiAPIController(BoiService boiService, BoiPesagemFacadeService boiPesagemService) {
        this.boiService = boiService;
        this.boiPesagemService = boiPesagemService;
    }

    @GetMapping(value = "/listBois")
    @ResponseStatus(HttpStatus.OK)
    public List<BoiResponseDTO> getBois(){
        List<BoiResponseDTO> responseList = boiService.getBois().stream().map(BoiResponseDTO::new).collect(Collectors.toList());
        return responseList;
    }

    @GetMapping(value = "/getBoiById")
    @ResponseStatus(HttpStatus.OK)
    public BoiResponseDTO getBoiById(@RequestParam Long id){
        BoiResponseDTO response = new BoiResponseDTO(boiService.getBoiById(id));
        return response;
    }

    @PostMapping(value = "/cadastraBoi")
    @ResponseStatus(HttpStatus.CREATED)
    public BoiResponseDTO cadastrarBoi(@RequestBody BoiRequestDTO boiRequestDTO){
        BoiResponseDTO response = new BoiResponseDTO(boiService.cadastraBoi(boiRequestDTO));
        return response;
    }

    @PostMapping(value = "/editaBoi")
    @ResponseStatus(HttpStatus.OK)
    public BoiResponseDTO editaBoi(@RequestBody BoiRequestDTO boiRequestDTO, @RequestParam Long boiId){
        BoiResponseDTO response = new BoiResponseDTO(boiService.editaBoi(boiRequestDTO, boiId));
        return response;
    }

    @DeleteMapping(value = "/deletaBoi")
    @ResponseStatus(HttpStatus.OK)
    public BoiResponseDTO deletaBoi(@RequestParam Long id){
        BoiResponseDTO response = new BoiResponseDTO(boiService.deleteBoi(id));
        return response;
    }

    @GetMapping(value = "/searchBois")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoiResponseDTO> searchBois(
            @ModelAttribute BoiFilterRequestDTO filterDTO,
            Pageable pageable
    ){
        Page<Boi> boiPage = boiService.searchBois(filterDTO, pageable);
        return boiPage.map(BoiResponseDTO::new);
    }

    @GetMapping(value = "/bois-pesagens-por-data")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoiComPesagensPorDataDTO> getBoisComPesosPorData(
            @ModelAttribute BoiFilterRequestDTO filterDTO,
            Pageable pageable
    ){
        Page<Boi> boiPage = boiService.getBoisComPesosPorData(filterDTO, pageable);
        return boiPage.map(BoiComPesagensPorDataDTO::new);
    }

    @GetMapping(value = "/ativos-com-peso")
    public List<BoiComUltimaPesagemDTO> listarBoisComUltimaPesagem() {
        return boiPesagemService.getBoisAtivosComUltimaPesagem().stream()
                .map(pair -> BoiComUltimaPesagemDTO.from(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/performance-summary")
    @ResponseStatus(HttpStatus.OK)
    public Page<BoiSumarioPerformanceDTO> getPerformanceSummary(
            @ModelAttribute BoiFilterRequestDTO filterDTO,
            Pageable pageable
    ) {
        return boiPesagemService.getCattlePerformanceSummary(filterDTO, pageable);
    }

    @GetMapping(value = "/{boiId}/weight-history")
    @ResponseStatus(HttpStatus.OK)
    public List<PesagemResponseDTO> getPerformanceSummary(
            @PathVariable Long boiId
    ) {
        System.out.println("boi id:");
        System.out.println(boiId);
        return boiPesagemService.getHistoricoDePesagem(boiId).stream().map(PesagemResponseDTO::new).collect(Collectors.toList());
    }

}
