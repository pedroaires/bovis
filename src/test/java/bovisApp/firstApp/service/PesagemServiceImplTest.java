package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.pesagem.PesagemRequestDTO;
import bovisApp.firstApp.exception.pesagem.PesagemInvalidaException;
import bovisApp.firstApp.exception.pesagem.PesagemNaoEncontradaException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.model.Pesagem;
import bovisApp.firstApp.repository.PesagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PesagemServiceImplTest {
    private PesagemService pesagemService_underTest;

    @Mock
    private PesagemRepository pesagemRepository;

    @Mock
    private MedicacaoService medicacaoService;

    private Date gerarDataAleatoria() {
        return new Date((long) (Math.random() * 1000000000));
    }

    private Long geraIdAleatorio() {
        return (long) (Math.random() * 1000);
    }

    private Double geraPesoAleatorio() {
        return Math.random() * 100;
    }

    private Pesagem gerarPesagemAleatoria() {
        Pesagem pesagem = new Pesagem(gerarDataAleatoria(), geraPesoAleatorio(), "descricao", new ArrayList<Medicacao>());
        pesagem.setId(geraIdAleatorio());
        return pesagem;
    }

    private Boi gerarBoiAleatorio() {
        Long id = geraIdAleatorio();
        Boi boi = new Boi();
        boi.setId(id);
        return boi;
    }
    @BeforeEach
    void setUp(){
        pesagemService_underTest = new PesagemServiceImpl(pesagemRepository, medicacaoService);
    }

    @Test
    void deveRetornarPesagens() {
        // given
        List<Pesagem> pesagens = new ArrayList<>();
        pesagens.add(new Pesagem(gerarDataAleatoria(), geraPesoAleatorio(), "descricao1", new ArrayList<Medicacao>()));
        pesagens.add(new Pesagem(gerarDataAleatoria(), geraPesoAleatorio(), "descricao2", new ArrayList<Medicacao>()));
        pesagens.add(new Pesagem(gerarDataAleatoria(), geraPesoAleatorio(), "descricao3", new ArrayList<Medicacao>()));
        pesagens.add(new Pesagem(gerarDataAleatoria(), geraPesoAleatorio(), "descricao4", new ArrayList<Medicacao>()));
        // when
        when(pesagemRepository.findAll()).thenReturn(pesagens);
        List<Pesagem> pesagems = pesagemService_underTest.getPesagens();

        // then
        verify(pesagemRepository).findAll();

        assertThat(pesagems).isEqualTo(pesagens);
    }

    @Test
    void deveRetornarPesagemPorBoiIdValido() {
        // given
        Long boiId = geraIdAleatorio();
        List<Pesagem> pesagensEsperadas = new ArrayList<Pesagem>();
        pesagensEsperadas.add(gerarPesagemAleatoria());
        // when
        when(pesagemRepository.findByBoiId(boiId)).thenReturn(pesagensEsperadas);
        List<Pesagem> pesagensResponse = pesagemService_underTest.getPesagemByBoiId(boiId);

        // then
        verify(pesagemRepository).findByBoiId(boiId);

        assertThat(pesagensResponse).isEqualTo(pesagensEsperadas);
    }

    @Test
    void deveRetornarPesagemPorBoiIdInvalido() {
        // given
        Long boiId = geraIdAleatorio();
        List<Pesagem> pesagensEsperadas = new ArrayList<Pesagem>();

        // when
        when(pesagemRepository.findByBoiId(boiId)).thenReturn(pesagensEsperadas);
        List<Pesagem> pesagensResponse = pesagemService_underTest.getPesagemByBoiId(boiId);

        // then
        verify(pesagemRepository).findByBoiId(boiId);
        assertThat(pesagensResponse).isEqualTo(pesagensEsperadas);
    }

    @Test
    void deveCadastrarPesagemDadosCompletos(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(medicacaoService.getOrCreateMedicacao(medicacaoStr)).thenReturn(medicacao);
        when(pesagemRepository.save(any(Pesagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Pesagem> pesagemArgumentCaptor = ArgumentCaptor.forClass(Pesagem.class);
        Pesagem response = pesagemService_underTest.cadastraPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ), boi
        );


        // then
        verify(pesagemRepository).save(pesagemArgumentCaptor.capture());
        assertThat(pesagemArgumentCaptor.getValue()).isEqualTo(response);
        assertThat(response.getDescricao()).isEqualTo(pesagemEsperada.getDescricao());
        assertThat(response.getData()).isEqualTo(pesagemEsperada.getData());
        assertThat(response.getPeso()).isEqualTo(pesagemEsperada.getPeso());
        assertThat(response.getMedicacoes()).isEqualTo(pesagemEsperada.getMedicacoes());
        assertThat(response.getBoi()).isEqualTo(pesagemEsperada.getBoi());
    }

    @Test
    void naoDeveCadastrarPesagemSemPeso(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when

        // then
        assertThatThrownBy(() -> pesagemService_underTest.cadastraPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        null,
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ), boi
        )).isInstanceOf(PesagemInvalidaException.class);
    }

    @Test
    void naoDeveCadastrarPesagemSemData(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when

        // then
        assertThatThrownBy(() -> pesagemService_underTest.cadastraPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        null,
                        List.of(medicacaoStr)
                ), boi
        )).isInstanceOf(PesagemInvalidaException.class);
    }

    @Test
    void naoDeveCadastrarPesagemSemBoiId(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when

        // then
        assertThatThrownBy(() -> pesagemService_underTest.cadastraPesagem(
                new PesagemRequestDTO(
                        null,
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ), boi
        )).isInstanceOf(PesagemInvalidaException.class);
    }

    @Test
    void deveEditarPesagemDadosCompletos() {
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        when(medicacaoService.getOrCreateMedicacao(medicacaoStr)).thenReturn(medicacao);
        when(pesagemRepository.save(any(Pesagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Pesagem> pesagemArgumentCaptor = ArgumentCaptor.forClass(Pesagem.class);
        Pesagem response = pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ),
                pesagemEsperada.getId(),
                boi
        );

        // then
        verify(pesagemRepository).save(pesagemArgumentCaptor.capture());
        assertThat(pesagemArgumentCaptor.getValue()).isEqualTo(pesagemEsperada);
        assertThat(response).isEqualTo(pesagemEsperada);
    }

    @Test
    void naoDeveEditarPesagemInexistente(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.empty());

        // then
        assertThatThrownBy(() -> pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ),
                pesagemEsperada.getId(),
                boi
        )).isInstanceOf(PesagemNaoEncontradaException.class);
    }

    @Test
    void naoDeveEditarPesagemPesoNulo(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        // then
        assertThatThrownBy(() -> pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        null,
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ),
                pesagemEsperada.getId(),
                boi
        )).isInstanceOf(PesagemInvalidaException.class);
    }

    @Test
    void naoDeveEditarPesagemDataNula(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        // then
        assertThatThrownBy(() -> pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        null,
                        List.of(medicacaoStr)
                ),
                pesagemEsperada.getId(),
                boi
        )).isInstanceOf(PesagemInvalidaException.class);
    }

    @Test
    void deveEditarPesagemDescricaoNula(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        when(pesagemRepository.save(any(Pesagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Pesagem> pesagemArgumentCaptor = ArgumentCaptor.forClass(Pesagem.class);
        Pesagem response = pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        null,
                        pesagemEsperada.getData(),
                        List.of(medicacaoStr)
                ),
                pesagemEsperada.getId(),
                boi
        );

        // then
        verify(pesagemRepository).save(pesagemArgumentCaptor.capture());
        assertThat(pesagemArgumentCaptor.getValue().getDescricao()).isEqualTo("");
        assertThat(response.getDescricao()).isEqualTo("");
    }

    @Test
    void deveEditarPesagemMedicacoesVazias(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        String medicacaoStr = "medicacao";
        Boi boi = gerarBoiAleatorio();
        pesagemEsperada.setBoi(boi);
        Medicacao medicacao = new Medicacao(medicacaoStr);
        List<Medicacao> medicacaoList = new ArrayList<Medicacao>();
        medicacaoList.add(medicacao);
        pesagemEsperada.setMedicacoes(medicacaoList);

        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        when(pesagemRepository.save(any(Pesagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Pesagem> pesagemArgumentCaptor = ArgumentCaptor.forClass(Pesagem.class);
        Pesagem response = pesagemService_underTest.editaPesagem(
                new PesagemRequestDTO(
                        boi.getId(),
                        pesagemEsperada.getPeso(),
                        pesagemEsperada.getDescricao(),
                        pesagemEsperada.getData(),
                        new ArrayList<String>()
                ),
                pesagemEsperada.getId(),
                boi
        );

        // then
        verify(pesagemRepository).save(pesagemArgumentCaptor.capture());
        assertThat(pesagemArgumentCaptor.getValue().getMedicacoes()).isEqualTo(new ArrayList<Medicacao>());
        assertThat(response.getMedicacoes()).isEqualTo(new ArrayList<Medicacao>());
    }

    @Test
    void deveDeletarPesagemIdValido(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.of(pesagemEsperada));
        ArgumentCaptor<Pesagem> pesagemArgumentCaptor = ArgumentCaptor.forClass(Pesagem.class);
        Pesagem response = pesagemService_underTest.deletaPesagem(pesagemEsperada.getId());

        // then
        verify(pesagemRepository).delete(pesagemEsperada);
        assertThat(response).isEqualTo(pesagemEsperada);
    }

    @Test
    void naoDeveDeletarPesagemInexistente(){
        // given
        Pesagem pesagemEsperada = gerarPesagemAleatoria();
        // when
        when(pesagemRepository.findById(pesagemEsperada.getId())).thenReturn(java.util.Optional.empty());

        // then
        assertThatThrownBy(() -> pesagemService_underTest.deletaPesagem(pesagemEsperada.getId())).isInstanceOf(PesagemNaoEncontradaException.class);
    }



}
