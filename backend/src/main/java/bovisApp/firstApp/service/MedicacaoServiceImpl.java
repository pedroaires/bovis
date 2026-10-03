package bovisApp.firstApp.service;


import bovisApp.firstApp.model.Medicacao;
import bovisApp.firstApp.repository.MedicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicacaoServiceImpl implements MedicacaoService {

    private final MedicacaoRepository medicacaoRepository;

    @Autowired
    private MedicacaoServiceImpl(MedicacaoRepository medicacaoRepository) {
        this.medicacaoRepository = medicacaoRepository;
    }

    private String normalizaMedicacao(String medicacaoStr){
        return medicacaoStr.toLowerCase();
    }

    @Override
    public Medicacao getOrCreateMedicacao(String medicacaoStr) {
        if(medicacaoStr == null){
            throw new IllegalArgumentException("Nome da medicação não pode ser nulo");
        }
        medicacaoStr = normalizaMedicacao(medicacaoStr);
        Optional<Medicacao> medicacaoOp = medicacaoRepository.findByNome(medicacaoStr);
        if(medicacaoOp.isPresent()){
            return medicacaoOp.get();
        }
        Medicacao medicacao = new Medicacao(medicacaoStr);
        medicacaoRepository.save(medicacao);
        return medicacao;
    }
}
