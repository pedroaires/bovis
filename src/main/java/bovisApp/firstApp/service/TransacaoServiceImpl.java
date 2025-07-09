package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.transacao.*;
import bovisApp.firstApp.exception.transacao.TransacaoInvalidaException;
import bovisApp.firstApp.exception.transacao.TransacaoNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.TransacaoBoi;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import bovisApp.firstApp.repository.TransacaoBoiRepository;
import bovisApp.firstApp.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransacaoServiceImpl implements TransacaoService{

    private final TransacaoRepository transacaoRepository;

    private final TransacaoBoiRepository transacaoBoiRepository;
    private final BoiService boiService;

    @Autowired
    public TransacaoServiceImpl(
            TransacaoRepository transacaoRepository,
            TransacaoBoiRepository transacaoBoiRepository,
            BoiService boiService
    ){
        this.transacaoRepository = transacaoRepository;
        this.transacaoBoiRepository = transacaoBoiRepository;
        this.boiService = boiService;
    }
    @Override
    public List<Transacao> getTransacoes() {
        List<Transacao> transacoes = transacaoRepository.findAll();
        return transacoes;
    }

    @Override
    public Transacao getLoteById(Long id) {
        Transacao transacao = transacaoRepository.findById(id).orElseThrow(
                () -> new TransacaoNaoEncontradaException(id)
        );
        return transacao;
    }

    @Override
    @Transactional
    public Transacao cadastraCompra(CompraRequestDTO compraRequestDTO) {
        validaTransacaoDTO(compraRequestDTO);
        List<BoiRequestDTO> boisRequestDTOS = compraRequestDTO.getCompraBoiDTOS()
                .stream().map(CompraBoiDTO::getBoiRequestDTO)
                .collect(Collectors.toList());
        List<Boi> boisComprados = boiService.cadastraBoisComprados(boisRequestDTOS);
        List<TransacaoBoi> transacaoBois = criaTransacoesBoisDeCompra(boisComprados, compraRequestDTO.getCompraBoiDTOS());
       return criarTransacao(compraRequestDTO, transacaoBois, TransacaoOperacao.COMPRA);
    }

    private List<TransacaoBoi> criaTransacoesBoisDeCompra(List<Boi> boisComprados, List<CompraBoiDTO> compraBoiDTOS){
        //TODO:
        // 1. Adicionar verificações como tamanho de boisComprados = compraBoiDTOS
        // 2. Adicionar validacoes de transacaoBoi (Pesos positivos maiores que 0, valores positivos maiores que 0)
        List<TransacaoBoi> transacaoBois = new ArrayList<>();
        for (int i = 0 ; i < compraBoiDTOS.size() ; i ++){
            CompraBoiDTO dto = compraBoiDTOS.get(i);
            Boi boi = boisComprados.get(i);
            TransacaoBoi transacaoBoi = new TransacaoBoi();

            transacaoBoi.setBoi(boi);
            transacaoBoi.setPeso(dto.getPesoNaTransacao());
            transacaoBoi.setValor(dto.getValor());
            //TODO: Atualizar o boi com nova transacao aqui
            // apenas adicionar a nova transacao ao boi

            transacaoBois.add(transacaoBoi);
        }
        return transacaoBois;
    }

    private List<TransacaoBoi> criaTransacoesBoisDeVenda(List<Boi> boisVendidos, List<VendaBoiDTO> vendaBoiDTOS){
        //TODO: Refatorar metodo duplicado ao criaTransacoesBoisDeCompra
        List<TransacaoBoi> transacaoBois = new ArrayList<>();
        for (int i = 0 ; i < vendaBoiDTOS.size() ; i ++){
            VendaBoiDTO dto = vendaBoiDTOS.get(i);
            Boi boi = boisVendidos.get(i);
            TransacaoBoi transacaoBoi = new TransacaoBoi();

            transacaoBoi.setBoi(boi);
            transacaoBoi.setPeso(dto.getPesoNaTransacao());
            transacaoBoi.setValor(dto.getValor());
            //TODO: Atualizar o boi com nova transacao aqui
            // apenas adicionar a nova transacao ao boi

            transacaoBois.add(transacaoBoi);
        }
        return transacaoBois;
    }

    @Override
    public Transacao cadastraVenda(VendaRequestDTO vendaRequestDTO) {
        this.validaTransacaoDTO(vendaRequestDTO);
        List<Boi> boisVendidos = boiService.cadastraBoisVendidos(
                vendaRequestDTO.getVendaBoiDTOS()
                        .stream()
                        .map(VendaBoiDTO::getBoiId)
                        .collect(Collectors.toList()));

        List<TransacaoBoi> transacaoBois = criaTransacoesBoisDeVenda(
                boisVendidos,
                vendaRequestDTO.getVendaBoiDTOS()
        );
        return criarTransacao(vendaRequestDTO, transacaoBois, TransacaoOperacao.VENDA);
    }

    @Override
    public Transacao deletaTransacao(Long id) {
        Transacao transacao = transacaoRepository.findById(id).orElseThrow(
                () -> new TransacaoNaoEncontradaException(id)
        );
        transacaoRepository.delete(transacao);
        return transacao;
    }


    private Transacao criarTransacao(
            TransacaoRequestDTO dto,
            List<TransacaoBoi> transacaoBois,
            TransacaoOperacao operacao
    ) {
        validaTransacaoDTO(dto);
        Transacao transacao = new Transacao();
        transacao.setValor(dto.getValor());
        transacao.setData(dto.getData());
        transacao.setDescricao(dto.getDescricao());
        transacao.setModeloTransacao(ModeloTransacao.valueOf(dto.getModeloTransacao()));
        transacao.setContraParte(dto.getContraParte());
        transacao.setTransacaoBois(transacaoBois);
        transacao.setOperacao(operacao);
        return transacaoRepository.save(transacao);
    }

    private void validaTransacaoDTO(TransacaoRequestDTO transacaoRequestDTO){
        if (transacaoRequestDTO == null){
            throw new TransacaoInvalidaException("A transação não pode ser nula");
        }
        if (transacaoRequestDTO.getData() == null){
            throw new TransacaoInvalidaException("Data da transação não pode ser nula");
        }
        if (transacaoRequestDTO.getContraParte() == null){
            throw new TransacaoInvalidaException("Contraparte não pode ser nula");
        }
        String modeloTransacaoStr = transacaoRequestDTO.getModeloTransacao();
        if(modeloTransacaoStr == null || modeloTransacaoStr.isEmpty()){
            throw new TransacaoInvalidaException("ModeloTransacao não pode ser nulo ou vazio");
        }
        try{
            ModeloTransacao.valueOf(modeloTransacaoStr);
        } catch(IllegalArgumentException e){
            throw new TransacaoInvalidaException("Valor inválido para ModeloTransacao");
        }
    }

}
