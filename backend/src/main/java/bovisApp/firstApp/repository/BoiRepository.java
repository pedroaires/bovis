package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoiRepository extends JpaRepository<Boi, Long> {
    Optional<Boi> findByNumeroAndLoteId(Integer numero, Long loteId);
}
