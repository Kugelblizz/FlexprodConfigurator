package at.jku.cis.FlexProd.configurator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableScheduling
@SpringBootApplication
public class FlexprodConfiguratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlexprodConfiguratorApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
//        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

		return mapper;
	}
}
