package geminisdk;

public class Svs5Exception extends Exception {

	private static final long serialVersionUID = 1L;

	public Svs5Exception(String message) {
		super(message);
	}
	
	public Svs5Exception(int svs5Error) {
		super(String.format("Error %d: %s", svs5Error, Svs5ErrorType.getErrorName(svs5Error)));
	}


}
