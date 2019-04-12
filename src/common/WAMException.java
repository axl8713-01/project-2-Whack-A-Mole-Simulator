package common;


/**
 * A custom exception class to be thrown by any of the WAM class in the case something
 * goes awry.
 *
 * @author Liang, Albin
 * @author Souza, Saakshi
 */
public class WAMException extends Exception{

    /**
     * Constructor to create a new WAMexception with an error message
     *
     * @param message the error message received when the exception is thrown.
     */
    public WAMException(String message){super(message);}


    /**
     * overloaded constructor that creates a new exception with the specified cause
     *
     * @param cause the specific cause to throw this new exception.
     */
    public WAMException(Throwable cause){super(cause);}


    /**
     * Another constructor that makes a new exception with a specified message and cause
     *
     * @param message the specific message
     * @param cause the specific cause
     */
    public WAMException(String message, Throwable cause){super(message, cause);}

}

