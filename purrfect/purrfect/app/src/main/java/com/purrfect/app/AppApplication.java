package com.purrfect.app;

import java.time.LocalDate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.purrfect.app.model.Application;
import com.purrfect.app.model.ApplicationRepository;
import com.purrfect.app.model.Job;
import com.purrfect.app.model.JobRepository;
import com.purrfect.app.model.JobType;
import com.purrfect.app.model.StatusType;
import com.purrfect.app.model.User;
import com.purrfect.app.model.UserRepository;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	ApplicationRunner init(UserRepository userRepo, JobRepository jobRepo, ApplicationRepository appRepo) {
		return arg -> {

			// CREATE USER EXAMPLE
			User poster = new User("Sahil", "Narang", "Surrey, BC", "13623475906",
					"sahil@outlook.com", "1234");
			User buster = new User("Prashneel", "Chand", "Burnaby, BC", "1234567891", "prashneel@gmail.com",
					 "1234");
			User buster2 = new User("Vy", "Do", "Burnaby, BC", "1234567891", "vy@example.com",
					 "1234");

			userRepo.save(poster); 
			userRepo.save(buster); //aka applicant
			userRepo.save(buster2);

			// CREATE JOB LISTING EXAMPLE
			Job job1 = new Job(poster, "Chillest Cat in the World!", LocalDate.of(2023, 2, 24),
					20.00, JobType.CAT,
					"Looking for someone to play with me",
					LocalDate.of(2023, 03, 10), LocalDate.of(2023, 3, 10), "/Users/sahilnarang/Desktop/purrfect/purrfect/cat1.jpg");
			Job job2 = new Job(poster, "I'm a Playful Dog", LocalDate.of(2023, 2, 24),
					18.00, JobType.DOG,
					"Looking for someone to play fetch with me",
					LocalDate.of(2023, 03, 1), LocalDate.of(2023, 3, 1), "/Users/sahilnarang/Desktop/purrfect/purrfect/dog1.jpg");

			poster.addJob(job1);
			poster.addJob(job2);

			jobRepo.save(job1);
			jobRepo.save(job2);

			// CREATE JOB APPLICATION EXAMPLE
			Application app1 = new Application(job1, buster, StatusType.APPLIED);
			job1.addApplication(app1);
			buster.addApplication(app1);
			appRepo.save(app1);

			userRepo.findAll().forEach(System.out::println);
			jobRepo.findAll().forEach(System.out::println);
			appRepo.findAll().forEach(System.out::println);
		};
	};
}