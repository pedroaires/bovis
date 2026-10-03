package bovisApp.firstApp.service;


import bovisApp.firstApp.DTO.boi.BoiFilterRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoiService {
    List<Boi> getBois();

    Boi getBoiById(Long id);

    Boi cadastraBoi(BoiRequestDTO boiRequestDTO);


    List<Boi> cadastraBoisComprados(List<BoiRequestDTO> bois);

    Boi editaBoi(BoiRequestDTO boiRequestDTO, Long boiId);

    List<Boi> cadastraBoisVendidos(List<Long> boisIds);

    Boi atualizaBoiVendido(Long id);

    Boi deleteBoi(Long id);

    Page<Boi> searchBois(BoiFilterRequestDTO filterDTO, Pageable pageable);

    Page<Boi> getBoisComPesosPorData(BoiFilterRequestDTO filterDTO, Pageable pageable);

    List<Boi> getBoisByEstado(EstadoBoi estadoBoi);
}
