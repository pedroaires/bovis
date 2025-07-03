package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.TransacaoBoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoBoiRepository extends JpaRepository<TransacaoBoi, Long> {

}
