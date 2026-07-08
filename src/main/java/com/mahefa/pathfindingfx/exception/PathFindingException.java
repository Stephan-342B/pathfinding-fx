package com.mahefa.pathfindingfx.exception;

public class PathFindingException extends RuntimeException {

    public PathFindingException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public PathFindingException(String errorMessage) {
        super(errorMessage);
    }
    public PathFindingException() {
        super();
    }

}