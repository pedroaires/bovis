package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.DTO.pesagem.PesagemTransacaoRequestDTO;
import bovisApp.firstApp.exception.pesagem.PesagemInvalidaException;
import bovisApp.firstApp.exception.pesagem.PesagemNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.repository.PesagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PesagemServiceImpl implements PesagemService{
    private final PesagemRepository pesagemRepository;

    private final MedicacaoService medicacaoService;

    private final BoiService boiService;

    @Autowired
    public PesagemServiceImpl(PesagemRepository pesagemRepository, MedicacaoService medicacaoService, BoiService boiService) {
        this.pesagemRepository = pesagemRepository;
        this.medicacaoService = medicacaoService;
        this.boiService = boiService;
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
        Boi boi = boiService.getBoiById(dto.getBoiId());

        String descricao = Optional.ofNullable(dto.getDescricao()).orElse("");
        List<String> medicacoesStr = Optional.ofNullable(dto.getMedicacoes()).orElse(Collections.emptyList());
        List<Medicacao> medicacoes = medicacoesStr.stream()
                .map(medicacaoService::getOrCreateMedicacao)
                .collect(Collectors.toList());


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
    public Pesagem criarPesagemParaTransacao(Boi boi, PesagemTransacaoRequestDTO dto, LocalDate dataTransacao){
        validaPesagemTransacaoDTO(dto);
        if(dataTransacao == null){
            throw new PesagemInvalidaException("Data da pesagem não pode ser nula");
        }
        if(dto.getPeso() == null){
            throw new PesagemInvalidaException("Peso não pode ser nulo");
        }
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String descricao = "Pesagem registrada na transação. (" + dataTransacao.format(formatador) + ")";
        Pesagem pesagem = new Pesagem(
                boi,
                dataTransacao,
                dto.getPeso(),
                descricao,
                Collections.emptyList()
        );
        return pesagemRepository.save(pesagem);
    }


    @Override
    public Pesagem editaPesagem(PesagemRequestDTO dto, Long pesagemId) {
        validaPesagemDTO(dto);

        Boi boi = boiService.getBoiById(dto.getBoiId());
        Pesagem pesagem = getPesagemById(pesagemId);

        String descricao = Optional.ofNullable(dto.getDescricao()).orElse("");
        List<String> medicacoesStr = Optional.ofNullable(dto.getMedicacoes()).orElse(Collections.emptyList());
        List<Medicacao> medicacoes = medicacoesStr.stream().map(medicacaoService::getOrCreateMedicacao).collect(Collectors.toList());

        pesagem.setData(dto.getData());
        pesagem.setPeso(dto.getPeso());
        pesagem.setDescricao(descricao);
        pesagem.setMedicacoes(medicacoes);
        pesagem.setBoi(boi);
        return pesagemRepository.save(pesagem);
    }

    @Override
    public Optional<Pesagem> getUltimaPesagemPorBoiId(Long boiId) {

        return pesagemRepository.findTopByBoiIdOrderByDataDesc(boiId);
    }

    @Override
    public List<Pesagem> getUltimasDuasPesagensPorBoiId(Long boiId) {

        return pesagemRepository.findTop2ByBoiIdOrderByDataDesc(boiId);
    }

    @Override
    public List<Pesagem> cadastraLotePesagem(List<PesagemRequestDTO> dtos) {
        if (dtos == null || dtos.isEmpty()){
            throw new PesagemInvalidaException("Dados de pesagem nulos ou vazios");
        }
        List<Pesagem> pesagens = new ArrayList<>();
        for(PesagemRequestDTO dto : dtos){
            pesagens.add(cadastraPesagem(dto));

        }
        return pesagens;
    }

    public void validaPesagemDTO(PesagemRequestDTO dto){
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

    public void validaPesagemTransacaoDTO(PesagemTransacaoRequestDTO dto){
        if (dto == null){
            throw new PesagemInvalidaException("Pesagem não pode ser nula");
        }
        if (dto.getPeso() == null){
            throw new PesagemInvalidaException("Peso da pesagem não pode ser nulo");
        }
    }

    @Override
    public Pesagem deletaPesagem(Long id) {
        Pesagem pesagem = getPesagemById(id);
        pesagemRepository.delete(pesagem);
        return pesagem;
    }




}
