package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
    List<Pesagem> findByBoiId(Long boiId);

    Optional<Pesagem> findTopByBoiIdOrderByDataDesc(Long boiId);

    List<Pesagem> findTop2ByBoiIdOrderByDataDesc(Long boiId);
}
