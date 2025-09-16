package ps.exalt.healthcare_appointment_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableMongoRepositories
public class HealthcareAppointmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthcareAppointmentSystemApplication.class, args);
	}

}
