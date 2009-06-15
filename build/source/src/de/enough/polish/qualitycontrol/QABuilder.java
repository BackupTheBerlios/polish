package de.enough.polish.qualitycontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom.JDOMException;

import de.enough.polish.devices.BugManager;
import de.enough.polish.exceptions.InvalidComponentException;
import de.enough.polish.util.StringUtil;

public class QABuilder {

	private boolean isVerbose;
	private VariablesManager variablesManager;
	private BugManager bugsManager;
	private PolishVariable[] variables;


	public QABuilder(File polishHome) throws JDOMException, IOException, InvalidComponentException {
		this.variablesManager = new VariablesManager( polishHome );
		this.bugsManager = new BugManager(polishHome);
		
		this.variables = this.variablesManager.getVisibleVariables();
	}


	private void build(File projectHome) {
		String[][] permutations = getVariablePermuations(); 
		for (int i = 0; i < permutations.length; i++) {
			String[] polishVariables = permutations[i];
			ArrayList args = new ArrayList();
			File errorFile = new File( projectHome, "errors.log");
			if (errorFile.exists()) {
				boolean success = errorFile.delete();
				if (!success) {
					handleConfigError( "Unable to delete " + errorFile.getAbsolutePath() );
				}
			}
			args.add("ant");
			args.add("-Dpolish.buildcontrol.errorhandler=de.enough.polish.qualitycontrol.FileErrorHandler");
			args.add("-Dfileerrorhandler.file=" + errorFile.getAbsolutePath());
			
			for (int j = 0; j < polishVariables.length; j++) {
				String polishVariable = polishVariables[j];
				args.add("-D" + polishVariable );
			}
		}
		
	}

	private String[][] getVariablePermuations() {
		int numberOfBooleanVariables = 0;
		int numberOfBooleanPermuations = 1;
		for (int i=0; i<this.variables.length; i++) {
			PolishVariable var = this.variables[i];
			if ("boolean".equals(var.getType())) {
				numberOfBooleanVariables++;
				numberOfBooleanPermuations *= numberOfBooleanVariables;
			}
		}
		String[][] perms = new String[numberOfBooleanPermuations][];
		for (int i = 0; i < perms.length; i++) {
			int booleanIndex = 0;
			String[] perm = new String[this.variables.length];
			for (int j=0; j<this.variables.length; j++) {
				PolishVariable var = this.variables[j];
				if ("boolean".equals(var.getType())) {
					perm[j] = var.getName() + "=" + getBooleanPermuationValue( booleanIndex, numberOfBooleanVariables, numberOfBooleanPermuations );
					booleanIndex++;
				} else {
					perm[j] = var.getName() + "=" + var.getDefaultValue();
				}
			}
			
			perms[i] = perm;
		}
		
		return perms;
	}


	private String getBooleanPermuationValue(int index,
			int numberOfBooleanVariables, int numberOfBooleanPermuations) 
	{
		if (index == 0) {
			return "true";
		} else if (index == 1) {
			return "false";
		} else {
			return "false";
		}
	}


	private static void handleConfigError(String text) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @param args
	 * @throws IOException 
	 * @throws InvalidComponentException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args)  {
		try {
			String polishHome = null;
			String projectsHome = null;
			boolean isVerbose = false;
			if (args.length > 1) {
				for (int i = 0; i < args.length; i++) {
					String arg = args[i];
					if (arg.startsWith("polish.home=")) {
						polishHome = arg.substring("polish.home=".length());
					} else if (arg.startsWith("project=")) {
						projectsHome = arg.substring("project=".length());
					} else if (arg.startsWith("projects=")) {
						projectsHome = arg.substring("projects=".length());
					} else if (arg.equals("-verbose")) {
						isVerbose = true;
					}
				}
			} else {
				polishHome = System.getProperty("polish.home");
				projectsHome = System.getProperty("projects");
				if (projectsHome == null) {
					projectsHome = System.getProperty("project");
				}
			}
			if (polishHome == null || projectsHome == null ) {
				usage();
				System.exit(1);
			}
			if (isVerbose) {
				System.out.println("QABuilder launching...");
			}
			QABuilder builder = new QABuilder( new File( polishHome ) );
			builder.isVerbose = isVerbose;
			if (isVerbose) {
				System.out.println("Starting with project...");
			}
			String[] projects = StringUtil.split(projectsHome, File.pathSeparatorChar);
			for (int i = 0; i < projects.length; i++) {
				String projectHome = projects[i];
				builder.build( new File( projectHome)  );
			}
		} catch (Exception e) {
			handleConfigError( "Unexpected configuration problem: " + e.toString() );
			System.err.println("Configuration error: " + e.toString() );
			e.printStackTrace();
		}
	}




	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java de.enough.polish.qualitycontrol.QABuilder [-verbose] polish.home=${polish.home} project(s)=${path to project(s)}");
		System.out.println("Order of parameters does not matter, parameter can also be given as environment variables.");
	}

}
