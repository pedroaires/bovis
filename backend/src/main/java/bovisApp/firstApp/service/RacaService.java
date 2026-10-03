package bovisApp.firstApp.service;

import bovisApp.firstApp.model.Raca;

import java.util.List;

public interface RacaService {
    String normalizaRaca(String raca);

    Raca getRacaByNome(String racaStr);

    List<Raca> getRacas();
}
