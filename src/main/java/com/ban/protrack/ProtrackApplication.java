package com.ban.protrack;

import com.ban.protrack.repository.ProjectRepository;
import com.ban.protrack.repository.UserRepository;
import com.ban.protrack.repository.WorkRepository;
import com.ban.protrack.service.implementation.FilesStorageServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProtrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProtrackApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	CommandLineRunner run(UserRepository userRepo, ProjectRepository projectRepo, WorkRepository workRepo, FilesStorageServiceImpl filesStorageService){
//		return args -> {
////			filesStorageService.init();
////			userRepo.save(new User(null, "dummy1", passwordEncoder().encode("1234"), null,
////					"dummy1@gmail.com", "091", ROLE_ADMIN, null));
////			userRepo.save(new User(null, "dummy2", passwordEncoder().encode("1234"), null,
////					"dummy2@gmail.com", "092", ROLE_USER, null));
////			userRepo.save(new User(null, "dummy3", passwordEncoder().encode("1234"), null,
////					"dummy3@gmail.com", "093", ROLE_USER, null));
////			userRepo.save(new User(null, "dummy4", passwordEncoder().encode("1234"), null,
////					"dummy4@gmail.com", "094", ROLE_USER, null));
//
////			projectRepo.save(new Project(null, "pdummy1", null, null));
////			projectRepo.save(new Project(null, "pdummy2", null, null));
////			projectRepo.addUsertoProject(1L, 1L, ROLE_ADMIN.toString());
////			projectRepo.addUsertoProject(2L, 1L, ROLE_USER.toString());
////			projectRepo.addUsertoProject(2L, 2L, ROLE_ADMIN.toString());
////			projectRepo.addUsertoProject(3L, 2L, ROLE_USER.toString());
////
////			workRepo.save(new Work("1_1", "wdummy1", "", 4L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_2", "wdummy2", "", 7L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_3", "wdummy3", "", 3L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_4", "wdummy4", "", 12L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_5", "wdummy5", "", 4L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_6", "wdummy6", "", 8L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_7", "wdummy7", "", 9L, null, null, null, null, null, null, "", false, null, null, null, null));
////			workRepo.save(new Work("1_8", "wdummy8", "", 7L, null, null, null, null, null, null, "", false, null, null, null, null));
////
////			workRepo.addWorkOrdertoProject("1_3", "1_4");
////			workRepo.addWorkOrdertoProject("1_1", "1_5");
////			workRepo.addWorkOrdertoProject("1_5", "1_6");
////			workRepo.addWorkOrdertoProject("1_4", "1_7");
////			workRepo.addWorkOrdertoProject("1_2", "1_8");
////			workRepo.addWorkOrdertoProject("1_6", "1_8");
////			workRepo.addWorkOrdertoProject("1_7", "1_8");
//		};
//	}

}
