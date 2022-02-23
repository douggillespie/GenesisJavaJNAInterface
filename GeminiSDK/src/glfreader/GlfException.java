package glfreader;

public class GlfException extends Exception {

	private static final long serialVersionUID = 1L;

	public GlfException() {
		super();
	}

	public GlfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GlfException(String message, Throwable cause) {
		super(message, cause);
	}

	public GlfException(String message) {
		super(message);
	}

	public GlfException(Throwable cause) {
		super(cause);
	}

}
