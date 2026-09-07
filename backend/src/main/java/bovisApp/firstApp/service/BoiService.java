package bovisApp.firstApp.service;


import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiResponseDTO;
import bovisApp.firstApp.model.Boi;

import java.util.List;

public interface BoiService {
    List<BoiResponseDTO> getBois();

    BoiResponseDTO getBoiById(Long id);

    BoiResponseDTO cadastraBoi(BoiRequestDTO boiRequestDTO);

    BoiResponseDTO editaBoi(BoiRequestDTO boiRequestDTO, Long boiId);

    BoiResponseDTO deleteBoi(Long id);
}
