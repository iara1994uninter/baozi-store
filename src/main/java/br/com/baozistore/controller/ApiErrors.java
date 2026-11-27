package br.com.baozistore.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflito() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "A operação viola um vínculo obrigatório. Exclua primeiro os pedidos relacionados.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail entradaInvalida(MethodArgumentNotValidException erro) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Verifique os campos informados.");
        problema.setProperty("campos", erro.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage()).toList());
        return problema;
    }
}
