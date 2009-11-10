/**
 * 
 */
package de.enough.polish.sample.pimapi;

public class Message{
	private boolean error;
	private String testName;
	private String message;
	public Message(boolean error, String testName, String message) {
		this.error = error;
		this.testName = testName;
		this.message = message;
	}
	public String getMessage() {
		return this.message;
	}
	public String getTestName() {
		return this.testName;
	}
	public boolean isError() {
		return this.error;
	}
	@Override
	public String toString() {
		String status = "";
		if(this.error) {
			status = "ERROR:";
		}
		return status+this.message+"("+this.testName+")";
	}
}