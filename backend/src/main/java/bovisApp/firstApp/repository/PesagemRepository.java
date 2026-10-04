package bovisApp.firstApp.repository;

import bovisApp.firstApp.DTO.pesagem.PesagemAgrupadaDTO;
import bovisApp.firstApp.model.Pesagem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
    List<Pesagem> findByBoiId(Long boiId);

    Optional<Pesagem> findTopByBoiIdOrderByDataDesc(Long boiId);

    List<Pesagem> findTop2ByBoiIdOrderByDataDesc(Long boiId);

    @EntityGraph(attributePaths = "transacaoBoi")
    List<Pesagem> findByData(LocalDate data);

    @EntityGraph(attributePaths = "transacaoBoi")
    List<Pesagem> findByDataOrderByBoiNumero(LocalDate data);

    @Query("SELECT NEW bovisApp.firstApp.DTO.pesagem.PesagemAgrupadaDTO(" +
            "   p.data, " +
            "   COUNT(p.id), " +
            "   AVG(p.peso), " +
             "   SUM(p.peso), " +
             "   SUM(CASE WHEN tb.id IS NOT NULL THEN 1 ELSE 0 END) " +
             ") " +
             "FROM Pesagem p " +
             "LEFT JOIN p.transacaoBoi tb " +
            "GROUP BY p.data " +
            "ORDER BY p.data DESC")
    List<PesagemAgrupadaDTO> findPesagensAgrupadasPorData();

    List<Pesagem> findByDataIn(List<LocalDate> dates);
}
