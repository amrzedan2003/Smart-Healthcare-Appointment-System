package ps.exalt.healthcare_appointment_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authz -> authz
                                                // Public endpoints - Only login
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // Patient endpoints -- Doctor search
                                                .requestMatchers("/api/doctors/search/**", "/api/doctors/active",
                                                                "/api/doctors/specialties")
                                                .hasRole("PATIENT")

                                                // Patient endpoints -- Appointment booking and cancellation
                                                .requestMatchers("/api/appointments/book/**",
                                                                "/api/appointments/*/cancel/*")
                                                .hasRole("PATIENT")

                                                // Patient endpoints -- View patient appointments
                                                .requestMatchers("/api/appointments/patient/**")
                                                .hasRole("PATIENT")

                                                // Patient endpoints -- View appointment details for patients
                                                .requestMatchers("/api/appointments/*/patient/*")
                                                .hasRole("PATIENT")

                                                // Doctor endpoints -- Complete appointments and view doctor
                                                // appointments
                                                .requestMatchers("/api/appointments/*/complete/*",
                                                                "/api/appointments/doctor/**")
                                                .hasRole("DOCTOR")

                                                // Doctor endpoints -- View appointment details for doctors
                                                .requestMatchers("/api/appointments/*/doctor/*")
                                                .hasRole("DOCTOR")

                                                // Both patient and doctor endpoints -- View available slots
                                                .requestMatchers("/api/appointments/slots/**")
                                                .hasAnyRole("PATIENT", "DOCTOR")

                                                // Doctor endpoints -- Time slots management (doctors can manage their
                                                // own schedules)
                                                .requestMatchers("/api/doctors/time-slots/**")
                                                .hasAnyRole("DOCTOR")

                                                // Prescription endpoints -- Doctors create prescriptions
                                                .requestMatchers("/api/prescriptions").hasRole("DOCTOR")

                                                // Prescription endpoints -- Doctors view their own prescriptions
                                                .requestMatchers("/api/prescriptions/doctor/my-prescriptions")
                                                .hasRole("DOCTOR")

                                                // Prescription endpoints -- Patients view their own prescriptions
                                                .requestMatchers("/api/prescriptions/my-records")
                                                .hasRole("PATIENT")

                                                // Prescription endpoints -- Both doctors and patients can view specific
                                                // prescriptions
                                                .requestMatchers("/api/prescriptions/*")
                                                .hasAnyRole("DOCTOR", "PATIENT")

                                                // Admin endpoints -- Doctor management
                                                .requestMatchers("/api/doctors/**").hasRole("ADMIN")
                                                .requestMatchers("/api/patients/**").hasRole("ADMIN")

                                                // All other requests need authentication
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
