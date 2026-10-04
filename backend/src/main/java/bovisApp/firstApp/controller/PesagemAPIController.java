package bovisApp.firstApp.controller;

import bovisApp.firstApp.DTO.pesagem.*;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.service.BoiPesagemFacadeService;
import bovisApp.firstApp.service.BoiPesagemFacadeServiceImpl;
import bovisApp.firstApp.service.PesagemService;
import bovisApp.firstApp.service.PesagemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/pesagem")
public class PesagemAPIController {
    private final PesagemService pesagemService;

    @Autowired
    public PesagemAPIController(PesagemService pesagemService) {
        this.pesagemService = pesagemService;
    }

    @GetMapping(value = "/listPesagens")
    public List<PesagemResponseDTO> listPesagens(){
        List<PesagemResponseDTO> pesagens = pesagemService.getPesagens().stream().map(PesagemResponseDTO::new).collect(Collectors.toList());
        return pesagens;
    }

    @GetMapping(value = "/getPesagemByBoiId")
    public List<PesagemResponseDTO> getPesagemByBoiId(Long boiId){
        List<PesagemResponseDTO> pesagemResponseDTO = pesagemService.getPesagemByBoiId(boiId).stream().map(PesagemResponseDTO::new).collect(Collectors.toList());
        return pesagemResponseDTO;
    }

    @PostMapping(value = "/cadastraPesagem")
    public PesagemResponseDTO cadastraPesagem(@RequestBody PesagemRequestDTO pesagemRequestDTO){
        PesagemResponseDTO pesagemResponseDTO = new PesagemResponseDTO(pesagemService.cadastraPesagem(pesagemRequestDTO));
        return pesagemResponseDTO;
    }
    @PostMapping(value = "/cadastraLotePesagem")
    public List<PesagemResponseDTO> cadastraLotePesagem(@RequestBody LotePesagemRequestDTO loteRequest) {


        List<Pesagem> pesagensSalvas = pesagemService.cadastraLotePesagem(loteRequest.getPesagens());

        return pesagensSalvas.stream()
                .map(PesagemResponseDTO::new)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/editaPesagem/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PesagemResponseDTO editaPesagem(
            @PathVariable Long id,
            @RequestBody PesagemRequestDTO pesagemRequestDTO
            )
    {
        PesagemResponseDTO pesagemResponseDTO = new PesagemResponseDTO(pesagemService.editaPesagem(pesagemRequestDTO, id));
        return pesagemResponseDTO;
    }

    @GetMapping(value = "/agrupadas")
    @ResponseStatus(HttpStatus.OK)
    public List<PesagemAgrupadaDTO> listPesagensAgrupadasPorData()
    {
        return pesagemService.getAgrupadasPorData();
    }

    @GetMapping(value = "/agrupadas/{data}")
    @ResponseStatus(HttpStatus.OK)
    public List<PesagemDetalhadaDTO> pesagensPorDataDetalhado(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate data
            )
    {
        List<PesagemDetalhadaDTO> response = pesagemService.getPesagensPorDataDetalhada(data)
                .stream().map(PesagemDetalhadaDTO::new).collect(Collectors.toList());
        return response;

    }

    @PutMapping(value = "/agrupadas/{data}")
    @ResponseStatus(HttpStatus.OK)
    public List<PesagemDetalhadaDTO> editaPesagensEmLotePorData(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate data,
            @RequestBody
            EditaPesagemLoteRequestDTO dto
    ){
        List<PesagemDetalhadaDTO> response = pesagemService.editaPesagensEmLotePorData(data, dto.getNovaData())
                .stream().map(PesagemDetalhadaDTO::new).collect(Collectors.toList());
        return response;
    }

    @DeleteMapping(value = "/agrupadas/{data}")
    @ResponseStatus(HttpStatus.OK)
    public void deletaPesagensEmLotePorData(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate data
    ){
        pesagemService.deletaPesagensEmLotePorData(data);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletaPesagem(@PathVariable Long id){
        pesagemService.deletaPesagem(id);
    }

    @PostMapping("/matriz")
    @ResponseStatus(HttpStatus.OK)
    public MatrizPesagemResponseDTO gerarMatrizPeso(
            @RequestBody MatrizPesagemRequestDTO request) {

        MatrizPesagemResponseDTO response = pesagemService.gerarMatrizPeso(request);
        return response;
    }
}
