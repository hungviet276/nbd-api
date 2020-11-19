package com.neo.nbdapi.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.neo.nbdapi.utils.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@RestController
public class ApplicationResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @SuppressWarnings("unused")
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String message = "";
        if (fieldErrorList.size() > 0) {
            message = fieldErrorList.get(0).getDefaultMessage();
        }
        ResponseBasicObj responseBasicObj = new ResponseBasicObj(Constants.EXCEPTION.BAD_REQUEST, message);
        return new ResponseEntity(responseBasicObj, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("unchecked")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ResponseBasicObj responseBasicObj =
                new ResponseBasicObj(Constants.EXCEPTION.INTERNAL_SERVER_ERROR,
                        "Lỗi hệ thống!");

        return new ResponseEntity(responseBasicObj, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * The method handle invalid username or password throw by AbstractUserDetailAuthenticationProvider
     *
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(WebRequest request) {
        ResponseBasicObj responseBasicObj = new ResponseBasicObj(400, "Tài khoản hoặc mật khẩu không đúng");

        return new ResponseEntity(responseBasicObj, HttpStatus.BAD_REQUEST);
    }

    /**
     * The method handle only StoreBusinessException
     *
     * @param ex
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleAgencyException(BusinessException ex, WebRequest request) {

        ResponseBasicObj responseBasicObj = new ResponseBasicObj(ex.getCode(),
                ex.getMessage());

        return new ResponseEntity(responseBasicObj, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseBasicObj responseBasicObj =
                new ResponseBasicObj(Constants.EXCEPTION.BAD_REQUEST,
                        "Truyền thiếu tham số :" + ex.getParameterName());
        return new ResponseEntity(responseBasicObj, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseBasicObj responseBasicObj =
                new ResponseBasicObj(Constants.EXCEPTION.BAD_REQUEST,
                        "Truyền thiếu tham số :" + ex.getVariableName());
        return new ResponseEntity(responseBasicObj, HttpStatus.BAD_REQUEST);
    }
}
