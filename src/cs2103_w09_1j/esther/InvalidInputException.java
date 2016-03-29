package cs2103_w09_1j.esther;

/**
 * A custom exception class to be used in ESTHER.
 * 
 * @author Tay Guo Qiang
 */

public class InvalidInputException extends Exception {

	public InvalidInputException() {
		super("Input is invalid. You may have missed out important details.");
	}
	
	/**
	 * For parser
	 * @author HuiShan
	 */
	public InvalidInputException(String errorMessage){
		super(errorMessage);
	}
	
}
