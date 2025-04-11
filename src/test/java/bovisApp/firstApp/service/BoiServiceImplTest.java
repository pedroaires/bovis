package bovisApp.firstApp.service;

import bovisApp.firstApp.DTO.boi.BoiRequestDTO;
import bovisApp.firstApp.exception.boi.BoiInvalidoException;
import bovisApp.firstApp.exception.boi.BoiNaoEncontradoException;
import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Raca;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.repository.BoiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BoiServiceImplTest {

    private BoiService boiService_underTest;

    @Mock
    private BoiRepository boiRepository;

    @Mock
    private RacaService racaService;

    private static final String[] RACAS = {"Angus", "Brangus", "Nelore", "Guzera", "Tabapua"};
    private static final String[] ESTADOS = {"na_fazenda", "vendido", "morto"};
    private String geraRacaStrAleatoria(){
        return RACAS[(int) (Math.random() * RACAS.length)];
    }
    private Integer geraNumeroAleatorio(){
        return (int)(Math.random()*1000);
    }

    private String gerarEstadoStrAleatorio(){
        return ESTADOS[(int) (Math.random() * ESTADOS.length)];
    }

    private Long geraIdAleatorio() {
        return (long) (Math.random() * 1000);
    }

    private BoiRequestDTO criaBoiRequestDTO(Integer numero, String raca,String estadoBoi){
        BoiRequestDTO boiRequestDTO = new BoiRequestDTO();
        boiRequestDTO.setNumero(numero);
        boiRequestDTO.setRaca(raca);
        boiRequestDTO.setEstadoBoi(estadoBoi);
        return boiRequestDTO;
    }
    @BeforeEach
    void setUp() {
        boiService_underTest = new BoiServiceImpl(boiRepository, racaService);
    }

    @Test
    void deveRetornarBois() {
        // given
        List<Boi> bois = new ArrayList<>();
        bois.add(new Boi(1, new Raca("Angus"), EstadoBoi.NA_FAZENDA));
        bois.add(new Boi(2, new Raca("Brangus"), EstadoBoi.VENDIDO));
        bois.add(new Boi(3, new Raca("Nelore"),  EstadoBoi.MORTO));
        // when
        when(boiRepository.findAll()).thenReturn(bois);
        List<Boi> response = boiService_underTest.getBois();

        // then
        verify(boiRepository).findAll();
        assertThat(response).isEqualTo(bois);
    }
    @Test
    void deveRetornarBoi() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();
        Long boiId = geraIdAleatorio();

        EstadoBoi estadoBoi = EstadoBoi.getEstadoBoi(estadoBoiStr);

        Raca raca = new Raca(racaStr);
        Boi boiEsperado = new Boi(numero, raca, estadoBoi);

        when(boiRepository.findById(boiId)).thenReturn(Optional.of(boiEsperado));
        Boi response = boiService_underTest.getBoiById(boiId);
        verify(boiRepository).findById(boiId);
        assertThat(response).isEqualTo(boiEsperado);
    }

    @Test
    void naoDeveRetornarBoiInexistente() {
        Long boiId = geraIdAleatorio();
        when(boiRepository.findById(boiId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> boiService_underTest.getBoiById(boiId))
                .isInstanceOf(BoiNaoEncontradoException.class);
    }

    @Test
    void deveCadastrarBoiDadosCompletos() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);
        EstadoBoi estadoBoi = EstadoBoi.getEstadoBoi(estadoBoiStr);

        Raca raca = new Raca(racaStr);
        when(racaService.getRacaByNome(racaStr)).thenReturn(raca);
        when(boiRepository.save(any(Boi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boi response = boiService_underTest.cadastraBoi(boiRequestDTO);

        Boi boiEsperado = new Boi(numero, raca, estadoBoi);

        verify(racaService).getRacaByNome(racaStr);

        ArgumentCaptor<Boi> boiArgumentCaptor = ArgumentCaptor.forClass(Boi.class);
        verify(boiRepository).save(boiArgumentCaptor.capture());
        assertThat(boiArgumentCaptor.getValue()).isEqualTo(boiEsperado);
        assertThat(response).isEqualTo(boiEsperado);
    }


    @Test
    void naoDeveCadastrarBoiNumeroNulo() {
        Integer numero = null;
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);

        assertThatThrownBy(() -> boiService_underTest.cadastraBoi(boiRequestDTO))
                .isInstanceOf(BoiInvalidoException.class);

    }

    @Test
    void naoDeveCadastrarBoiEstadoInvalido() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = "estado invalido";

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);

        assertThatThrownBy(() -> boiService_underTest.cadastraBoi(boiRequestDTO))
                .isInstanceOf(BoiInvalidoException.class);

    }

    @Test
    void deveCadastrarBoiRacaNula() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = null;
        String estadoBoiStr = gerarEstadoStrAleatorio();

        BoiRequestDTO boiRequestDTO = criaBoiRequestDTO(numero, racaStr, estadoBoiStr);
        EstadoBoi estadoBoi = EstadoBoi.getEstadoBoi(estadoBoiStr);

        when(racaService.getRacaByNome(null)).thenReturn(null);
        when(boiRepository.save(any(Boi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boi response = boiService_underTest.cadastraBoi(boiRequestDTO);

        ArgumentCaptor<Boi> boiCaptor = ArgumentCaptor.forClass(Boi.class);
        verify(boiRepository).save(boiCaptor.capture());

        Boi boiSalvo = boiCaptor.getValue();
        assertThat(boiSalvo.getNumero()).isEqualTo(numero);
        assertThat(boiSalvo.getRaca()).isNull();
        assertThat(boiSalvo.getEstadoBoi()).isEqualTo(estadoBoi);

        assertThat(response).isEqualTo(boiSalvo);
    }




    @Test
    void deveEditarBoiDadosCompletos() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();
        Long boiId = geraIdAleatorio();

        BoiRequestDTO boiRequestDTO = criaBoiRequestDTO(numero, racaStr, estadoBoiStr);
        EstadoBoi estadoBoi = EstadoBoi.getEstadoBoi(estadoBoiStr);
        Raca raca = new Raca(racaStr);

        Boi boiExistente = new Boi(111, new Raca("outra_raca"), EstadoBoi.NA_FAZENDA);

        when(boiRepository.findById(boiId)).thenReturn(Optional.of(boiExistente));
        when(racaService.getRacaByNome(racaStr)).thenReturn(raca);
        when(boiRepository.save(any(Boi.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Boi response = boiService_underTest.editaBoi(boiRequestDTO, boiId);

        ArgumentCaptor<Boi> boiCaptor = ArgumentCaptor.forClass(Boi.class);
        verify(boiRepository).save(boiCaptor.capture());

        Boi boiSalvo = boiCaptor.getValue();
        assertThat(boiSalvo.getNumero()).isEqualTo(numero);
        assertThat(boiSalvo.getRaca()).isEqualTo(raca);
        assertThat(boiSalvo.getEstadoBoi()).isEqualTo(estadoBoi);
        assertThat(response).isEqualTo(boiSalvo);
    }

    @Test
    void naoDeveEditarBoiInexistente(){
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();
        Long boiId = geraIdAleatorio();

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);

        when(boiRepository.findById(boiId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boiService_underTest.editaBoi(boiRequestDTO, boiId))
                .isInstanceOf(BoiNaoEncontradoException.class);
    }

    @Test
    void naoDeveEditarBoiNumeroNulo() {
        Integer numero = null;
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();
        Long boiId = geraIdAleatorio();

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);

        when(boiRepository.findById(boiId)).thenReturn(Optional.of(new Boi()));

        assertThatThrownBy(() -> boiService_underTest.editaBoi(boiRequestDTO, boiId))
                .isInstanceOf(BoiInvalidoException.class)
                .hasMessage("Número do boi não pode ser nulo");

    }

    @Test
    void naoDeveEditarBoiEstadoInvalido() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = "estado invalido";
        Long boiId = geraIdAleatorio();

        BoiRequestDTO boiRequestDTO =
                criaBoiRequestDTO(numero, racaStr, estadoBoiStr);

        when(boiRepository.findById(boiId)).thenReturn(Optional.of(new Boi()));


        assertThatThrownBy(() -> boiService_underTest.editaBoi(boiRequestDTO, boiId))
                .isInstanceOf(BoiInvalidoException.class);

    }

    @Test
    void deveDeletarBoi() {
        Integer numero = geraNumeroAleatorio();
        String racaStr = geraRacaStrAleatoria();
        String estadoBoiStr = gerarEstadoStrAleatorio();
        Long boiId = geraIdAleatorio();

        EstadoBoi estadoBoi = EstadoBoi.getEstadoBoi(estadoBoiStr);

        Raca raca = new Raca(racaStr);
        Boi boiEsperado = new Boi(numero, raca, estadoBoi);

        when(boiRepository.findById(boiId)).thenReturn(Optional.of(boiEsperado));

        Boi response = boiService_underTest.deleteBoi(boiId);

        verify(boiRepository).findById(boiId);
        verify(boiRepository).delete(boiEsperado);
        assertThat(response).isEqualTo(boiEsperado);
    }

    @Test
    void naoDeveDeletarBoiInexistente(){
        Long boiId = geraIdAleatorio();
        when(boiRepository.findById(boiId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> boiService_underTest.deleteBoi(boiId))
                .isInstanceOf(BoiNaoEncontradoException.class);
    }
}