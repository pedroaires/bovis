package bovisApp.firstApp.service;


import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiResponseDTO;
import bovisApp.firstApp.model.Boi;

import java.util.List;

public interface BoiService {
    List<Boi> getBois();

    Boi getBoiById(Long id);

    Boi cadastraBoi(BoiRequestDTO boiRequestDTO);


    List<Boi> cadastraBoisComprados(List<BoiRequestDTO> bois);

    Boi editaBoi(BoiRequestDTO boiRequestDTO, Long boiId);

    List<Boi> cadastraBoisVendidos(List<Long> boisIds);

    Boi deleteBoi(Long id);
}
