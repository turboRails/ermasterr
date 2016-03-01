package org.insightech.er.common.exception;

public class InputException extends Exception {

    private static final long serialVersionUID = -6325812774566059357L;

    private String[] args;

    public InputException() {}

    public InputException(final String message) {
        super(message);
    }

    public InputException(final Throwable exception) {
        super(exception);
    }

    public InputException(final String message, final String[] args) {
        super(message);

        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

}
