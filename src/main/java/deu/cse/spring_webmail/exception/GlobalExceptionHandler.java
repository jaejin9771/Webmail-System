package deu.cse.spring_webmail.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 *
 * @author jiye
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        CannotCreateTransactionException.class,
        JpaSystemException.class,
        DataAccessException.class
    })
    public String handleDatabaseError(Exception e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/db_error";
    }
}
