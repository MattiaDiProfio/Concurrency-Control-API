package com.mdp.next;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mdp.next.repository.*;
import com.mdp.next.entity.*;

@SpringBootApplication
public class NextApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(NextApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// create two users with their respective accounts for debugging purposes
		User john = new User(null, "John Doe", "johndoe@yahoo.com", "12 Common Street EH8 3DD");
		User mary = new User(null, "Mary Jane", "maryjane@gmail.com", "4 Random Avenue JK3 5HL");
		Account johnAccount = new Account(john, 0.00, john.getID());
		john.setAccount(johnAccount);
		Account maryAccount = new Account(mary, 0.00, mary.getID());
		mary.setAccount(maryAccount);

		userRepository.save(john);
		userRepository.save(mary);
	}

}
