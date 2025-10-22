package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.transacao.*;
import bovisApp.firstApp.exception.transacao.TransacaoInvalidaException;
import bovisApp.firstApp.exception.transacao.TransacaoNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;
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
@Service
public class TransacaoServiceImpl implements TransacaoService{

    private final TransacaoRepository transacaoRepository;

    private final TransacaoBoiRepository transacaoBoiRepository;

    private final BoiService boiService;

    private final PesagemService pesagemService;

    @Autowired
    public TransacaoServiceImpl(
            TransacaoRepository transacaoRepository,
            TransacaoBoiRepository transacaoBoiRepository,
            BoiService boiService,
            PesagemService pesagemService
    ){
        this.transacaoRepository = transacaoRepository;
        this.transacaoBoiRepository = transacaoBoiRepository;
        this.boiService = boiService;
        this.pesagemService = pesagemService;
    }
    @Override
    public List<Transacao> getTransacoes() {
        List<Transacao> transacoes = transacaoRepository.findAll();
        return transacoes;
    }

    @Override
    public Transacao getTransacaoById(Long id) {
        Transacao transacao = transacaoRepository.findById(id).orElseThrow(
                () -> new TransacaoNaoEncontradaException(id)
        );
        return transacao;
    }

    @Override
    @Transactional
    public Transacao registraCompra(CompraRequestDTO compraRequestDTO){
        validaTransacaoDTO(compraRequestDTO);
        List<TransacaoBoi> transacaoBois = new ArrayList<>();
        for (ItemCompraRequestDTO item : compraRequestDTO.getCompraBoiDTOS()){
            Boi boi = boiService.cadastraBoi(item.getBoiRequestDTO());
            Pesagem pesagem = pesagemService.criarPesagemParaTransacao(boi, item.getPesagemDTO(), compraRequestDTO.getData());
            TransacaoBoi transacaoBoi = new TransacaoBoi();
            transacaoBoi.setPesagem(pesagem);
            transacaoBoi.setValor(item.getValor());

            boi.addTransacaoBoi(transacaoBoi);
            transacaoBois.add(transacaoBoi);
        }

        return criarTransacao(compraRequestDTO, transacaoBois, TransacaoOperacao.COMPRA);
    }

    @Override
    @Transactional
    public Transacao registraVenda(VendaRequestDTO vendaRequestDTO){
        validaTransacaoDTO(vendaRequestDTO);
        List<TransacaoBoi> transacaoBois = new ArrayList<>();
        for (ItemVendaRequestDTO item : vendaRequestDTO.getVendaBoiDTOS()){
            Boi boi = boiService.atualizaBoiVendido(item.getBoiId());
            Pesagem pesagem = pesagemService.criarPesagemParaTransacao(boi, item.getPesagemDTO(), vendaRequestDTO.getData());
            TransacaoBoi transacaoBoi = new TransacaoBoi();
            transacaoBoi.setPesagem(pesagem);
            transacaoBoi.setValor(item.getValor());

            boi.addTransacaoBoi(transacaoBoi);
            transacaoBois.add(transacaoBoi);
        }

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

    @Override
    public Transacao getTransacaoDetalhadaById(Long id) {
        return transacaoRepository.findDetalhesById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));
    }

    @Override
    public Transacao editaTransacao(Long id, EditaTransacaoRequestDTO dto) {
        if(dto == null){
            throw new TransacaoInvalidaException("Dados de edição não podem ser nulos");
        }

        Transacao transacao = this.getTransacaoById(id);

        if(dto.getValor() != null) transacao.setValor(dto.getValor());
        if(dto.getData() != null) transacao.setData(dto.getData());
        if(dto.getDescricao() != null) transacao.setDescricao(dto.getDescricao());
        if(dto.getContraparte() != null) transacao.setContraParte(dto.getContraparte());
        if(dto.getModeloTransacao() != null){
            validaModeloTransacao(dto.getModeloTransacao());
            transacao.setModeloTransacao(ModeloTransacao.valueOf(dto.getModeloTransacao().toUpperCase()));
        }
        transacaoRepository.save(transacao);
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
        this.validaModeloTransacao(transacaoRequestDTO.getModeloTransacao());

    }
    private void validaModeloTransacao(String modeloTransacaoStr){
        if(modeloTransacaoStr == null || modeloTransacaoStr.isEmpty()){
            throw new TransacaoInvalidaException("ModeloTransacao não pode ser nulo ou vazio");
        }
        try{
            ModeloTransacao.valueOf(modeloTransacaoStr.toUpperCase());
        } catch(IllegalArgumentException e){
            throw new TransacaoInvalidaException("Valor inválido para ModeloTransacao");
        }
    }

}
