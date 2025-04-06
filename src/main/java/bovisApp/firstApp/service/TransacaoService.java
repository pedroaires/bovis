package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.model.Transacao;
import java.util.List;

public interface TransacaoService {
    List<Transacao> getTransacoes();

    Transacao getLoteById(Long id);

    Transacao cadastraCompra(CompraRequestDTO compraRequestDTO);

    Transacao cadastraVenda(VendaRequestDTO vendaRequestDTO);

    Transacao deletaTransacao(Long id);
}
