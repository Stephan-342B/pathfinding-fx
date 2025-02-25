package org.mahefa.common.exceptions;

public class MissingAlgorithmException extends RuntimeException {

    public MissingAlgorithmException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public MissingAlgorithmException(String errorMessage) {
        super(errorMessage);
    }
    public MissingAlgorithmException() {
        super();
    }
    
}
