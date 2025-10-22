package bovisApp.firstApp.repository;


import bovisApp.firstApp.model.Transacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @EntityGraph(attributePaths = {
            "transacaoBois",
            "transacaoBois.pesagem",
            "transacaoBois.boi",
            "transacaoRelacionada"
    })
    Optional<Transacao> findDetalhesById(Long id);
}
