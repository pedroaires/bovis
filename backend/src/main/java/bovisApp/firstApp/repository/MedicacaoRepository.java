package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Medicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicacaoRepository extends JpaRepository<Medicacao, Long> {
    Optional<Medicacao> findByNome(String medicacaoStr);
}
