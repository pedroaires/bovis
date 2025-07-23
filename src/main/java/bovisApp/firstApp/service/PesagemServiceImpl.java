package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.exception.pesagem.PesagemInvalidaException;
import bovisApp.firstApp.exception.pesagem.PesagemNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.repository.PesagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PesagemServiceImpl implements PesagemService{
    private final PesagemRepository pesagemRepository;

    private final BoiService boiService;

    private final MedicacaoService medicacaoService;

    @Autowired
    public PesagemServiceImpl(PesagemRepository pesagemRepository, BoiService boiService, MedicacaoService medicacaoService) {
        this.pesagemRepository = pesagemRepository;
        this.boiService = boiService;
        this.medicacaoService = medicacaoService;
    }
    @Override
    public Pesagem getPesagemById(Long id){

        return pesagemRepository.findById(id).orElseThrow(
                () -> new PesagemNaoEncontradaException(id)
        );
    }

    @Override
    public List<Pesagem> getPesagens(){
        return pesagemRepository.findAll();
    }


    @Override
    public List<Pesagem> getPesagemByBoiId(Long boiId){
        return pesagemRepository.findByBoiId((boiId));
    }

    @Override
    public Pesagem cadastraPesagem(PesagemRequestDTO dto) {
        validaPesagemDTO(dto);

        String descricao = Optional.ofNullable(dto.getDescricao()).orElse("");
        List<String> medicacoesStr = Optional.ofNullable(dto.getMedicacoes()).orElse(Collections.emptyList());
        List<Medicacao> medicacoes = medicacoesStr.stream()
                .map(medicacaoService::getOrCreateMedicacao)
                .collect(Collectors.toList());

        Boi boi = boiService.getBoiById(dto.getBoiId());

        Pesagem pesagem = new Pesagem(
                boi,
                dto.getData(),
                dto.getPeso(),
                descricao,
                medicacoes
        );

        return pesagemRepository.save(pesagem);
    }
    @Override
    public Pesagem editaPesagem(PesagemRequestDTO dto, Long pesagemId) {
        Pesagem pesagem = getPesagemById(pesagemId);
        validaPesagemDTO(dto);
        String descricao = Optional.ofNullable(dto.getDescricao()).orElse("");
        List<String> medicacoesStr = Optional.ofNullable(dto.getMedicacoes()).orElse(Collections.emptyList());
        List<Medicacao> medicacoes = medicacoesStr.stream().map(medicacaoService::getOrCreateMedicacao).collect(Collectors.toList());

        Boi boi = boiService.getBoiById(dto.getBoiId());
        pesagem.setData(dto.getData());
        pesagem.setPeso(dto.getPeso());
        pesagem.setDescricao(descricao);
        pesagem.setMedicacoes(medicacoes);
        pesagem.setBoi(boi);
        return pesagemRepository.save(pesagem);
    }

    @Override
    public Optional<Pesagem> getUltimaPesagemPorBoiId(Long boiId) {
        List<Pesagem> pesagens = getPesagemByBoiId(boiId);
        return pesagens.stream().max(Comparator.comparing(Pesagem::getData));
    }

    private void validaPesagemDTO(PesagemRequestDTO dto){
        if (dto == null){
            throw new PesagemInvalidaException("Pesagem não pode ser nula");
        }
        if (dto.getPeso() == null){
            throw new PesagemInvalidaException("Peso da pesagem não pode ser nulo");
        }
        if(dto.getData() == null){
            throw new PesagemInvalidaException("Data da pesagem não pode ser nula");
        }
        if (dto.getBoiId() == null){
            throw new PesagemInvalidaException("Id do boi não pode ser nulo");
        }
    }

    @Override
    public Pesagem deletaPesagem(Long id) {
        Pesagem pesagem = getPesagemById(id);
        pesagemRepository.delete(pesagem);
        return pesagem;
    }



}
