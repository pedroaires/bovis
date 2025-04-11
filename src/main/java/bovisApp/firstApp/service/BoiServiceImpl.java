package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.exception.EntidadeInvalidaException;
import bovisApp.firstApp.exception.boi.BoiInvalidoException;
import bovisApp.firstApp.exception.boi.BoiNaoEncontradoException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Raca;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.repository.BoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoiServiceImpl implements BoiService {
    private final BoiRepository boiRepository;
    private final RacaService racaService;

    @Autowired
    public BoiServiceImpl(BoiRepository boiRepository, RacaService racaService) {
        this.boiRepository = boiRepository;
        this.racaService = racaService;
    }

    @Override
    public List<Boi> getBois() {
        return boiRepository.findAll();
    }

    @Override
    public Boi getBoiById(Long id){
        return boiRepository.findById(id)
                .orElseThrow(() -> new BoiNaoEncontradoException(id));
    }

    @Override
    public Boi cadastraBoi(BoiRequestDTO dto) throws IllegalArgumentException{
        validaBoiDTO(dto);
        Raca raca = racaService.getRacaByNome(dto.getRaca());
        EstadoBoi estadoBoi = parseEstadoBoi(dto.getEstadoBoi());
        Boi boi = new Boi(
                dto.getNumero(),
                raca,
                estadoBoi
        );

        return boiRepository.save(boi);
    }

    private void validaBoiDTO(BoiRequestDTO dto) {
        if(dto == null){
            throw new BoiInvalidoException("Boi não pode ser nulo");
        }
        if (dto.getNumero() == null){
            throw new BoiInvalidoException("Número do boi não pode ser nulo");
        }
        if(dto.getEstadoBoi() == null | dto.getEstadoBoi().isEmpty()){
            throw new BoiInvalidoException("Estado do boi não pode ser nulo");
        }
    }

    private EstadoBoi parseEstadoBoi(String estado) {
        try {
            return EstadoBoi.getEstadoBoi(estado);
        } catch (IllegalArgumentException e) {
            throw new BoiInvalidoException("Estado do boi inválido. Use: 'na_fazenda', 'vendido' ou 'morto'");
        }
    }

    @Override
    public List<Boi> cadastraBoisComprados(List<BoiRequestDTO> boisDTO) {
        return boisDTO.stream().map(this::cadastraBoi).collect(Collectors.toList());
    }

    @Override
    public Boi editaBoi(BoiRequestDTO dto, Long id){
        Boi boi = getBoiById(id);
        validaBoiDTO(dto);

        Raca raca = racaService.getRacaByNome(dto.getRaca());
        EstadoBoi estadoBoi = parseEstadoBoi(dto.getEstadoBoi());

        boi.setNumero(dto.getNumero());
        boi.setRaca(raca);
        boi.setEstadoBoi(estadoBoi);

        return boiRepository.save(boi);
    }

    @Override
    public List<Boi> cadastraBoisVendidos(List<Long> boisIds){
        if(boisIds == null || boisIds.isEmpty())
            throw new BoiInvalidoException("Ids dos bois não podem ser nulos ou estarem vazio");

        return boisIds.stream().map(this::atualizaBoiVendido).collect(Collectors.toList());
    }

    private Boi atualizaBoiVendido(Long id){
        Boi boi = getBoiById(id);
        EstadoBoi estadoBoi = boi.getEstadoBoi();
        if (!estadoBoi.equals(EstadoBoi.NA_FAZENDA)) {
            throw new BoiInvalidoException("Boi só pode ser vendido se estiver NA_FAZENDA");
        }
        boi.setEstadoBoi(EstadoBoi.VENDIDO);
        return boiRepository.save(boi);

    }



    @Override
    public Boi deleteBoi(Long id){
        Boi boi = getBoiById(id);
        boiRepository.delete(boi);
        return boi;
    }


}
