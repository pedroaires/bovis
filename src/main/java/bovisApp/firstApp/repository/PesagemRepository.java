package bovisApp.firstApp.repository;

import bovisApp.firstApp.DTO.pesagem.PesagemAgrupadaDTO;
import bovisApp.firstApp.model.Pesagem;
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

    List<Pesagem> findByDataOrderByDataDesc(LocalDate data);

    @Query("SELECT NEW bovisApp.firstApp.DTO.pesagem.PesagemAgrupadaDTO(" +
            "   p.data, " +
            "   COUNT(p.id), " +
            "   AVG(p.peso), " +
            "   SUM(p.peso) " +
            ") " +
            "FROM Pesagem p " +
            "GROUP BY p.data " +
            "ORDER BY p.data DESC")
    List<PesagemAgrupadaDTO> findPesagensAgrupadasPorData();
}
