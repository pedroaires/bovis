package bovisApp.firstApp.service;

import bovisApp.firstApp.model.Medicacao;

public interface MedicacaoService {

    Medicacao getOrCreateMedicacao(String medicacaoStr);

}
