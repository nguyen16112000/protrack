package com.ban.protrack;

import com.ban.protrack.model.User;
import com.ban.protrack.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static com.ban.protrack.enumeration.Role.ROLE_ADMIN;
import static com.ban.protrack.enumeration.Role.ROLE_USER;

@SpringBootApplication
public class ProtrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtrackApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run(UserRepository userRepo){
//		return args -> {
//			userRepo.save(new User(null, "dummy1", "1234", null,
//					"dummy1@gmail.com", "091", ROLE_ADMIN, null));
//			userRepo.save(new User(null, "dummy2", "1234", null,
//					"dummy2@gmail.com", "092", ROLE_USER, null));
//			userRepo.save(new User(null, "dummy3", "1234", null,
//					"dummy3@gmail.com", "093", ROLE_USER, null));
//			userRepo.save(new User(null, "dummy4", "1234", null,
//					"dummy4@gmail.com", "094", ROLE_USER, null));
//		};
//	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "Filename"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

}
