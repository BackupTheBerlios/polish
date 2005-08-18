package de.enough.polish.emulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.Project;

import de.enough.polish.BooleanEvaluator;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.FileUtil;

/**
 * Invokes a specific BlackBerry simulator.
 * @author Robert Virkus
 */
public class BlackBerryEmulator extends Emulator {

	private File blackberryHome;
	private String[] arguments;

	public boolean init( Device dev, EmulatorSetting setting,
			Environment env, Project project, BooleanEvaluator evaluator,
			String wtkHome ) 
	{
		String blackberryHomeStr = env.getVariable("blackberry.home");
		if (blackberryHomeStr == null) {
			System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" is not set." );
			return false;
		}
		this.blackberryHome = new File( blackberryHomeStr );
		if ( !this.blackberryHome.exists() ) {
			System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" points to an invalid directory: " + this.blackberryHome.getAbsolutePath()  );
			return false;
		}
		File executable = new File( this.blackberryHome, "simulator" + File.separatorChar + dev.getName() + ".bat" );
		if ( !executable.exists() ) {
			System.err.println("Unable to start blackberry simulator: simulator not found: " + executable.getAbsolutePath()  );
			return false;
		}
		ArrayList argumentsList = new ArrayList();
		if (File.separatorChar == '/') { // this is a unix environment, try wine:
			argumentsList.add("wine");
			argumentsList.add( executable.getAbsolutePath() );
			argumentsList.add("--");
		} else {
			argumentsList.add( executable.getAbsolutePath() );
		}
		this.arguments = (String[]) argumentsList.toArray( new String[ argumentsList.size() ] );
		
		// now copy the jar, cod, alx and jad files to the simulator's home directory:
		File targetDir = executable.getParentFile();
		File file = new File( env.getVariable("polish.jadPath") );
		try {
			//FileUtil.copy( file, targetDir );
			//file = new File( env.getVariable("polish.jarPath") );
			//FileUtil.copy( file, targetDir );
			String baseName = file.getAbsolutePath();
			baseName = baseName.substring( 0, baseName.length() - ".jar".length() );
			file = new File( baseName + ".cod" );
			FileUtil.copy( file, targetDir );
			file = new File( baseName + ".alx" );
			FileUtil.copy( file, targetDir );
		} catch ( IOException e ) {
			e.printStackTrace();
			System.err.println("Unable to copy BlackBerry resources to the simulator directory: " + e.toString() );
			return false;
		}
		return true;
	}

	public String[] getArguments() {
		return this.arguments;
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.emulator.Emulator#getExecutionDir()
	 */
	protected File getExecutionDir() {
		return new File( this.blackberryHome, "simulator" );
	}
	
	

}
