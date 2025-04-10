package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.TransacaoRequestDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.exception.transacao.TransacaoInvalidaException;
import bovisApp.firstApp.exception.transacao.TransacaoNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import bovisApp.firstApp.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoServiceImpl implements TransacaoService{

    private final TransacaoRepository transacaoRepository;
    private final BoiService boiService;

    @Autowired
    public TransacaoServiceImpl(TransacaoRepository transacaoRepository, BoiService boiService){
        this.transacaoRepository = transacaoRepository;
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
    public Transacao cadastraCompra(CompraRequestDTO compraRequestDTO) {
        validaTransacaoDTO(compraRequestDTO);
        List<Boi> boisComprados = boiService.cadastraBoisComprados(compraRequestDTO.getBois());

       return criarTransacao(compraRequestDTO, boisComprados, TransacaoOperacao.COMPRA);
    }

    @Override
    public Transacao cadastraVenda(VendaRequestDTO vendaRequestDTO) {
        this.validaTransacaoDTO(vendaRequestDTO);
        List<Boi> boisVendidos = boiService.cadastraBoisVendidos(vendaRequestDTO.getBoisIds());

        return criarTransacao(vendaRequestDTO, boisVendidos, TransacaoOperacao.VENDA);
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
            List<Boi> bois,
            TransacaoOperacao operacao
    ) {
        validaTransacaoDTO(dto);
        Transacao transacao = new Transacao();
        transacao.setValor(dto.getValor());
        transacao.setData(dto.getData());
        transacao.setDescricao(dto.getDescricao());
        transacao.setModeloTransacao(ModeloTransacao.valueOf(dto.getModeloTransacao()));
        transacao.setContraParte(dto.getContraParte());
        transacao.setBois(bois);
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
