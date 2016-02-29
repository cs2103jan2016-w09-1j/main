package esther;

import java.lang.Exception;

public class InvalidInputException extends Exception {

	public InvalidInputException() {
		super("Input is invalid. You may have missed out important details.");
	}
	
}
