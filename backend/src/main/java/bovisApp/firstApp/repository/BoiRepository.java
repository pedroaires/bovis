package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoiRepository extends JpaRepository<Boi, Long>, JpaSpecificationExecutor<Boi> {
    @EntityGraph(attributePaths = {"pesagens"})
    Page<Boi> findAll(Specification<Boi> spec, Pageable pageable);

    List<Boi> findAllByEstadoBoi(EstadoBoi estadoBoi);
}
