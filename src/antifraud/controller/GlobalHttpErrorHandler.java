package antifraud.controller;

import antifraud.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.relation.RoleNotFoundException;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalHttpErrorHandler {

    @ExceptionHandler({
            StatusNotFoundException.class,
            AdminChangeAccessException.class,
            AdminChangeRoleException.class,
            AdminAlreadyExistsException.class,
            ConstraintViolationException.class,
            StolenCardNumberBadFormatException.class
    })
    ResponseEntity<String> errorHandlerBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler({
            RoleAlreadySetException.class,
            RecordAlreadyExistsException.class
    })
    ResponseEntity<String> errorHandlerConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    @ExceptionHandler({
            RoleNotFoundException.class,
            UsernameNotFoundException.class,
            SuspiciousIpNotFoundException.class,
            StolenCardNotFoundException.class,
            TransactionNotFoundException.class
    })
    ResponseEntity<String> errorHandlerNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
