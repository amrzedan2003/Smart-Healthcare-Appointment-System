package ps.exalt.healthcare_appointment_system.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    // Log appointment booking
    @Before("execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.bookAppointment(..))")
    public void logBeforeBookAppointment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("APPOINTMENT BOOKING - User: {} is attempting to book an appointment", username);
    }

    @AfterReturning(value = "execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.bookAppointment(..))", returning = "result")
    public void logAfterBookAppointment(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("APPOINTMENT BOOKING - SUCCESS: User: {} successfully booked an appointment", username);
    }

    @AfterThrowing(value = "execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.bookAppointment(..))", throwing = "exception")
    public void logBookAppointmentError(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.error("APPOINTMENT BOOKING - ERROR: User: {} failed to book appointment. Error: {}", username,
                exception.getMessage());
    }

    // Log appointment cancellation
    @Before("execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.cancelAppointment(..))")
    public void logBeforeCancelAppointment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long appointmentId = args.length > 0 ? (Long) args[0] : null;
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("APPOINTMENT CANCELLATION - User: {} is attempting to cancel appointment ID: {}", username,
                appointmentId);
    }

    @AfterReturning("execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.cancelAppointment(..))")
    public void logAfterCancelAppointment(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long appointmentId = args.length > 0 ? (Long) args[0] : null;
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("APPOINTMENT CANCELLATION - SUCCESS: User: {} successfully cancelled appointment ID: {}", username,
                appointmentId);
    }

    @AfterThrowing(value = "execution(* ps.exalt.healthcare_appointment_system.service.AppointmentService.cancelAppointment(..))", throwing = "exception")
    public void logCancelAppointmentError(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        Long appointmentId = args.length > 0 ? (Long) args[0] : null;
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.error("APPOINTMENT CANCELLATION - ERROR: User: {} failed to cancel appointment ID: {}. Error: {}",
                username, appointmentId, exception.getMessage());
    }

    // Log prescription creation/updates
    @Before("execution(* ps.exalt.healthcare_appointment_system.service.PrescriptionService.createPrescription(..))")
    public void logBeforeCreatePrescription(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("PRESCRIPTION UPDATE - Doctor: {} is creating a new prescription", username);
    }

    @AfterReturning(value = "execution(* ps.exalt.healthcare_appointment_system.service.PrescriptionService.createPrescription(..))", returning = "result")
    public void logAfterCreatePrescription(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.info("PRESCRIPTION UPDATE - SUCCESS: Doctor: {} successfully created a prescription", username);
    }

    @AfterThrowing(value = "execution(* ps.exalt.healthcare_appointment_system.service.PrescriptionService.createPrescription(..))", throwing = "exception")
    public void logCreatePrescriptionError(JoinPoint joinPoint, Exception exception) {
        Object[] args = joinPoint.getArgs();
        String username = args.length > 1 ? (String) args[1] : "Unknown";
        log.error("PRESCRIPTION UPDATE - ERROR: Doctor: {} failed to create prescription. Error: {}",
                username, exception.getMessage());
    }
}
