package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
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
        List<Boi> responseList = boiRepository.findAll();
        return responseList;
    }

    @Override
    public Boi getBoiById(Long id){
        Boi boi = boiRepository.findById(id).orElseThrow(() -> new BoiNaoEncontradoException("Boi não encontrado"));
        return boi;
    }

    @Override
    public Boi cadastraBoi(BoiRequestDTO boiRequestDTO) throws IllegalArgumentException{
        Integer numero = Optional.ofNullable(boiRequestDTO.getNumero()).orElseThrow(
                () -> new BoiInvalidoException("Número do boi não pode ser nulo")
        );

        EstadoBoi estadoBoi;
        try{
            estadoBoi = EstadoBoi.getEstadoBoi(boiRequestDTO.getEstadoBoi());
        } catch (IllegalArgumentException e){
            throw new BoiInvalidoException("Estado do boi é inválido. Use 'na_fazenda', 'vendido' ou 'morto'");
        }

        Raca raca = racaService.getRacaByNome(boiRequestDTO.getRaca());

        Boi boi = new Boi(numero, raca, estadoBoi);
        boiRepository.save(boi);
        return boi;
    }

    @Override
    public List<Boi> cadastraBoisComprados(List<BoiRequestDTO> boisDTO) {
        List<Boi> bois = boisDTO.stream().map(this::cadastraBoi).collect(Collectors.toList());
        return bois;
    }

    @Override
    public Boi editaBoi(BoiRequestDTO boiRequestDTO, Long id){
        Boi boi = boiRepository.findById(id).orElseThrow(() -> new BoiNaoEncontradoException("Boi não encontrado"));
        Integer numero = Optional.ofNullable(boiRequestDTO.getNumero()).orElseThrow(
                () -> new BoiInvalidoException("Número do boi não pode ser nulo")
        );
        EstadoBoi estadoBoi;
        try{
            estadoBoi = EstadoBoi.getEstadoBoi(boiRequestDTO.getEstadoBoi());
        } catch (IllegalArgumentException e){
            throw new BoiInvalidoException("Estado do boi é inválido. Use 'na_fazenda', 'vendido' ou 'morto'");
        }
        Raca raca = racaService.getRacaByNome(boiRequestDTO.getRaca());

        boi.setNumero(numero);
        boi.setRaca(raca);
        boi.setEstadoBoi(estadoBoi);
        boiRepository.save(boi);
        return boi;
    }

    @Override
    public List<Boi> cadastraBoisVendidos(List<Long> boisIds){
        if(boisIds == null || boisIds.isEmpty())
            throw new IllegalArgumentException("Ids dos bois não podem ser nulos ou estarem vazio");

        List<Boi> bois = boisIds.stream().map(this::atualizaBoiVendido).collect(Collectors.toList());

        return bois;
    }

    private Boi atualizaBoiVendido(Long boiId){
        Boi boi = boiRepository.findById(boiId).orElseThrow(
                () -> new BoiNaoEncontradoException("Boi não encontrado")
        );
        EstadoBoi estadoBoi = boi.getEstadoBoi();
        if (estadoBoi.equals(EstadoBoi.NA_FAZENDA)) {
            boi.setEstadoBoi(EstadoBoi.VENDIDO);
        }
        else {
            throw new IllegalArgumentException("Boi só pode ser vendido se estiver NA_FAZENDA");
        }
        boi = boiRepository.save(boi);
        return boi;

    }



    @Override
    public Boi deleteBoi(Long id){
        Boi boi = boiRepository.findById(id).orElseThrow(() -> new BoiNaoEncontradoException("Boi não encontrado"));
        boiRepository.delete(boi);
        return boi;
    }


}
