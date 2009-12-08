/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.enough.polish;

import de.enough.polish.notify.GrowlNotifier;
import de.enough.polish.notify.LinuxNotifier;

/**
 * A generic interface for notifications.
 * This will call the notification system that is installed.
 *
 * TODO: This should be able to be turned on and off by some config
 * TODO: config path location of bin files
 *
 * @author david
 */
public abstract class Notify {

    public static Notify instance;

    public static Notify getInstance() {
        if (instance != null) {
            return instance;
        }

        if (GrowlNotifier.isGrowlAvailable()) {
            instance = new GrowlNotifier();
        } else if (LinuxNotifier.isNotifyAvailable()) {
            instance = new LinuxNotifier();
        }
        return instance;
    }

    /**
     * Shows the message using the desired handlers
     * @param title the title
     * @param message the message
     * @return true if publish was successful, otherwise false
     */
    public static boolean publish(String title, String message) {
        Notify n = Notify.getInstance();
        if (n != null) {
            return n.publishInternal(title, message);
        }
        return false;
    }

    protected abstract boolean publishInternal(String title,String message);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage:");
            System.out.println("java de.enough.polish.notify [title] [description]");
            System.exit(1);
        }
        if (getInstance() != null) {
            getInstance().publish(args[0], args[1]);
        }
        System.exit(0);
    }
}
