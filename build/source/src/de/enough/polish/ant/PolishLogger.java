/*
 * Created on 01-Sep-2004 at 22:52:41.
 * 
 * Copyright (c) 2004-2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.ant;

import java.io.PrintStream;
import java.util.Map;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;



/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2004, 2005</p>

 * <pre>
 * history
 *        01-Sep-2004 - rob creation
 * </pre>
 * @author Robert Virkus, j2mepolish@enough.de
 */
public class PolishLogger implements BuildLogger {
	
	private BuildLogger logger;
	private boolean isInObfuscateMode;
	private boolean isInCompileMode;
	private Map classPathTranslations;
	private boolean isInternalCompilationError;


	/**
	 * Creates a new logger
	 * 
	 * @param logger the original logger
	 * @param classPathTranslations a map containing all original paths for each loaded java file.
	 * 
	 */
	public PolishLogger( BuildLogger logger, Map classPathTranslations ) {
		this.logger = logger;
		this.classPathTranslations = classPathTranslations;
		try {
			// force class loading, so that no additional build-event
			// is triggered when the messageLogged()-method
			// is invoced:
			Class.forName( "org.apache.tools.ant.BuildEvent" );
			Class.forName( "java.lang.String" );
			Class.forName( "java.lang.StringBuffer" );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new BuildException("Unable to load BuildEvent from the classpath. Please report this error to j2mepolish@enough.de stating your Ant-version.");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	 */
	public void messageLogged(BuildEvent event) {
		if (this.isInCompileMode) {
			String message = event.getMessage();
			int index;
			if (message != null && (index = message.indexOf(".java")) != -1) {
				int startIndex = message.substring(0, index).lastIndexOf("source");
				if (startIndex == -1) {
					startIndex = 0;
				} else {
					startIndex += "source/".length();
				}
				String classPath = message.substring(startIndex, index + ".java".length() );
				String originalPath = (String) this.classPathTranslations.get( classPath );
				if (originalPath != null) {
					// [javac] is needed by some IDEs like Eclipse,
					// so that it can map the source-code position
					// to the editor.
					message = "[javac] " + originalPath + message.substring( index + ".java".length() );
					event.setMessage(message, event.getPriority());
					this.isInternalCompilationError = false;
				} else {
					event.setMessage("Internal J2ME Polish class: " + message, event.getPriority() );
					this.isInternalCompilationError = true;
				}
			}
		} else if (this.isInObfuscateMode) {
			String message = event.getMessage();
			if (message != null) {
				if (message.startsWith("Note")
						|| message.startsWith("Reading")
						|| message.startsWith("Copying")
						) 
				{
					// suppress this event:
					return;
				}
			}
		}
		this.logger.messageLogged(event);
	}
	
	/**
	 * Enables or disables the obfuscate mode.
	 * 
	 * @param enable true when the obfuscation mode is enabled.
	 */
	public void setObfuscateMode(boolean enable) {
		this.isInObfuscateMode = enable;
	}

	/**
	 * Enables or disables the compile mode.
	 * 
	 * @param enable true when the compile mode is enabled.
	 */
	public void setCompileMode(boolean enable) {
		this.isInCompileMode = enable;
	}
	
	/**
	 * Determines if a compilation error occurred witin the internal J2ME Polish libraries.
	 * 
	 * @return true when a compilation error occurred witin the internal J2ME Polish libraries.
	 */
	public boolean isInternalCompilationError() {
		return this.isInternalCompilationError;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	// ONLY WRAPPER METHODS ARE AFTER THIS POINT
	///////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////
	

	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setOutputPrintStream(java.io.PrintStream)
	 */
	public void setOutputPrintStream(PrintStream out) {
		this.logger.setOutputPrintStream(out);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setMessageOutputLevel(int)
	 */
	public void setMessageOutputLevel(int level) {
		this.logger.setMessageOutputLevel(level);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setEmacsMode(boolean)
	 */
	public void setEmacsMode(boolean enabled) {
		this.logger.setEmacsMode(enabled);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildLogger#setErrorPrintStream(java.io.PrintStream)
	 */
	public void setErrorPrintStream(PrintStream out) {
		this.logger.setErrorPrintStream(out);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void buildStarted(BuildEvent event) {
		this.logger.buildStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void buildFinished(BuildEvent event) {
		this.logger.buildFinished(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void targetStarted(BuildEvent event) {
		this.logger.targetStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void targetFinished(BuildEvent event) {
		this.logger.targetFinished(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void taskStarted(BuildEvent event) {
		this.logger.taskStarted(event);
	}



	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void taskFinished(BuildEvent event) {
		this.logger.taskFinished(event);
	}


} 