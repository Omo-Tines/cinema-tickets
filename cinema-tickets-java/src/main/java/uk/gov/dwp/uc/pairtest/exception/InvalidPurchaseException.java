package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException (String errormessage){
        super(errormessage); /* this ensures it takes the message of the parent class where it is called */
    }
}
