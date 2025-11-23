package be.thomasheusdens.seo_metadata_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeoMetadataMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeoMetadataMicroserviceApplication.class, args);
	}
}