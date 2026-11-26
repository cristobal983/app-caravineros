package cl.esfocar.comunicados.controller;

import cl.esfocar.comunicados.model.Comunicado;
import cl.esfocar.comunicados.repository.ComunicadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/comunicados")
@CrossOrigin(origins = "*")
public class ComunicadoController {

    private final ComunicadoRepository repository;

    public ComunicadoController(ComunicadoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Comunicado> listar() {
        return repository.findAllByOrderByIdDesc();
    }

    @PostMapping
    public ResponseEntity<Comunicado> crear(@RequestBody Comunicado comunicado) {
        if (comunicado.getTitulo() == null || comunicado.getTitulo().isBlank() ||
            comunicado.getDetalle() == null || comunicado.getDetalle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (comunicado.getFecha() == null || comunicado.getFecha().isBlank()) {
            comunicado.setFecha(LocalDate.now().toString());
        }
        if (comunicado.getUnidad() == null || comunicado.getUnidad().isBlank()) {
            comunicado.setUnidad("Dirección ESFOCAR");
        }
        if (comunicado.getPrioridad() == null || comunicado.getPrioridad().isBlank()) {
            comunicado.setPrioridad("Normal");
        }
        Comunicado guardado = repository.save(comunicado);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
