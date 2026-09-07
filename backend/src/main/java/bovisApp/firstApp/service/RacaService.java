package bovisApp.firstApp.service;

import bovisApp.firstApp.model.Raca;

public interface RacaService {
    String normalizaRaca(String raca);

    Raca getRacaByNome(String racaStr);
}
