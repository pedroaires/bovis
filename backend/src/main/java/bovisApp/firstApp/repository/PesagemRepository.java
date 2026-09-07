package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
}
