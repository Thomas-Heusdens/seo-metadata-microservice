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

	@Bean
	CommandLineRunner init(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
		return args -> {

			Role userRole = roleRepo.findByName("USER")
					.orElseGet(() -> roleRepo.save(new Role("USER")));

			Role adminRole = roleRepo.findByName("ADMIN")
					.orElseGet(() -> roleRepo.save(new Role("ADMIN")));

			if (userRepo.findByUsername("user1").isEmpty()) {
				User u = new User();
				u.setUsername("user1");
				u.setPassword(encoder.encode("password"));
				u.getRoles().add(userRole);
				userRepo.save(u);
			}

			if (userRepo.findByUsername("admin").isEmpty()) {
				User a = new User();
				a.setUsername("admin");
				a.setPassword(encoder.encode("admin"));
				a.getRoles().add(adminRole);
				userRepo.save(a);
			}
		};
	}
}