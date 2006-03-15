package de.enough.mepose.launcher;

import org.eclipse.osgi.util.NLS;

public class MIDletLauncherMessages
{
  private static final String BUNDLE_NAME = "de.enough.mepose.launcher.MIDletLauncherMessages";

  public static String StartingEmulator;
  public static String AttachingTo;
  public static String VerifyingLaunchAttributes;
  public static String ConnectorNotSpecified;
  public static String CreatingSourceLocator;
  
  static
  {
    // Load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, MIDletLauncherMessages.class);
  }
}
