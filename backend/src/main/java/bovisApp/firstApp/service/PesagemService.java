package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.*;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Pesagem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PesagemService {
    Pesagem getPesagemById(Long id);

    List<Pesagem> getPesagens();

    List<Pesagem> getPesagemByBoiId(Long boiId);

    Pesagem cadastraPesagem(PesagemRequestDTO pesagemRequestDTO);

    Pesagem deletaPesagem(Long id);

    Pesagem criarPesagemParaTransacao(Boi boi, PesagemTransacaoRequestDTO dto, LocalDate dataTransacao);

    Pesagem editaPesagem(PesagemRequestDTO pesagemRequestDTO, Long pesagemId);

    Optional<Pesagem> getUltimaPesagemPorBoiId(Long boiId);

    List<Pesagem> getUltimasDuasPesagensPorBoiId(Long boiId);

    List<Pesagem> cadastraLotePesagem(List<PesagemRequestDTO> pesagens);

    List<PesagemAgrupadaDTO> getAgrupadasPorData();

    List<Pesagem> getPesagensPorDataDetalhada(LocalDate data);

    List<Pesagem> editaPesagensEmLotePorData(LocalDate data, LocalDate novaData);

    List<Pesagem> deletaPesagensEmLotePorData(LocalDate data);

    MatrizPesagemResponseDTO gerarMatrizPeso(MatrizPesagemRequestDTO request);
}
