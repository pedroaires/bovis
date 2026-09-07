package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Raca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RacaRepository extends JpaRepository<Raca, Long> {
    Optional<Raca> findByNome(String racaStr);
}
