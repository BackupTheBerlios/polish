package de.enough.polish.netbeans;

import de.enough.polish.ide.swing.DeviceSelector;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

public class J2mePolishProjectTemplateWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    private static final long serialVersionUID = 1L;
    
    private transient int index;
    private transient WizardDescriptor.Panel[] panels;
    private transient WizardDescriptor wiz;
    
    public J2mePolishProjectTemplateWizardIterator() {}
    
    public static J2mePolishProjectTemplateWizardIterator createIterator() {
        return new J2mePolishProjectTemplateWizardIterator();
    }
    
    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[] {
            new J2mePolishProjectTemplateWizardPanel(),
            new SelectTargetDevicesWizardPanel(),
            new SetupProjectWizardPanel()
        };
    }
    
    private String[] createSteps() {
        return new String[] {
            NbBundle.getMessage(J2mePolishProjectTemplateWizardIterator.class, "LBL_CreateProjectStep"),
            NbBundle.getMessage(J2mePolishProjectTemplateWizardIterator.class, "LBL_SelectTargetDevicesStep"),
            NbBundle.getMessage(J2mePolishProjectTemplateWizardIterator.class, "LBL_SetupProjectStep")
        };
    }
    
    public Set/*<FileObject>*/ instantiate() throws IOException {
        Set resultSet = new LinkedHashSet();
        File projectDir = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        projectDir.mkdirs();
        
        //FileObject template = Templates.getTemplate(wiz);
        FileObject dir = FileUtil.toFileObject(projectDir);
        // do not unzip the template, rather load it from J2ME Polish directly... 
        // unZipFile(template.getInputStream(), dir);
        //Map map = new HashMap
        DeviceSelector selector = (DeviceSelector) wiz.getProperty("polish.devicesselector");
        //File projectDir = (File) wiz.getProperty("projdir");
        String projectName = (String) wiz.getProperty("name");
        String polishHome =  (String) wiz.getProperty("polish.home");
        String template = (String) wiz.getProperty("polish.template");
        if (template == null) {
            template = "menu";
        }
        File templateDir = new File( polishHome, "samples/" + template );
        Map buildProperties = new HashMap();
        buildProperties.put("polish.home", polishHome );
        buildProperties.put("polish.EmulatorPlatform", wiz.getProperty("polish.EmulatorPlatform"));
        String usePolishGuiStr = (String) wiz.getProperty("polish.usePolishGui");
        if (usePolishGuiStr == null) {
            usePolishGuiStr = "true";
            System.out.println("UNABLE TO FIND polish.usePolishGui!!!!!");
        }
        buildProperties.put("polish.usePolishGui",  usePolishGuiStr );
        J2mePolishProjectGenerator.generateProjectFromTemplate(projectName, projectDir, templateDir, selector, buildProperties);
        

        
        // Always open top dir as a project:
        resultSet.add(dir);
        // Look for nested projects to open as well:
        Enumeration e = dir.getFolders(true);
        while (e.hasMoreElements()) {
            FileObject subfolder = (FileObject) e.nextElement();
            if (ProjectManager.getDefault().isProject(subfolder)) {
                resultSet.add(subfolder);
            }
        }
        
        File parent = projectDir.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }
        
        return resultSet;
    }
    
    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null);
        this.wiz.putProperty("name",null);
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)});
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[index];
    }
    
    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {}
    public final void removeChangeListener(ChangeListener l) {}
    
    private static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
                    FileLock lock = fo.lock();
                    try {
                        OutputStream out = fo.getOutputStream(lock);
                        try {
                            FileUtil.copy(str, out);
                        } finally {
                            out.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            }
        } finally {
            source.close();
        }
    }
    
}
