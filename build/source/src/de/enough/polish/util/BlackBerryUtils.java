/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.enough.polish.util;

import de.enough.polish.BuildException;
import de.enough.polish.Device;
import de.enough.polish.Environment;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 *
 * @author david
 */
public class BlackBerryUtils {

    public static final String WINDOWS_BB_HOME = "C:\\Program Files\\Research In Motion";


    /**
     * Looks for the defined blackberry home directory. If it wasn't defined
     * it tries some default locations.
     * @param env
     * @return
     */
    public static File getBBHome(Environment env) {
        //Try user locations.
        String blackberryHomeStr = env.getVariable("blackberry.emulator.home");
        if (blackberryHomeStr == null) {
            blackberryHomeStr = env.getVariable("blackberry.home");
        }
        //Try guess it windows installs it in a standard place.
        if (blackberryHomeStr == null) {
            File file = new File(WINDOWS_BB_HOME);
            if (file.exists()) {
                blackberryHomeStr = WINDOWS_BB_HOME;
            } else {
                System.err.println("Unable to start blackberry simulator: Ant property \"blackberry.home\" is not set.");
                return null;
            }
        }
        File blackberryHome = new File(blackberryHomeStr);
        if (!blackberryHome.exists()) {
            throw new BuildException("Can not find blackberry home. Please define blackberry.home or make sure it points to a valid location.");
        }
        return blackberryHome;
    }

    /**
     * Gets the best bbhome bassed on a spesific device.
     * This method searches for best possible match checking to see if the simulator was in the path
     * or via the BlackBerry platform version string.
     * @param device
     * @param env
     * @return
     */
    public static File getBBHome(Device dev, Environment env) {
        File blackberryHome = getBBHome(env);
        // search for "*JDE*" folders:
        File parent;
        if (blackberryHome.getName().indexOf("JDE") == -1) {
            parent = blackberryHome;
        } else {
            parent = blackberryHome.getParentFile();
        }

        //Look for jde's in main blackberry.home
        File[] jdes = parent.listFiles(new DirectoryFilter("JDE"));
        //Look in most highest versioned blackberry folders first
        //must use java.util.Arrays
        Arrays.sort(jdes);

        //First search for an exact matched device.
        //It was BlackBerry Device Simulators, but searching for simulators will also find this value.
        //My install of 4.2 4.7 the folder is called simulators so try match it all.
        for (int i = jdes.length - 1; i >= 0; i--) {
            File jdeFolder = jdes[i];

            File[] simulators = jdeFolder.listFiles(new DirectoryFilter("simulator"));
            for (int j = simulators.length - 1; j >= 0; j--) {
                File executable = getExecutable(simulators[j], dev, env);
                if (executable.exists()) {
                    return jdeFolder;
                }
            }
        }

        //Next
        String bbVersion = getBlackBerryOSVersion(dev);
        //Search for closest BBversion
        for (int i = jdes.length - 1; i >= 0; i--) {
            File jdeFolder = jdes[i];
            if (jdeFolder.getName().indexOf(bbVersion) >= 0) {
                return jdeFolder;
            }
        }

        System.out.println("WARNING: could not find " + bbVersion
                + " of blackberry we are now using "
                + jdes[jdes.length - 1].getAbsolutePath() + " in stead");

        //Exact version not found use last JDE
        return jdes[jdes.length - 1];
    }

    /**
     * Retrieves the executable
     *
     * @param dev the device
     * @param env the environment
     * @return the executable file, which might not exist
     */
    public static File getExecutable(File simulatorHome, Device dev, Environment env) {
        File executable = new File(simulatorHome, dev.getName() + ".bat");
        if (!executable.exists()) {
            String alternativeName = dev.getCapability("polish.Emulator.Skin");
            if (alternativeName != null) {
                executable = new File(simulatorHome, alternativeName + ".bat");
            }
        }
        return executable;
    }

    public static String getBlackBerryOSVersion(Device dev) {
        String vers = dev.getCapability("build.BlackBerry.JDE-Version");
        if (vers != null && vers.length() > 0) {
            return vers;
        }
        //Try to get what the JDE version is. This shouldn't be there. ;/
        String input = getBlackBerryOSVersion(dev.getCapability(Device.JAVA_PLATFORM));
        if (input == null) {
            throw new BuildException("BlackBerry Device doesn't contain OS version");
        }
        return input;
    }

    public static String getBlackBerryOSVersion(String input) {
        String[] inputs = StringUtil.splitAndTrim(input, ',');
        for (int i = 0; i < inputs.length; i++) {
            String platform = inputs[i].toLowerCase().trim();
            if (platform.startsWith("blackberry/")) {
                String version = platform.substring("blackberry/".length());
                return version;
            }
        }
        return null;
    }

    public static File getRapc(Device dev, Environment env) {
        File home = getBBHome(dev, env);
        File rapc = new File(home, "/bin/rapc.jar");
        if (!rapc.exists()) {
            throw new BuildException("Could not find any rapc.jar files, looking in [" + home.getAbsolutePath() + "]");
        }
        return rapc;
    }

    private static class DirectoryFilter implements FileFilter {

        private final String requiredName;

        /**
         * @param requiredName the name of the dir
         */
        public DirectoryFilter(String requiredName) {
            this.requiredName = requiredName;
        }

        public boolean accept(File file) {
            //Compare case insensivity
            return file.isDirectory()
                    && file.getName().toLowerCase().indexOf(this.requiredName.toLowerCase()) >= 0;
        }
    }
}
