package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.DTO.transacao.ItemCompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.ItemVendaRequestDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.exception.transacao.TransacaoInvalidaException;
import bovisApp.firstApp.exception.transacao.TransacaoNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import bovisApp.firstApp.repository.TransacaoBoiRepository;
import bovisApp.firstApp.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceImplTest {

    private TransacaoService transacaoService_underTest;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private TransacaoBoiRepository transacaoBoiRepository;


    @Mock
    private PesagemService pesagemService;

    @Mock
    private BoiService boiService;

    @BeforeEach
    void setUp() {
        transacaoService_underTest = new TransacaoServiceImpl(transacaoRepository, transacaoBoiRepository, boiService, pesagemService);
    }

    private Boi gerarBoiAleatorio() {
        Boi boi = new Boi();
        boi.setId((long) (Math.random() * 1000));
        boi.setTransacaoBois(new ArrayList<>());
        return boi;
    }
    private ItemCompraRequestDTO gerarCompraBoiDTO(){

        ItemCompraRequestDTO itemCompraRequestDTO = new ItemCompraRequestDTO();
        BoiRequestDTO boiRequestDTO = new BoiRequestDTO();
        itemCompraRequestDTO.setBoiRequestDTO(boiRequestDTO);
        itemCompraRequestDTO.setPesoNaTransacao(Double.valueOf(500));
        itemCompraRequestDTO.setValor(BigDecimal.TEN);
        return itemCompraRequestDTO;
    }
    private ItemVendaRequestDTO gerarVendaBoiDTO(Long boiId) {
        ItemVendaRequestDTO itemVendaRequestDTO = new ItemVendaRequestDTO();
        itemVendaRequestDTO.setBoiId(boiId);
        itemVendaRequestDTO.setPesoNaTransacao(Double.valueOf(450));
        itemVendaRequestDTO.setValor(BigDecimal.valueOf(750));
        return itemVendaRequestDTO;
    }

    private CompraRequestDTO gerarCompraRequest() {
        CompraRequestDTO dto = new CompraRequestDTO();
        dto.setValor(BigDecimal.TEN);
        dto.setData(LocalDate.now());
        dto.setDescricao("Compra de bois");
        dto.setModeloTransacao(ModeloTransacao.UNIDADE.name());
        dto.setContraParte("Fazenda Exemplo");
        ItemCompraRequestDTO itemCompraRequestDTO = gerarCompraBoiDTO();
        dto.setCompraBoiDTOS(List.of(itemCompraRequestDTO));
        return dto;
    }

    private VendaRequestDTO gerarVendaRequest(List<Long> boiIds) {
        VendaRequestDTO dto = new VendaRequestDTO();
        dto.setValor(BigDecimal.valueOf(1500));
        dto.setData(LocalDate.now());
        dto.setDescricao("Venda de bois");
        dto.setModeloTransacao(ModeloTransacao.PESO.name());
        dto.setContraParte("Frigorífico XYZ");

        List<ItemVendaRequestDTO> itemVendaRequestDTOS = boiIds.stream()
                .map(this::gerarVendaBoiDTO)
                .collect(Collectors.toList());

        dto.setVendaBoiDTOS(itemVendaRequestDTOS);
        return dto;
    }

    @Test
    void deveCadastrarCompraComDadosValidos() {
        // given
        CompraRequestDTO dto = gerarCompraRequest();
        List<Boi> bois = List.of(gerarBoiAleatorio());
        List<BoiRequestDTO> expectedBoiRequests = dto.getCompraBoiDTOS()
                .stream()
                .map(ItemCompraRequestDTO::getBoiRequestDTO)
                .collect(Collectors.toList());

        ArgumentCaptor<List<BoiRequestDTO>> boiRequestCaptor = ArgumentCaptor.forClass(List.class);
        when(boiService.cadastraBoisComprados(boiRequestCaptor.capture())).thenReturn(bois);

        // when
        transacaoService_underTest.cadastraCompra(dto);

        // then
            // Verifica se a lista correta foi passada para o boiService
        List<BoiRequestDTO> capturedBoiRequests = boiRequestCaptor.getValue();
        assertThat(capturedBoiRequests).isEqualTo(expectedBoiRequests);

            // Verifica se a transação foi salva corretamente
        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(captor.capture());

        Transacao saved = captor.getValue();
        assertThat(saved.getValor()).isEqualTo(dto.getValor());
        assertThat(saved.getOperacao()).isEqualTo(TransacaoOperacao.COMPRA);
        assertThat(saved.getContraParte()).isEqualTo(dto.getContraParte());
        assertThat(saved.getTransacaoBois()).isNotNull();
        assertThat(saved.getTransacaoBois().size()).isEqualTo(bois.size());
    }

    @Test
    void deveCadastrarVendaComDadosValidos() {
        // given
        List<Long> boiIds = List.of(1L, 2L);
        VendaRequestDTO dto = gerarVendaRequest(boiIds);
        List<Boi> bois = List.of(gerarBoiAleatorio(), gerarBoiAleatorio());

        List<Long> expectedBoiIds = dto.getVendaBoiDTOS()
                .stream()
                .map(ItemVendaRequestDTO::getBoiId)
                .collect(Collectors.toList());

        ArgumentCaptor<List<Long>> boiIdsCaptor = ArgumentCaptor.forClass(List.class);
        when(boiService.cadastraBoisVendidos(boiIdsCaptor.capture())).thenReturn(bois);

        // when
        transacaoService_underTest.cadastraVenda(dto);

        // then
            // Verifica se a lista correta foi passada para o boiService
        List<Long> capturedBoiIds = boiIdsCaptor.getValue();
        assertThat(capturedBoiIds).isEqualTo(expectedBoiIds);

            // Verifica se a transação foi salva corretamente
        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(captor.capture());

        Transacao saved = captor.getValue();
        assertThat(saved.getOperacao()).isEqualTo(TransacaoOperacao.VENDA);
        assertThat(saved.getValor()).isEqualTo(dto.getValor());
        assertThat(saved.getTransacaoBois()).isNotNull();
        assertThat(saved.getTransacaoBois().size()).isEqualTo(bois.size());
    }

    @Test
    void naoDeveCadastrarCompraComDTOInvalido() {
        assertThatThrownBy(() -> transacaoService_underTest.cadastraCompra(null))
                .isInstanceOf(TransacaoInvalidaException.class);
    }

    @Test
    void naoDeveCadastrarVendaComDTOInvalido() {
        assertThatThrownBy(() -> transacaoService_underTest.cadastraVenda(null))
                .isInstanceOf(TransacaoInvalidaException.class);
    }

    @Test
    void deveRetornarTransacoes() {
        List<Transacao> mockTransacoes = List.of(new Transacao(), new Transacao());
        when(transacaoRepository.findAll()).thenReturn(mockTransacoes);

        List<Transacao> result = transacaoService_underTest.getTransacoes();

        assertThat(result).isEqualTo(mockTransacoes);
        verify(transacaoRepository).findAll();
    }

    @Test
    void deveRetornarTransacaoPorId() {
        Transacao mock = new Transacao();
        mock.setId(1L);
        when(transacaoRepository.findById(1L)).thenReturn(Optional.of(mock));

        Transacao result = transacaoService_underTest.getTransacaoById(1L);

        assertThat(result).isEqualTo(mock);
        verify(transacaoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoSeTransacaoNaoEncontradaPorId() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService_underTest.getTransacaoById(1L))
                .isInstanceOf(TransacaoNaoEncontradaException.class);
    }
}
