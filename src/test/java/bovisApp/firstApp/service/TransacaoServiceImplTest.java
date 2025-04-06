package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.transacao.CompraRequestDTO;
import bovisApp.firstApp.DTO.transacao.VendaRequestDTO;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Transacao;
import bovisApp.firstApp.model.enumeration.ModeloTransacao;
import bovisApp.firstApp.model.enumeration.TransacaoOperacao;
import bovisApp.firstApp.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceImplTest {

    private TransacaoService transacaoService_underTest;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private BoiService boiService;

    @BeforeEach
    void setUp() {
        transacaoService_underTest = new TransacaoServiceImpl(transacaoRepository, boiService);
    }

    private Boi gerarBoiAleatorio() {
        Boi boi = new Boi();
        boi.setId((long) (Math.random() * 1000));
        return boi;
    }

    private CompraRequestDTO gerarCompraRequest() {
        CompraRequestDTO dto = new CompraRequestDTO();
        dto.setValor(BigDecimal.TEN);
        dto.setData(LocalDate.now());
        dto.setDescricao("Compra de bois");
        dto.setModeloTransacao(ModeloTransacao.UNIDADE.name());
        dto.setContraParte("Fazenda Exemplo");
        dto.setBois(Collections.emptyList()); // pode adicionar mocks se necessário
        return dto;
    }

    private VendaRequestDTO gerarVendaRequest(List<Long> boiIds) {
        VendaRequestDTO dto = new VendaRequestDTO();
        dto.setValor(BigDecimal.valueOf(1500));
        dto.setData(LocalDate.now());
        dto.setDescricao("Venda de bois");
        dto.setModeloTransacao(ModeloTransacao.PESO.name());
        dto.setContraParte("Frigorífico XYZ");
        dto.setBoisIds(boiIds);
        return dto;
    }

    @Test
    void deveCadastrarCompraComDadosValidos() {
        // given
        CompraRequestDTO dto = gerarCompraRequest();
        List<Boi> bois = List.of(gerarBoiAleatorio());
        when(boiService.cadastraBoisComprados(dto.getBois())).thenReturn(bois);

        // when
        transacaoService_underTest.cadastraCompra(dto);

        // then
        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(captor.capture());

        Transacao saved = captor.getValue();
        assertThat(saved.getValor()).isEqualTo(dto.getValor());
        assertThat(saved.getOperacao()).isEqualTo(TransacaoOperacao.COMPRA);
        assertThat(saved.getContraParte()).isEqualTo(dto.getContraParte());
    }

    @Test
    void deveCadastrarVendaComDadosValidos() {
        // given
        List<Long> boiIds = List.of(1L, 2L);
        VendaRequestDTO dto = gerarVendaRequest(boiIds);
        List<Boi> bois = List.of(gerarBoiAleatorio(), gerarBoiAleatorio());
        when(boiService.cadastraBoisVendidos(boiIds)).thenReturn(bois);

        // when
        transacaoService_underTest.cadastraVenda(dto);

        // then
        ArgumentCaptor<Transacao> captor = ArgumentCaptor.forClass(Transacao.class);
        verify(transacaoRepository).save(captor.capture());

        Transacao saved = captor.getValue();
        assertThat(saved.getOperacao()).isEqualTo(TransacaoOperacao.VENDA);
        assertThat(saved.getValor()).isEqualTo(dto.getValor());
    }

    @Test
    void naoDeveCadastrarCompraComDTOInvalido() {
        assertThatThrownBy(() -> transacaoService_underTest.cadastraCompra(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void naoDeveCadastrarVendaComDTOInvalido() {
        assertThatThrownBy(() -> transacaoService_underTest.cadastraVenda(null))
                .isInstanceOf(IllegalArgumentException.class);
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

        Transacao result = transacaoService_underTest.getLoteById(1L);

        assertThat(result).isEqualTo(mock);
        verify(transacaoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoSeTransacaoNaoEncontradaPorId() {
        when(transacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService_underTest.getLoteById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
