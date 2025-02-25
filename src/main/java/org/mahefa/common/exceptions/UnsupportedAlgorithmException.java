package org.mahefa.common.exceptions;

public class UnsupportedAlgorithmException extends RuntimeException {

    public UnsupportedAlgorithmException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public UnsupportedAlgorithmException(String errorMessage) {
        super(errorMessage);
    }
    public UnsupportedAlgorithmException() {
        super();
    }
    
}
