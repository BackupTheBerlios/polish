package de.enough.polish.netbeans.convert;

import de.enough.polish.plugin.netbeans.project.PolishProjectSupport;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public final class ConvertVisualPanel1 extends JPanel {

    private ConvertWizardPanel1 wizard;

    /** Creates new form ConvertVisualPanel1 */
    public ConvertVisualPanel1(ConvertWizardPanel1 wizard) {
        this.wizard = wizard;
        initComponents();
        combo.setRenderer (new ProjectListCellRenderer ());
    }
    
    public String getName() {
        return "Select Project To Convert";
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        combo = new javax.swing.JComboBox();
        convertType = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Project:");

        combo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, convertType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, combo, 0, 319, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(combo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(convertType)
                .addContainerGap(258, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void comboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboActionPerformed
    Project project = getSelectedProject ();
    convertType.setText (project != null ? PolishProjectSupport.isPolishProject (project)
        ? "Converting from J2ME Polish Project to Mobile Project"
        : "Converting from Mobile Project to J2ME Polish Project"
    : "");
    wizard.fireChangeEvent();
}//GEN-LAST:event_comboActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox combo;
    private javax.swing.JLabel convertType;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    Project getSelectedProject () {
        return (Project) combo.getSelectedItem ();
    }

    public void loadProjects (Project project) {
        Project[] projects = OpenProjects.getDefault ().getOpenProjects ();
        Vector projectsVector = new Vector ();
        for (Project prj : projects)
            if (MidpProjectPropertiesSupport.isMobileProject (prj))
                projectsVector.add (prj);
        combo.setModel (new DefaultComboBoxModel (projectsVector));
        if (project == null) {
            Project prj = OpenProjects.getDefault ().getMainProject ();
            if (MidpProjectPropertiesSupport.isMobileProject (prj))
                project = prj;
        }
        if (project == null  &&  projects.length > 0)
            project = projects[0];
        combo.setSelectedItem (project);
    }

    public static class ProjectListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null)
                return super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
            ProjectInformation info = ((Project) value).getLookup ().lookup (ProjectInformation.class);
            super.getListCellRendererComponent (list, info != null ? info.getDisplayName () : null, index, isSelected, cellHasFocus);
            if (info != null)
                setIcon (info.getIcon ());
            return this;
        }

    }

}
