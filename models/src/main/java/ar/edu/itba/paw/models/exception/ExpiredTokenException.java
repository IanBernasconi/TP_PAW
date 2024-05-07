package ar.edu.itba.paw.models.exception;

public class ExpiredTokenException extends VerificationException{

        public ExpiredTokenException(String message) {
            super(message);
        }

}
