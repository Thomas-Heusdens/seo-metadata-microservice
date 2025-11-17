package be.thomasheusdens.seo_metadata_microservice;

import be.thomasheusdens.seo_metadata_microservice.user.Role;
import be.thomasheusdens.seo_metadata_microservice.user.RoleRepository;
import be.thomasheusdens.seo_metadata_microservice.user.User;
import be.thomasheusdens.seo_metadata_microservice.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SeoMetadataMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeoMetadataMicroserviceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepo, RoleRepository roleRepo) {
		return args -> {
			try {
				if (roleRepo.findByName("ROLE_USER") == null) {
					roleRepo.save(new Role("ROLE_USER"));
				}
				if (!userRepo.existsByEmail("admin@test.com")) {
					User admin = new User();
					admin.setEmail("admin@test.com");
					admin.setPasswordHash("test123");
					admin.getRoles().add(roleRepo.findByName("ROLE_USER"));
					userRepo.save(admin);
				}
			} catch (Exception e) {
				System.out.println("SEED ERROR: " + e.getMessage());
			}
		};
	}
}