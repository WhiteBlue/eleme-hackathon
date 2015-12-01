package cn.whiteblue.exceptions;

/**
 * Description:
 * <p>
 * ======================
 * by WhiteBlue
 * on 15/11/12
 */
public class NullRequestException extends Exception {
    public NullRequestException() {
    }

    public NullRequestException(String message) {
        super(message);
    }
}
