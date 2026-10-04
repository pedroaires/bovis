package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiFilterRequestDTO;
import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.exception.boi.BoiInvalidoException;
import bovisApp.firstApp.exception.boi.BoiNaoEncontradoException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Raca;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import bovisApp.firstApp.repository.BoiRepository;
import bovisApp.firstApp.specification.BoiSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoiServiceImpl implements BoiService {

    private final BoiRepository boiRepository;
    private final RacaService racaService;


    @Autowired
    public BoiServiceImpl(
            BoiRepository boiRepository,
            RacaService racaService
    )
    {
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
        if(dto.getEstadoBoi() == null || dto.getEstadoBoi().isEmpty()){
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

    @Override
    public Boi atualizaBoiVendido(Long id){
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
    private Specification<Boi> buildSpecification(BoiFilterRequestDTO filterDTO){
        Specification<Boi> spec = Specification.where(null);
        if (filterDTO.getRacaId() != null){
            spec = spec.and(BoiSpecification.hasRaca(filterDTO.getRacaId()));
        }
        if (filterDTO.getNumeroBoi() != null){
            spec = spec.and(BoiSpecification.hasNumero(filterDTO.getNumeroBoi()));
        }
        if (filterDTO.getEstadoBoi() != null){
            EstadoBoi estadoBoi = parseEstadoBoi(filterDTO.getEstadoBoi());
            spec = spec.and(BoiSpecification.hasEstado(estadoBoi));
        }
        if(filterDTO.getPesoMin() != null || filterDTO.getPesoMax() != null){
            spec = spec.and(BoiSpecification.hasPesagemBetween(
                    filterDTO.getPesoMin(), filterDTO.getPesoMax()
            ));
        }
        if (filterDTO.getDataPesagemInicio() != null || filterDTO.getDataPesagemFim() != null){
            spec = spec.and(BoiSpecification.hasPesagemDateBetween(
                    filterDTO.getDataPesagemInicio(),
                    filterDTO.getDataPesagemFim()
            ));
        }
        if (filterDTO.getDataCompraInicio() != null || filterDTO.getDataCompraFim() != null){
            spec = spec.and(BoiSpecification.hasTransacaoDataBetween(
                    filterDTO.getDataCompraInicio(),
                    filterDTO.getDataCompraFim(),
                    TransacaoOperacao.COMPRA
            ));
        }
        return spec;
    }
    @Override
    public Page<Boi> searchBois(BoiFilterRequestDTO filterDTO, Pageable pageable) {
        Specification<Boi> spec = buildSpecification(filterDTO);

        return boiRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Boi> getBoisComPesosPorData(BoiFilterRequestDTO filterDTO, Pageable pageable) {
        Specification<Boi> spec = buildSpecification(filterDTO);
        spec = spec.and(BoiSpecification.fetchPesagens());

        return boiRepository.findAll(spec, pageable);
    }

    @Override
    public List<Boi> getBoisByEstado(EstadoBoi estadoBoi){
        return boiRepository.findAllByEstadoBoi(estadoBoi);
    }


}
