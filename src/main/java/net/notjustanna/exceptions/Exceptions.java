package net.notjustanna.exceptions;

public class Exceptions {

    public static <T> T unknown(String at, Throwable cause) {
        throw new UnknownApplicationException(at, cause);
    }

    public static <T> T noSuchAccount() {
        throw new ApplicationException(ExceptionType.NO_SUCH_ACCOUNT);
    }

    public static <T> T inconsistentState() {
        throw new ApplicationException(ExceptionType.INCONSISTENT_STATE);
    }

    public static <T> T noSuchChannel() {
        throw new ApplicationException(ExceptionType.NO_SUCH_CHANNEL);
    }

    public static <T> T noSuchAudioInput() {
        throw new ApplicationException(ExceptionType.NO_SUCH_AUDIO_INPUT);
    }

    public static <T> T invalidToken() {
        throw new ApplicationException(ExceptionType.INVALID_TOKEN);
    }

    public static <T> T emptyToken() {
        throw new ApplicationException(ExceptionType.EMPTY_TOKEN);
    }

    public static <T> T rememberMeFailed() {
        throw new ApplicationException(ExceptionType.REMEMBER_ME_FAILED);
    }

    public static <T> T rememberMeInvalidToken() {
        throw new ApplicationException(ExceptionType.REMEMBER_ME_INVALID_TOKEN);
    }

    public static <T> T unsupportedAction() {
        throw new ApplicationException(ExceptionType.UNSUPPORTED_ACTION);
    }

    public static <T> T audioInputFailed() {
        throw new ApplicationException(ExceptionType.AUDIO_INPUT_FAILED);
    }

    public static <T> T noSuchStream() {
        throw new ApplicationException(ExceptionType.NO_SUCH_STREAM);
    }
}
