package org.example.exceptions;

public class CommandExecutionException extends AppException {
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
