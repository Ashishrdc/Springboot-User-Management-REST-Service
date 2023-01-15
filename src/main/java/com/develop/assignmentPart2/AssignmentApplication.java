package com.develop.assignmentPart2;

import com.develop.assignmentPart2.model.Role;
import com.develop.assignmentPart2.repository.RoleRepository;
import org.hibernate.annotations.SQLInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.develop.assignmentPart2")
public class AssignmentApplication implements CommandLineRunner {

	@Autowired
	RoleRepository roleRepository;
	public static void main(String[] args) {
		SpringApplication.run(AssignmentApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		Role admin = new Role();
		admin.setId(1);
		admin.setName("ROLE_ADMIN");

		Role user = new Role();
		user.setId(2);
		user.setName("ROLE_USER");
		roleRepository.save(admin);
		roleRepository.save(user);

	}
}
