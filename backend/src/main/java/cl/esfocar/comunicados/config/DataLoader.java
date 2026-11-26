package cl.esfocar.comunicados.config;

import cl.esfocar.comunicados.model.Comunicado;
import cl.esfocar.comunicados.repository.ComunicadoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarComunicadosIniciales(ComunicadoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Comunicado(
                    "Formación matinal 07:30 hrs",
                    "Presentarse con uniforme de servicio en patio central para revista semanal.",
                    "Jefatura de Instrucción",
                    "2026-11-26",
                    "Alta"
                ));
                repository.save(new Comunicado(
                    "Entrega de rúbricas DSY1105",
                    "Los documentos oficiales de evaluación se encuentran disponibles en la sección Rúbricas.",
                    "Coordinación Académica",
                    "2026-11-25",
                    "Normal"
                ));
                repository.save(new Comunicado(
                    "Actualización calendario de feriados",
                    "Revisar turnos y servicios asignados para los próximos días festivos nacionales.",
                    "Subdirección Operativa",
                    "2026-11-24",
                    "Normal"
                ));
            }
        };
    }
}
