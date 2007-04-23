package de.enough.polish.plugin.netbeans.project;

import org.netbeans.api.project.Project;
import org.netbeans.modules.vmd.api.io.DataObjectContext;
import org.netbeans.modules.vmd.api.io.ProjectUtils;
import org.netbeans.modules.vmd.api.io.javame.MidpProjectPropertiesSupport;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;

import java.awt.*;

/**
 * @author David Kaspar
 */
public class PolishProjectSupport {

    public static final String PROP_USE_POLISH_PROJECT = "polish.project.useJ2MEPolish"; // NOI18N

    // HINT - this represents diferrent property from one returns in MidpProjectPropertiesSupport.getDeviceScreenSizeFromProject
    public static Dimension getDeviceScreenSizeFromProject (DataObjectContext context) {
        return parseDimension (evaluateProjectConfigurationProperty (context, "polish.ScreenSize")); // NOI18N
    }

    public static Dimension getDeviceCanvasSizeFromProject (DataObjectContext context) {
        return parseDimension (evaluateProjectConfigurationProperty (context, "polish.CanvasSize")); // NOI18N
    }

    public static Dimension getDeviceFullCanvasSizeFromProject (DataObjectContext context) {
        return parseDimension (evaluateProjectConfigurationProperty (context, "polish.FullCanvasSize")); // NOI18N
    }

    public static Integer getFontHeightSmall (DataObjectContext context) {
        return parseInteger (evaluateProjectConfigurationProperty (context, "polish.Font.small")); // NOI18N
    }

    public static Integer getFontHeightMedium (DataObjectContext context) {
        return parseInteger (evaluateProjectConfigurationProperty (context, "polish.Font.medium")); // NOI18N
    }

    public static Integer getFontHeightLarge (DataObjectContext context) {
        return parseInteger (evaluateProjectConfigurationProperty (context, "polish.Font.large")); // NOI18N
    }

    public static boolean parseBoolean (String s) {
        return "true".equalsIgnoreCase (s)  ||  "yes".equalsIgnoreCase (s);
    }

    public static Integer parseInteger (String s) {
        if (s != null) {
            try {
                return Integer.parseInt (s);
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    public static Dimension parseDimension (String s) {
        if (s == null)
            return null;
        int i = s.indexOf ('x');
        if (i < 0)
            return null;
        try {
            int width = Integer.parseInt (s.substring (0, i));
            int height = Integer.parseInt (s.substring (i + 1));
            return new Dimension (width, height);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String evaluateProjectConfigurationProperty (DataObjectContext context, String property) {
        Project project = ProjectUtils.getProject (context);
        return evaluateProjectConfigurationProperty (project, property);
    }

    private static String evaluateProjectConfigurationProperty (Project project, String property) {
        return evaluateProjectProperty (project, property, MidpProjectPropertiesSupport.getActiveConfiguration (project));
    }

    private static String evaluateProjectProperty (Project project, String property, String activeConfiguration) {
        AntProjectHelper helper = project.getLookup ().lookup (AntProjectHelper.class);
        EditableProperties ep = helper.getProperties (AntProjectHelper.PROJECT_PROPERTIES_PATH);
        return MidpProjectPropertiesSupport.evaluateProperty (ep, property, activeConfiguration);
    }

    public static boolean isPolishProject (Project project) {
        return parseBoolean (evaluateProjectProperty (project, PROP_USE_POLISH_PROJECT, null)); // NOI18N
    }

}
