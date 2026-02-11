package org.example.Commands.exceptions;

public class CommandExecutionException extends RuntimeException {
    private boolean stopFlag = false;
    public CommandExecutionException(String message) {
        super(message);
    }

    public boolean isStopFlag() {
        return stopFlag;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }
}
