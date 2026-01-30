package tn.enis.pfa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendPfaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(BackendPfaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("a7la PFA m3a a7la ness");
		
	}

}
