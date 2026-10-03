package bovisApp.firstApp.controller;

import bovisApp.firstApp.DTO.raca.RacaResponseDTO;
import bovisApp.firstApp.service.RacaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/raca")
public class RacaAPIController {

    private final RacaService racaService;

    @Autowired
    public RacaAPIController(RacaService racaService) {
        this.racaService = racaService;
    }

    @GetMapping(value = "/listRacas")
    @ResponseStatus(HttpStatus.OK)
    public List<RacaResponseDTO> getRacas(){
        return racaService.getRacas().stream().map(RacaResponseDTO::fromRaca).collect(Collectors.toList());
    }


}
