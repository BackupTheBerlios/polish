/*
 * Created on Jul 26, 2006 at 4:32:29 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
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
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.mepose.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IFeatureContentConsumer;
import org.eclipse.update.core.IInstallHandler;
import org.eclipse.update.core.IInstallHandlerEntry;
import org.eclipse.update.core.INonPluginEntry;
import org.eclipse.update.core.IPluginEntry;
import org.eclipse.update.core.IVerificationListener;
import org.eclipse.update.core.InstallMonitor;

import de.enough.mepose.installer.PolishInstaller;

/**
 * 
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        Jul 26, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class InstallHandler implements IInstallHandler {

    /*
     * @see org.eclipse.update.core.IInstallHandler#initialize(int, org.eclipse.update.core.IFeature, org.eclipse.update.core.IInstallHandlerEntry, org.eclipse.update.core.InstallMonitor)
     */
    public void initialize(int type, IFeature feature,
                           IInstallHandlerEntry entry, InstallMonitor monitor)
                                                                              throws CoreException {
//        System.out.println("DEBUG:InstallHandler.initialize(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#installInitiated()
     */
    public void installInitiated() throws CoreException {
//        System.out.println("DEBUG:InstallHandler.installInitiated(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#pluginsDownloaded(org.eclipse.update.core.IPluginEntry[])
     */
    public void pluginsDownloaded(IPluginEntry[] plugins) throws CoreException {
//        System.out.println("DEBUG:InstallHandler.pluginsDownloaded(...):enter.");
        throw new CoreException(null);

    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#nonPluginDataDownloaded(org.eclipse.update.core.INonPluginEntry[], org.eclipse.update.core.IVerificationListener)
     */
    public void nonPluginDataDownloaded(INonPluginEntry[] nonPluginData,
                                        IVerificationListener listener)
                                                                       throws CoreException {
        System.out.println("DEBUG:InstallHandler.nonPluginDataDownloaded(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#completeInstall(org.eclipse.update.core.IFeatureContentConsumer)
     */
    public void completeInstall(IFeatureContentConsumer consumer)
                                                                 throws CoreException {
        System.out.println("DEBUG:InstallHandler.completeInstall(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#installCompleted(boolean)
     */
    public void installCompleted(boolean success) throws CoreException {
        System.out.println("DEBUG:InstallHandler.installCompleted(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#configureInitiated()
     */
    public void configureInitiated() throws CoreException {
        System.out.println("DEBUG:InstallHandler.configureInitiated(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#completeConfigure()
     */
    public void completeConfigure() throws CoreException {
        System.out.println("DEBUG:InstallHandler.completeConfigure(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#configureCompleted(boolean)
     */
    public void configureCompleted(boolean success) throws CoreException {
        System.out.println("DEBUG:InstallHandler.configureCompleted(...):enter.");
        PolishInstaller polishInstaller = new PolishInstaller();
        polishInstaller.start();
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#unconfigureInitiated()
     */
    public void unconfigureInitiated() throws CoreException {
        System.out.println("DEBUG:InstallHandler.unconfigureInitiated(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#completeUnconfigure()
     */
    public void completeUnconfigure() throws CoreException {
        System.out.println("DEBUG:InstallHandler.completeUnconfigure(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#unconfigureCompleted(boolean)
     */
    public void unconfigureCompleted(boolean success) throws CoreException {
        System.out.println("DEBUG:InstallHandler.unconfigureCompleted(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#uninstallInitiated()
     */
    public void uninstallInitiated() throws CoreException {
        System.out.println("DEBUG:InstallHandler.uninstallInitiated(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#completeUninstall()
     */
    public void completeUninstall() throws CoreException {
        System.out.println("DEBUG:InstallHandler.completeUninstall(...):enter.");
        throw new CoreException(null);
    }

    /*
     * @see org.eclipse.update.core.IInstallHandler#uninstallCompleted(boolean)
     */
    public void uninstallCompleted(boolean success) throws CoreException {
        System.out.println("DEBUG:InstallHandler.uninstallCompleted(...):enter.");
        throw new CoreException(null);
    }

}
