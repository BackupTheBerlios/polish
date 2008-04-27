package de.enough.polish.ant.blackberry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class JDPTask extends Task {
	private static final String TOKEN = "@@FILES@@";

	String project;

	String template;

	String sources;

	public void execute() throws BuildException {
		String sourceDir = this.sources.trim() + "/source";

		String resourceDir = this.sources.trim() + "/classes";

		try {
			System.out.println("Reading template...");

			String content = readFile(new File(template)).trim();

			System.out.println("Collecting java classes from " + sourceDir
					+ "...");

			List sourceFiles = FileListing.getFileListing(new File(sourceDir),
					"java", true);

			System.out.println("Collecting resources from " + resourceDir
					+ "...");

			List resourceFiles = FileListing.getFileListing(new File(
					resourceDir), null, false);

			System.out.println("Writing files to project " + project
					+ "...");

			String list = getFileList(sourceFiles, resourceFiles).trim();
			
			content = content.replaceAll(TOKEN, list);
			
			writeFile(new File(project.trim()),content);
			
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private String readFile(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		
		int len = stream.available();
		
		String result = " ";

		for (int i = 1; i <= len; i++) {
			result = result + (char) stream.read();
		}

		return result;
	}
	
	private void writeFile(File file, String content) throws IOException
	{
		if(!file.exists())
		{
			file.createNewFile();
		}
		
		FileWriter writer = new FileWriter(file);

		writer.write(content);
		
		writer.close();
	}

	private String getFileList(List classes, List resources) {
		String result = "";
		
		String filename;

		for (int i = 0; i < classes.size(); i++) {
			File file = (File) classes.get(i);
			filename = file.getAbsolutePath().replace("\\", "\\\\");
			result += filename + "\r\n";
		}

		for (int i = 0; i < resources.size(); i++) {
			File file = (File) resources.get(i);
			filename = file.getAbsolutePath().replace("\\", "\\\\");
			result += filename + "\r\n";
		}

		return result;
	}

	public String getproject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
