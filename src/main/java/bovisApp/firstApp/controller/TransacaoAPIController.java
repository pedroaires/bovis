package bovisApp.firstApp.controller;


import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.TransacaoDetalhadaCompletaDTO;
import bovisApp.firstApp.DTO.transacao.TransacaoResponseDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/transacao")
public class TransacaoAPIController {
    private final TransacaoService transacaoService;

    @Autowired
    public TransacaoAPIController(TransacaoService transacaoService){
        this.transacaoService = transacaoService;
    }

    @GetMapping(value="/listTransacoes")
    public List<TransacaoResponseDTO> getTransacoes(){
        List<TransacaoResponseDTO> responseList = this.transacaoService.getTransacoes().stream().map(TransacaoResponseDTO::new).collect(Collectors.toList());
        return responseList;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TransacaoResponseDTO getTransacaoById(@PathVariable Long id){
        TransacaoResponseDTO response = new TransacaoResponseDTO(transacaoService.getTransacaoById(id));
        return response;
    }

    @GetMapping(value = "/{id}/detalhes")
    @ResponseStatus(HttpStatus.OK)
    public TransacaoDetalhadaCompletaDTO getTransacaoDetalhesById(
            @PathVariable Long id
    ){
        TransacaoDetalhadaCompletaDTO response = new TransacaoDetalhadaCompletaDTO(transacaoService.getTransacaoDetalhadaById(id));
        return response;
    }


    @PostMapping(value = "/cadastraCompra")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponseDTO cadastraCompra(@RequestBody CompraRequestDTO compraRequestDTO){
        TransacaoResponseDTO response = new TransacaoResponseDTO(transacaoService.registraCompra(compraRequestDTO));
        return response;
    }

    @PostMapping(value = "/cadastraVenda")
    @ResponseStatus(HttpStatus.CREATED)
    public TransacaoResponseDTO cadastraVenda(@RequestBody VendaRequestDTO vendaRequestDTO){
        TransacaoResponseDTO response = new TransacaoResponseDTO(transacaoService.registraVenda(vendaRequestDTO));
        return response;
    }


    @DeleteMapping(value = "/deleteTransacao")
    @ResponseStatus(HttpStatus.OK)
    public TransacaoResponseDTO deletaTransacao(@RequestParam Long id){
        TransacaoResponseDTO response = new TransacaoResponseDTO(transacaoService.deletaTransacao(id));
        return response;
    }
}
