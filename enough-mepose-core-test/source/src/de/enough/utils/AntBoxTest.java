package de.enough.utils;


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import de.enough.polish.Device;
import de.enough.polish.ant.PolishTask;

public class AntBoxTest extends TestCase{

    private final String buildxml1_basedir = "/Users/ricky/workspace/enough-polish-demo";
    private final String buildxml1_path = "build.xml";
    private final String[] buildxml1_targets = {"test","deploy","init","j2mepolish","printmessage","preprocesssource","postcompile","clean","cleanbuild"};
    private final String buildxml1_polishtarget = "j2mepolish";

    
    public void testGetTargets() {
        Set expectedKeys = new HashSet(Arrays.asList(this.buildxml1_targets));
        
        AntBox antBox = new AntBox(new File(this.buildxml1_path));
        antBox.setWorkingDirectory(this.buildxml1_basedir);
        antBox.setAlternativeClassLoader(this.getClass().getClassLoader());
        antBox.createProject();
        Map targets = antBox.getProject().getTargets();
        Set targetKeys = targets.keySet();
        Assert.assertNotNull("'targetkey' should not be null.",targetKeys);
//        for (Iterator iterator = targetKeys.iterator(); iterator.hasNext(); ) {
//            String element = (String) iterator.next();
//            System.out.println("DEBUG:AntBoxTest.testGetTargets(...):element:X"+element+"X");
//        }
        // Use only containsAll(...) instead of equals(...) because the targetkeys contain a blank key. 
        Assert.assertTrue("'expectedKeys' is not equal to 'targetKeys'",targetKeys.containsAll(expectedKeys));
    }
    
    
    public void testConfigureTarget() {
        AntBox antBox = new AntBox(new File(this.buildxml1_path));
        antBox.setWorkingDirectory(this.buildxml1_basedir);
        antBox.setAlternativeClassLoader(this.getClass().getClassLoader());
        antBox.createProject();
        Project project = antBox.getProject();
        
        Map targets = project.getTargets();
        Target target = (Target)targets.get(this.buildxml1_polishtarget);
        
        antBox.configureTarget(target);
        // The maybeConfigure method creates the concrete class and exchange it with the original UnknownElement.
        // So we need to get the tasklist again.
        target = (Target)targets.get(this.buildxml1_polishtarget);
        Task[] tasks = target.getTasks();
        Task task;
        for (int i = 0; i < tasks.length; i++) {
            task = tasks[i];
            Assert.assertTrue(task.getClass() == PolishTask.class);
        }
    }
    
    public void testGetDevices() {
        AntBox antBox = new AntBox(new File(this.buildxml1_path));
        antBox.setWorkingDirectory(this.buildxml1_basedir);
        antBox.setAlternativeClassLoader(this.getClass().getClassLoader());
        
        antBox.createProject();
        Project project = antBox.getProject();
        Target target = (Target)project.getTargets().get(this.buildxml1_polishtarget);
        antBox.configureTarget(target);
        Task[] tasks = target.getTasks();
        Task task = tasks[0];
        if( ! (task instanceof PolishTask)) {
            fail("tasks[0] is not PolishTask");
        }
        PolishTask polishTask = (PolishTask) task;
        //TODO: Change the directory.
        //polishTask.checkSettings();
        polishTask.initProject();
        polishTask.selectDevices();
        Device[] devices = polishTask.getDevices();
        Assert.assertNotNull("No devices found",devices);
//        for (int i = 0; i < devices.length; i++) {
//            System.out.println("DEBUG:AntBoxTest.testGetDevices(...):devices[i]:"+devices[i].getIdentifier());
//        }
    }
}
