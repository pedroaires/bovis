package bovisApp.firstApp.repository;

import bovisApp.firstApp.model.Boi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoiRepository extends JpaRepository<Boi, Long> {
}
