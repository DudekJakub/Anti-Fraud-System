package antifraud.config;

import antifraud.model.Region;
import antifraud.model.Role;
import antifraud.model.User;
import antifraud.repository.RegionRepository;
import antifraud.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Slf4j
@Configuration
@AllArgsConstructor
public class H2Config {

    RegionRepository regionRepository;

    @Bean
    ApplicationRunner run(RegionRepository regionRepository) {
        return (ApplicationArguments arg) -> {
            List<Region> regions = List.of(
                    new Region("EAP", "East Asia and Pacific"),
                    new Region("ECA", "Europe and Central Asia"),
                    new Region("HIC", "High-Income countries"),
                    new Region("LAC", "Latin America and the Caribbean"),
                    new Region("MENA", "The Middle East and North Africa"),
                    new Region("SA", "South Asia"),
                    new Region("SSA", "Sub-Saharan Africa")
            );

            regionRepository.saveAll(regions);
        };
    }

    @Bean(name = "validRegions")
    @Lazy
    List<Region> getRegions() {
        return regionRepository.findAll();
    }
}
