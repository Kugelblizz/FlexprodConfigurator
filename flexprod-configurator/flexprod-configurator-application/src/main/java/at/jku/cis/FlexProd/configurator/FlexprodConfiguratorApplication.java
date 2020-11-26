package at.jku.cis.FlexProd.configurator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FlexprodConfiguratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexprodConfiguratorApplication.class, args);
	}
}
