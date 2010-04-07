package de.enough.polish.emulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.enough.polish.Device;
import de.enough.polish.Environment;
import de.enough.polish.ant.emulator.EmulatorSetting;
import de.enough.polish.util.BlackBerryUtils;
import de.enough.polish.util.FileUtil;
import de.enough.polish.util.OsUtil;

/**
 * Invokes a specific BlackBerry simulator.
 * <pre>
 * history
 *        29-Dec-2009 - David refactored JDE detection
 *                    - Better linux support, Wine can be specified  by wine.cmd
 *        30-Dec-2009 - David Blackberry Emultors work completely in wine now.
 *        07-April-2010 - David improved BB coded by using BlackBerryUtils.
 * </pre>
 * @author Robert Virkus
 * @author David Rubin
 * TODO: filter out wine noise fixme: and error: by config
 */
public class BlackBerryEmulator extends Emulator {

    private File blackberryHome;
    private File executionDir;
    private String[] arguments;

    public boolean init(Device dev, EmulatorSetting setting,
            Environment env) {

        File executable = getEmulator(dev, env);
        if (executable != null && !executable.exists()) {
            return false;
        }
        this.executionDir = executable.getParentFile();

        ArrayList argumentsList = new ArrayList();
        if (!OsUtil.isRunningWindows()) {
            // this is a unix environment, try wine:
            //wine can NOT execute .bat files so we need to extract the relavent info.
            System.out.println("Extracting params from [" + executable + "] for use in wine.");
            try {
                String[] data = FileUtil.readTextFile(executable);
                String[] args = new String[0];
                //Only include data from the running application line.
                for (int i = 0; i < data.length; i++) {
                    if (data[i].trim().startsWith("fledge.exe")) {
                        args = data[i].split(" ");
                    }
                }
                if (args.length > 0) {
                    //
                    String wineBinary = env.getVariable("wine.cmd");
                    if (wineBinary != null && wineBinary.length() > 0) {
                        argumentsList.add(wineBinary);
                    } else {
                        argumentsList.add("wine");
                    }
                    //Should include the fledge.exe
                    for (int i = 0; i < args.length; i++) {
                        argumentsList.add(args[i]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //Blackberry don't by default support linux.
                System.err.println("Failed to extract Blackberry params");
                return false;
            }
        } else {
            argumentsList.add(executable.getAbsolutePath());
        }
        this.arguments = (String[]) argumentsList.toArray(new String[argumentsList.size()]);

        // now copy the jar, cod, alx and jad files to the simulator's home directory:
        File targetDir = this.executionDir;
        File file = new File(env.getVariable("polish.jadPath"));
        try {
            //FileUtil.copy( file, targetDir );
            //file = new File( env.getVariable("polish.jarPath") );
            //FileUtil.copy( file, targetDir );
            String baseName = file.getAbsolutePath();
            baseName = baseName.substring(0, baseName.length() - ".jar".length());
            file = new File(baseName + ".cod");
            FileUtil.copy(file, targetDir);
            file = new File(baseName + ".alx");
            FileUtil.copy(file, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to copy BlackBerry resources to the simulator directory: " + e.toString());
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
        return this.executionDir;
    }

    public File getEmulator(Device dev, Environment env) {
        blackberryHome = BlackBerryUtils.getBBHome(dev, env);
        File simHome = new File(this.blackberryHome, "simulator");
        File executable = BlackBerryUtils.getExecutable(simHome, dev, env);
        if (!executable.exists()) {
            System.err.println("Simulator not found for device was looking in [" + simHome.getAbsolutePath() + "]");
        }
        return executable;
    }
}