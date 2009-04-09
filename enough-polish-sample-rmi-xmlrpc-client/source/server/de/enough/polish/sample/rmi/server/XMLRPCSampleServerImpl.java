package de.enough.polish.sample.rmi.server;

import org.apache.log4j.Logger;


public class XMLRPCSampleServerImpl implements XMLRPCSampleServer{

	private static Logger logger = Logger.getLogger(XMLRPCSampleServerImpl.class);
		
	public int sub(int i, int j){
		logger.info("Sub: " + i + " - " + j + " = " + String.valueOf(i-j));
		return i - j;
	}

	public int sum(int i, int j){
		logger.info("Sum: " + i + " + " + j + " = " + String.valueOf(i+j));
		return i+j;
	}

}