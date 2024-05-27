package com.mdp.next;

import java.time.LocalDate;
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
		// create two users with their respective accounts for debugging purposes
		userRepository.save(new User("John Doe", "johndoe@yahoo.com", "12 Common Street EH8 3DD"));
		userRepository.save(new User("Mary Jane", "maryjane@gmail.com", "4 Random Avenue JK3 5HL"));

		User john = userService.getUser(1L);
		User mary = userService.getUser(2L);

		Account johnAccount = new Account(john, 0.00, 1L);
		Account maryAccount = new Account(mary, 0.00, 2L);

		john.setAccount(johnAccount);
		mary.setAccount(maryAccount);

		userRepository.save(john);
		userRepository.save(mary);
	}

}
