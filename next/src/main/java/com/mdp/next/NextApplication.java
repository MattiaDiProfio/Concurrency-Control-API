package com.mdp.next;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mdp.next.repository.*;
import com.mdp.next.service.*;
import com.mdp.next.entity.*;

@SpringBootApplication
public class NextApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(NextApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User john = new User("john", "john@email.com", "123 address");
		User amy = new User("amy", "amy@email.com", "123 address");
		User tom = new User("tom", "tom@email.com", "123 address");

		userRepository.save(john);
		userRepository.save(amy);
		userRepository.save(tom);
	}

}
