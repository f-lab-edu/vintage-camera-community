package com.zerozone.vintage.exception;

public class EmailException {
    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String message) {
            super(message);
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    public static class EmailSendException extends RuntimeException {
        public EmailSendException(String message) {
            super(message);
        }
    }
}
