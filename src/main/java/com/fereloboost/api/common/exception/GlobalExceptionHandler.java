package com.fereloboost.api.common.exception;

import com.fereloboost.api.identity.application.service.EmailAlreadyInUseException;
import com.fereloboost.api.identity.application.service.InvalidCredentialsException;
import com.fereloboost.api.identity.application.service.InvalidRefreshTokenException;
import com.fereloboost.api.identity.application.service.UserNotFoundException;
import com.fereloboost.api.identity.application.service.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    ProblemDetail handleEmailAlreadyInUse(EmailAlreadyInUseException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Kayıt yapılamadı");
        return problem;
    }

    @ExceptionHandler(RoleNotFoundException.class)
    ProblemDetail handleRoleNotFound(RoleNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        problem.setTitle("Kimlik yapılandırması hatalı");
        return problem;
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidRefreshTokenException.class, UserNotFoundException.class})
    ProblemDetail handleAuthenticationFailure(RuntimeException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problem.setTitle("Kimlik doğrulama başarısız");
        return problem;
    }
}
