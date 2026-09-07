package bovisApp.firstApp.service;

import bovisApp.firstApp.model.Raca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import bovisApp.firstApp.repository.RacaRepository;

import java.util.Optional;

@Service
public class RacaServiceImpl implements RacaService{

    private final RacaRepository racaRepository;

    @Autowired
    public RacaServiceImpl(RacaRepository racaRepository) {
        this.racaRepository = racaRepository;
    }

    @Override
    public String normalizaRaca(String racaStr){
        if(racaStr == null || racaStr.isEmpty()) {
            throw new IllegalArgumentException("Raca do boi nao pode ser vazia");
        }
        String strNorm = racaStr.toLowerCase().trim();
        return strNorm;
    }

    @Override
    public Raca getRacaByNome(String racaStr){
        String racaStrNorm = normalizaRaca(racaStr);
        Optional<Raca> racaOp = racaRepository.findByNome(racaStrNorm);
        Raca raca;
        if(racaOp.isPresent()){
            raca = racaOp.get();
        }
        else{
            raca = new Raca(racaStrNorm);
            racaRepository.save(raca);
        }
        return raca;
    }
}
