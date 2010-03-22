package de.enough.skylight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import de.enough.skylight.js.JsEngine;

public class Main {

	public static void main(String[] args) throws IOException {
		
		if(args.length == 0) {
			throw new IllegalArgumentException("A filename is needed as the first parameter");
		}
		
		String filename = args[0];
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		StringBuffer buffer = new StringBuffer();
		String line = reader.readLine();
		while(line != null) {
			buffer.append(line);
			line = reader.readLine();
		}
		JsEngine jsEngine = new JsEngine();
		jsEngine.runScript(buffer.toString());
	}
}
