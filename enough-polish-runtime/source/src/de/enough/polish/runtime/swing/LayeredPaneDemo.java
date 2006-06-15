package de.enough.polish.runtime.swing;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

/* 
 * LayeredPaneDemo.java is a 1.4 application that requires
 * images/dukeWaveRed.gif. 
 */
public class LayeredPaneDemo extends JPanel
                             implements ActionListener,
                                        MouseMotionListener {
    private String[] layerStrings = { "Yellow (0)", "Magenta (1)",
                                      "Cyan (2)",   "Red (3)",
                                      "Green (4)" };
    private Color[] layerColors = { Color.yellow, Color.magenta,
                                    Color.cyan,   Color.red,
                                    Color.green };

    private JLayeredPane layeredPane;
    private JLabel dukeLabel;
    private JCheckBox onTop;
    private JComboBox layerList;

    //Action commands
    private static String ON_TOP_COMMAND = "ontop";
    private static String LAYER_COMMAND = "layer";

    //Adjustments to put Duke's toe at the cursor's tip.
    private static final int XFUDGE = 40;
    private static final int YFUDGE = 57;

    public LayeredPaneDemo()    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Create and load the duke icon.
        final ImageIcon icon = createImageIcon("images/dukeWaveRed.gif");

        //Create and set up the layered pane.
        this.layeredPane = new JLayeredPane();
        this.layeredPane.setPreferredSize(new Dimension(300, 310));
        this.layeredPane.setBorder(BorderFactory.createTitledBorder(
                                    "Move the Mouse to Move Duke"));
        this.layeredPane.addMouseMotionListener(this);

        //This is the origin of the first label added.
        Point origin = new Point(10, 20);

        //This is the offset for computing the origin for the next label.
        int offset = 35;

        //Add several overlapping, colored labels to the layered pane
        //using absolute positioning/sizing.
        for (int i = 0; i < this.layerStrings.length; i++) {
            JLabel label = createColoredLabel(this.layerStrings[i],
                                              this.layerColors[i], origin);
            this.layeredPane.add(label, new Integer(i));
            origin.x += offset;
            origin.y += offset;
        }

        //Create and add the Duke label to the layered pane.
        this.dukeLabel = new JLabel(icon);
        if (icon != null) {
            this.dukeLabel.setBounds(15, 225,
                                icon.getIconWidth(),
                                icon.getIconHeight());
        } else {
            System.err.println("Duke icon not found; using black square instead.");
            this.dukeLabel.setBounds(15, 225, 30, 30);
            this.dukeLabel.setOpaque(true);
            this.dukeLabel.setBackground(Color.BLACK);
        }
        this.layeredPane.add(this.dukeLabel, new Integer(2), 0);

        //Add control pane and layered pane to this JPanel.
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createControlPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(this.layeredPane);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = LayeredPaneDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    //Create and set up a colored label.
    private JLabel createColoredLabel(String text,
                                      Color color,
                                      Point origin) {
        JLabel label = new JLabel(text);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.black);
        label.setBorder(BorderFactory.createLineBorder(Color.black));
        label.setBounds(origin.x, origin.y, 140, 140);
        return label;
    }

    //Create the control pane for the top of the frame.
    private JPanel createControlPanel() {
        this.onTop = new JCheckBox("Top Position in Layer");
        this.onTop.setSelected(true);
        this.onTop.setActionCommand(ON_TOP_COMMAND);
        this.onTop.addActionListener(this);

        this.layerList = new JComboBox(this.layerStrings);
        this.layerList.setSelectedIndex(2);    //cyan layer
        this.layerList.setActionCommand(LAYER_COMMAND);
        this.layerList.addActionListener(this);

        JPanel controls = new JPanel();
        controls.add(this.layerList);
        controls.add(this.onTop);
        controls.setBorder(BorderFactory.createTitledBorder(
                                 "Choose Duke's Layer and Position"));
        return controls;
    }

    //Make Duke follow the cursor.
    public void mouseMoved(MouseEvent e) {
        this.dukeLabel.setLocation(e.getX()-XFUDGE, e.getY()-YFUDGE);
    }
    public void mouseDragged(MouseEvent e) {} //do nothing

    //Handle user interaction with the check box and combo box.
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (ON_TOP_COMMAND.equals(cmd)) {
            if (this.onTop.isSelected())
                this.layeredPane.moveToFront(this.dukeLabel);
            else
                this.layeredPane.moveToBack(this.dukeLabel);

        } else if (LAYER_COMMAND.equals(cmd)) {
            int position = this.onTop.isSelected() ? 0 : 1;
            this.layeredPane.setLayer(this.dukeLabel,
                                      this.layerList.getSelectedIndex(),
                                      position);
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("LayeredPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new LayeredPaneDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
