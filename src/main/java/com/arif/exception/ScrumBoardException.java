package com.arif.exception;

/**
 * Custom exception class
 * 
 * @author arifakrammohammed
 *
 */
public class ScrumBoardException extends Exception {

	private static final long serialVersionUID = 1L;

	public ScrumBoardException() {
		super();
	}

	public ScrumBoardException(String message) {
		super(message);
	}
}
