package cl.esfocar.comunicados.repository;

import cl.esfocar.comunicados.model.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {
    List<Comunicado> findAllByOrderByIdDesc();
}
