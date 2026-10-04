package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.*;
import bovisApp.firstApp.exception.OperacaoConflitanteException;
import bovisApp.firstApp.exception.pesagem.PesagemInvalidaException;
import bovisApp.firstApp.exception.pesagem.PesagemNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.repository.PesagemRepository;
import bovisApp.firstApp.util.PerformanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
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
        String descricao = "Pesagem registrada na transação.";
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
    @Transactional
    public Pesagem editaPesagem(PesagemRequestDTO dto, Long pesagemId) {
        validaPesagemDTO(dto);

        Pesagem pesagem = getPesagemById(pesagemId);
        validaPesagemNaoVinculada(pesagem);
        Boi boi = boiService.getBoiById(dto.getBoiId());

        pesagem.setData(dto.getData());
        pesagem.setPeso(dto.getPeso());
        pesagem.setBoi(boi);
        if (dto.getDescricao() != null) {
            pesagem.setDescricao(dto.getDescricao());
        }
        if (dto.getMedicacoes() != null) {
            List<Medicacao> medicacoes = dto.getMedicacoes().stream()
                    .map(medicacaoService::getOrCreateMedicacao)
                    .collect(Collectors.toList());
            pesagem.setMedicacoes(medicacoes);
        }
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

    @Override
    public List<PesagemAgrupadaDTO> getAgrupadasPorData() {
        return pesagemRepository.findPesagensAgrupadasPorData();
    }

    @Override
    public List<Pesagem> getPesagensPorDataDetalhada(LocalDate data) {

        return pesagemRepository.findByDataOrderByBoiNumero(data);
    }

    @Override
    @Transactional
    public List<Pesagem> editaPesagensEmLotePorData(LocalDate data, LocalDate novaData) {
        if(novaData == null) throw new PesagemInvalidaException("Nova data não pode ser nula");

        List<Pesagem> pesagens = pesagemRepository.findByData(data);
        validaGrupoSemPesagemVinculada(pesagens);
        pesagens.stream().forEach(pesagem -> pesagem.setData(novaData));
        return pesagemRepository.saveAll(pesagens);
    }

    @Override
    @Transactional
    public List<Pesagem> deletaPesagensEmLotePorData(LocalDate data) {
        List<Pesagem> pesagens = pesagemRepository.findByData(data);
        validaGrupoSemPesagemVinculada(pesagens);
        pesagemRepository.deleteAll(pesagens);
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
    @Transactional
    public Pesagem deletaPesagem(Long id) {
        Pesagem pesagem = getPesagemById(id);
        validaPesagemNaoVinculada(pesagem);
        pesagemRepository.delete(pesagem);
        return pesagem;
    }

    private void validaPesagemNaoVinculada(Pesagem pesagem) {
        if (pesagem.isVinculadaTransacao()) {
            throw new OperacaoConflitanteException(
                    "Pesagem vinculada a uma transação não pode ser editada ou excluída por esta operação"
            );
        }
    }

    private void validaGrupoSemPesagemVinculada(List<Pesagem> pesagens) {
        if (pesagens.stream().anyMatch(Pesagem::isVinculadaTransacao)) {
            throw new OperacaoConflitanteException(
                    "O grupo contém pesagens vinculadas a transações e não pode ser alterado"
            );
        }
    }


    @Override
    public MatrizPesagemResponseDTO gerarMatrizPeso(MatrizPesagemRequestDTO request) {
        validarRequest(request);

        List<LocalDate> datasOrdenadas = request.getDatasSelecionadas().stream()
                .sorted()
                .collect(Collectors.toList());
        List<Pesagem> todasPesagens = pesagemRepository.findByDataIn(datasOrdenadas);
        Map<Long, List<Pesagem>> pesagensPorBoi = todasPesagens.stream()
                .collect(Collectors.groupingBy(p -> p.getBoi().getId()));

        List<LinhaMatrizDTO> linhas = construirLinhasMatriz(pesagensPorBoi, datasOrdenadas);
        linhas.sort(Comparator.comparing(LinhaMatrizDTO::getBoiNumero));

        int totalDias = (int) ChronoUnit.DAYS.between(
                datasOrdenadas.get(0),
                datasOrdenadas.get(datasOrdenadas.size() - 1)
        );

        return new MatrizPesagemResponseDTO(linhas.size(), totalDias, datasOrdenadas, linhas);
    }

    private void validarRequest(MatrizPesagemRequestDTO request) {
        if (request == null || request.getDatasSelecionadas() == null || request.getDatasSelecionadas().isEmpty()) {
            throw new PesagemInvalidaException("Datas selecionadas não podem ser nulas ou vazias");
        }

        if (request.getDatasSelecionadas().size() < 2) {
            throw new PesagemInvalidaException("Selecione pelo menos 2 datas para gerar a matriz");
        }
    }

    private List<LinhaMatrizDTO> construirLinhasMatriz(
            Map<Long, List<Pesagem>> pesagensPorBoi,
            List<LocalDate> datasOrdenadas) {

        List<LinhaMatrizDTO> linhas = new ArrayList<>();

        for (Map.Entry<Long, List<Pesagem>> entry : pesagensPorBoi.entrySet()) {
            List<Pesagem> pesagensDoBoi = entry.getValue();
            pesagensDoBoi.sort(Comparator.comparing(Pesagem::getData));

            Boi boi = pesagensDoBoi.get(0).getBoi();
            Map<LocalDate, Pesagem> pesagensPorData = pesagensDoBoi.stream()
                    .collect(Collectors.toMap(Pesagem::getData, p -> p, (p1, p2) -> p1));

            List<PesoComGmdDTO> pesos = construirListaPesos(pesagensPorData, datasOrdenadas);
            BigDecimal gmdMedio = PerformanceCalculator.calcularGmdMedio(pesos);
            BigDecimal ganhoTotal = calcularGanhoTotal(pesos);

            LinhaMatrizDTO linha = new LinhaMatrizDTO(
                    boi.getId(),
                    boi.getNumero(),
                    boi.getRaca().getNome(),
                    pesos,
                    gmdMedio,
                    ganhoTotal
            );

            linhas.add(linha);
        }

        return linhas;
    }

    private List<PesoComGmdDTO> construirListaPesos(
            Map<LocalDate, Pesagem> pesagensPorData,
            List<LocalDate> datasOrdenadas) {

        List<PesoComGmdDTO> pesos = new ArrayList<>();
        BigDecimal pesoAnteriorValido = null;
        LocalDate dataAnteriorValida = null;

        for (LocalDate data : datasOrdenadas) {
            Pesagem pesagem = pesagensPorData.get(data);

            if (pesagem != null) {
                BigDecimal pesoAtual = pesagem.getPeso();
                BigDecimal gmd = PerformanceCalculator.calculateGMD(pesoAtual, data, pesoAnteriorValido, dataAnteriorValida);

                pesos.add(new PesoComGmdDTO(pesoAtual, gmd, pesagem.getId()));

                pesoAnteriorValido = pesoAtual;
                dataAnteriorValida = data;
            } else {
                pesos.add(new PesoComGmdDTO(null, null, null));
            }
        }

        return pesos;
    }


    private BigDecimal calcularGanhoTotal(List<PesoComGmdDTO> pesos) {
        BigDecimal primeiroPesoValido = pesos.stream()
                .map(PesoComGmdDTO::getPeso)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        BigDecimal ultimoPesoValido = null;
        for (int i = pesos.size() - 1; i >= 0; i--) {
            BigDecimal peso = pesos.get(i).getPeso();
            if (peso != null) {
                ultimoPesoValido = peso;
                break;
            }
        }

        if (primeiroPesoValido == null || ultimoPesoValido == null) {
            return BigDecimal.ZERO;
        }

        return ultimoPesoValido.subtract(primeiroPesoValido);
    }

}
