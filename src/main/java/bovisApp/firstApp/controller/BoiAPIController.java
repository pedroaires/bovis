package bovisApp.firstApp.controller;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiResponseDTO;
import bovisApp.firstApp.service.BoiService;
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

    @Autowired
    public BoiAPIController(BoiService boiService) {
        this.boiService = boiService;
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

}
