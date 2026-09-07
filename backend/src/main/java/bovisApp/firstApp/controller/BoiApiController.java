package bovisApp.firstApp.controller;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiResponseDTO;
import bovisApp.firstApp.service.BoiService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class BoiApiController {
    private final BoiService boiService;

    @Autowired
    public BoiApiController(BoiService boiService) {
        this.boiService = boiService;
    }

    @GetMapping(value = "/listBois")
    @ResponseStatus(HttpStatus.OK)
    public List<BoiResponseDTO> getBoi(){
        return boiService.getBois();
    }

    @PostMapping(value = "/cadastraBoi")
    @ResponseStatus(HttpStatus.CREATED)
    public BoiResponseDTO cadastrarBoi(@RequestBody BoiRequestDTO boiRequestDTO){
        BoiResponseDTO boiResponseDTO = boiService.cadastraBoi(boiRequestDTO);
        return boiResponseDTO;
    }

    @PostMapping(value = "/editaBoi")
    @ResponseStatus(HttpStatus.OK)
    public BoiResponseDTO editaBoi(@RequestBody BoiRequestDTO boiRequestDTO, @RequestParam Long boiId){
        BoiResponseDTO boiResponseDTO = boiService.editaBoi(boiRequestDTO, boiId);
        return boiResponseDTO;
    }

    @DeleteMapping(value = "/deletaBoi")
    @ResponseStatus(HttpStatus.OK)
    public BoiResponseDTO deletaBoi(@RequestParam Long id){
        BoiResponseDTO boiResponseDTO = boiService.deleteBoi(id);
        return boiResponseDTO;
    }

}
