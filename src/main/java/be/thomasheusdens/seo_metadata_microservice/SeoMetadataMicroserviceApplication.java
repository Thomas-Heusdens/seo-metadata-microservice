package be.thomasheusdens.seo_metadata_microservice;

import be.thomasheusdens.seo_metadata_microservice.user.Role;
import be.thomasheusdens.seo_metadata_microservice.user.RoleRepository;
import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SeoMetadataMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeoMetadataMicroserviceApplication.class, args);
	}
}