package bovisApp.firstApp.config;

import bovisApp.firstApp.model.Boi;
import bovisApp.firstApp.model.Lote;
import bovisApp.firstApp.model.Raca;
import bovisApp.firstApp.model.enumeration.EstadoBoi;
import bovisApp.firstApp.model.enumeration.EstadoLote;
import bovisApp.firstApp.repository.BoiRepository;
import bovisApp.firstApp.repository.LoteRepository;
import bovisApp.firstApp.repository.RacaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Date;
import java.util.List;



@Configuration
public class AppConfiguration {

    @Bean
    CommandLineRunner commandLineRunner (BoiRepository boiRepository, LoteRepository loteRepository, RacaRepository racaRepository){
        return args-> {

            Raca nelore = new Raca("nelore");
            Raca angus = new Raca("angus");

            racaRepository.saveAll(List.of(nelore, angus));

            Lote lote = new Lote(
                    new Date(2021, 1, 1),
                    EstadoLote.getEstadoLote("Nada_vendido")
            );
            loteRepository.save(lote);


            Boi boi1 = new Boi(
                    1,
                    nelore,
                    lote,
                    EstadoBoi.getEstadoBoi("Na_fazenda")
            );

            Boi boi2 = new Boi(
                    2,
                    nelore,
                    lote,
                    EstadoBoi.getEstadoBoi("Na_fazenda")
            );

            Boi boi3 = new Boi(
                    3,
                    angus,
                    lote,
                    EstadoBoi.getEstadoBoi("Na_fazenda")
            );

            Boi boi4 = new Boi(
                    4,
                    nelore,
                    lote,
                    EstadoBoi.getEstadoBoi("Na_fazenda")
            );

            boiRepository.saveAll(List.of(boi1, boi2, boi3, boi4));

        };

    }
}
