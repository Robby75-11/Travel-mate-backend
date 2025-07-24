package it.epicode.travel_mate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "it.epicode.travel_mate")
@EnableAsync
public class
TravelMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelMateApplication.class, args);
	}

}
