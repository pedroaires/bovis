package bovisApp.firstApp.service;

import bovisApp.firstApp.model.Lote;
import bovisApp.firstApp.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class LoteServiceImpl implements  LoteService{
    private final LoteRepository loteRepository;

    @Autowired
    public LoteServiceImpl(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @Override
    public Lote getLoteById(Long id){
        if(id == null){
            Lote lote = new Lote();
            lote.setDescricao("Lote criado automaticamente para boi sem lote");
            loteRepository.save(lote);
            return lote;
        }
        Optional<Lote> loteOp = loteRepository.findById(id);
        return loteOp.orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
    }

}
