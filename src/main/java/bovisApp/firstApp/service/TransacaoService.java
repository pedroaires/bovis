package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.model.Transacao;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransacaoService {
    List<Transacao> getTransacoes();

    Transacao getTransacaoById(Long id);

    @Transactional
    Transacao registraCompra(CompraRequestDTO compraRequestDTO);

    @Transactional
    Transacao registraVenda(VendaRequestDTO vendaRequestDTO);

    Transacao deletaTransacao(Long id);

    Transacao getTransacaoDetalhadaById(Long id);
}
