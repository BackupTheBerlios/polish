//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Tue Mar 24 10:34:27 EET 2009
package de.enough.polish.android.bluetooth;

/**
 * 
 * The <code>L2CAPConnectionNotifier</code> interface provides
 * an L2CAP connection notifier.
 * To create a server connection, the protocol must be
 * <code>btl2cap</code>. The target contains "localhost:" and the UUID of the
 * service. The parameters are ReceiveMTU and  TransmitMTU, the same parameters
 * used to define a client connection. Here is an example of a valid server
 * connection string:<BR><code>
 * btl2cap://localhost:3B9FA89520078C303355AAA694238F07;ReceiveMTU=512;TransmitMTU=512
 * </code><BR>
 * A call to Connector.open() with this string will return a
 * <code>javax.bluetooth.L2CAPConnectionNotifier</code> object. An
 * <code>L2CAPConnection</code> object is obtained from the
 * <code>L2CAPConnectionNotifier</code> by calling the method
 * <code>acceptAndOpen()</code>.
 * 
 * <DD>1.2</DD>
 * <HR>
 * 
 * 
 * <!-- ========== METHOD SUMMARY =========== -->
 * 
 * <A NAME="method_summary"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TH ALIGN="left" COLSPAN="2"><FONT SIZE="+2">
 * <B>Method Summary</B></FONT></TH>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;<A HREF="L2CAPConnection.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html" title="interface in javax.bluetooth">L2CAPConnection</A></CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnectionNotifier.html#acceptAndOpen()" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnectionNotifier.html#acceptAndOpen()">acceptAndOpen</A></B>()</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Waits for a client to connect to this L2CAP service.</TD>
 * </TR>
 * </TABLE>
 * &nbsp;
 * 
 * <!-- ============ METHOD DETAIL ========== -->
 * 
 * <A NAME="method_detail"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TH ALIGN="left" COLSPAN="1"><FONT SIZE="+2">
 * <B>Method Detail</B></FONT></TH>
 * </TR>
 * </TABLE>
 * 
 * <A NAME="acceptAndOpen()"><!-- --></A><H3>
 * acceptAndOpen</H3>
 * <PRE>
 * <A HREF="L2CAPConnection.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html" title="interface in javax.bluetooth">L2CAPConnection</A> <B>acceptAndOpen</B>()
 * throws java.io.IOException</PRE>
 * <DD>Waits for a client to connect to this L2CAP service.
 * Upon connection returns an <code>L2CAPConnection</code>
 * that can be used to communicate with this client.
 * 
 * <P> A service record associated with this connection will be
 * added to the SDDB associated with this
 * <code>L2CAPConnectionNotifier</code> object if one does not
 * exist in the SDDB.  This method will put the
 * local device in connectable mode so that it may respond to
 * connection attempts by clients.
 * 
 * <P> The following checks are done to verify that any
 * modifications made by the application to the service record
 * after it was created by <code>Connector.open()</code> have not
 * created an invalid service record.  If any of these checks
 * fail, then a <code>ServiceRegistrationException</code> is thrown.
 * <UL>
 * <LI>ServiceClassIDList and ProtocolDescriptorList, the mandatory
 * service attributes for a <code>btl2cap</code> service record,
 * must be present in the service record.
 * <LI>L2CAP must be in the ProtocolDescriptorList.
 * <LI>The PSM value must not have changed in the service record.
 * </UL>
 * This method will not ensure that the service record created
 * is a completely valid service record. It is the responsibility
 * of the application to ensure that the service record follows
 * all of the applicable syntactic and semantic rules for service
 * record correctness.
 * 
 * <DD><CODE>java.io.IOException</CODE> - if the notifier is closed before
 * <code>acceptAndOpen()</code> is called
 * <DD><CODE><A HREF="ServiceRegistrationException.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/ServiceRegistrationException.html" title="class in javax.bluetooth">ServiceRegistrationException</A></CODE> - if the structure of the
 * associated service record is invalid or if the service record
 * could not be added successfully to the local SDDB.  The
 * structure of service record is invalid if the service
 * record is missing any mandatory service attributes, or has
 * changed any of the values described above which are fixed and
 * cannot be changed. Failures to add the record to the SDDB could
 * be due to insufficient disk space, database locks, etc.
 * <DD><CODE><A HREF="BluetoothStateException.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/BluetoothStateException.html" title="class in javax.bluetooth">BluetoothStateException</A></CODE> - if the server device could
 * not be placed in connectable mode because the device user has
 * configured the device to be non-connectable.</DL>
 * <!-- ========= END OF CLASS DATA ========= -->
 * <HR>
 * 
 * 
 * <!-- ======= START OF BOTTOM NAVBAR ====== -->
 * <A NAME="navbar_bottom"><!-- --></A>
 * <A HREF="#skip-navbar_bottom" title="Skip navigation links"></A>
 * <TABLE BORDER="0" WIDTH="100%" CELLPADDING="1" CELLSPACING="0" SUMMARY="">
 * <TR>
 * <TD COLSPAN=2 BGCOLOR="#EEEEFF" CLASS="NavBarCell1">
 * <A NAME="navbar_bottom_firstrow"><!-- --></A>
 * <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="3" SUMMARY="">
 * <TR ALIGN="center" VALIGN="top">
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../overview-summary.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/overview-summary.html"><FONT CLASS="NavBarFont1"><B>Overview</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-summary.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/package-summary.html"><FONT CLASS="NavBarFont1"><B>Package</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#FFFFFF" CLASS="NavBarCell1Rev"> &nbsp;<FONT CLASS="NavBarFont1Rev"><B>Class</B></FONT>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-tree.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/package-tree.html"><FONT CLASS="NavBarFont1"><B>Tree</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../deprecated-list.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/deprecated-list.html"><FONT CLASS="NavBarFont1"><B>Deprecated</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../index-all.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/index-all.html"><FONT CLASS="NavBarFont1"><B>Index</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../help-doc.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/help-doc.html"><FONT CLASS="NavBarFont1"><B>Help</B></FONT></A>&nbsp;</TD>
 * </TR>
 * </TABLE>
 * </TD>
 * <TD ALIGN="right" VALIGN="top" ROWSPAN=3><EM>
 * <b>JSR 82</b></EM>
 * </TD>
 * </TR>
 * 
 * <TR>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * &nbsp;<A HREF="L2CAPConnection.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html" title="interface in javax.bluetooth"><B>PREV CLASS</B></A>&nbsp;
 * &nbsp;<A HREF="LocalDevice.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/LocalDevice.html" title="class in javax.bluetooth"><B>NEXT CLASS</B></A></FONT></TD>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * <A HREF="../../index.html-javax-bluetooth-L2CAPConnectionNotifier.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/index.html?javax/bluetooth/L2CAPConnectionNotifier.html" target="_top"><B>FRAMES</B></A>  &nbsp;
 * &nbsp;<A HREF="L2CAPConnectionNotifier.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnectionNotifier.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
 * &nbsp;<SCRIPT type="text/javascript">
 * <!--
 * if(window==top) {
 * document.writeln('<A HREF="../../allclasses-noframe.html"/tpa=http://java.sun.com/javame/reference/apis/jsr082/allclasses-noframe.html/><B>All Classes</B></A>');
 * }
 * //-->
 * </SCRIPT>
 * <NOSCRIPT>
 * <A HREF="../../allclasses-noframe.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/allclasses-noframe.html"><B>All Classes</B></A>
 * </NOSCRIPT>
 * 
 * 
 * </FONT></TD>
 * </TR>
 * <TR>
 * <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
 * SUMMARY:&nbsp;NESTED&nbsp;|&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_summary">METHOD</A></FONT></TD>
 * <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
 * DETAIL:&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_detail">METHOD</A></FONT></TD>
 * </TR>
 * </TABLE>
 * <A NAME="skip-navbar_bottom"></A>
 * <!-- ======== END OF BOTTOM NAVBAR ======= -->
 * 
 * <HR>
 * <small>Copyright � 2006 Sun Microsystems, Inc. All rights reserved. <b>Use is subject to <a href="http://java.sun.com/javame/reference/apis/license.html" target="_top">License Terms</a>.</b> Your use of this web site or any of its content or software indicates your agreement to be bound by these License Terms.<br><br>For more information, please consult the <a href="http://jcp.org/en/jsr/detail?id=82" target="_top">JSR 82 specification.</a></small>
 * </BODY>
 * <script language="JavaScript" src="../../../../../../js/omi/jsc/s_code_remote.js" tppabs="http://java.sun.com/js/omi/jsc/s_code_remote.js"></script></HTML>
 * 
 */
public interface L2CAPConnectionNotifier
{
	/**
	 * Waits for a client to connect to this L2CAP service.
	 * Upon connection returns an <code>L2CAPConnection</code>
	 * that can be used to communicate with this client.
	 * 
	 * <P> A service record associated with this connection will be
	 * added to the SDDB associated with this
	 * <code>L2CAPConnectionNotifier</code> object if one does not
	 * exist in the SDDB.  This method will put the
	 * local device in connectable mode so that it may respond to
	 * connection attempts by clients.
	 * 
	 * <P> The following checks are done to verify that any
	 * modifications made by the application to the service record
	 * after it was created by <code>Connector.open()</code> have not
	 * created an invalid service record.  If any of these checks
	 * fail, then a <code>ServiceRegistrationException</code> is thrown.
	 * <UL>
	 * <LI>ServiceClassIDList and ProtocolDescriptorList, the mandatory
	 * service attributes for a <code>btl2cap</code> service record,
	 * must be present in the service record.
	 * <LI>L2CAP must be in the ProtocolDescriptorList.
	 * <LI>The PSM value must not have changed in the service record.
	 * </UL>
	 * <P>
	 * This method will not ensure that the service record created
	 * is a completely valid service record. It is the responsibility
	 * of the application to ensure that the service record follows
	 * all of the applicable syntactic and semantic rules for service
	 * record correctness.
	 * <P>
	 * 
	 * 
	 * @return a connection to communicate with the client
	 * @throws java.io.IOException - if the notifier is closed before acceptAndOpen() is called
	 * @throws ServiceRegistrationException - if the structure of the associated service record is invalid or if the service record could not be added successfully to the local SDDB.  The structure of service record is invalid if the service record is missing any mandatory service attributes, or has changed any of the values described above which are fixed and cannot be changed. Failures to add the record to the SDDB could be due to insufficient disk space, database locks, etc.
	 * @throws BluetoothStateException - if the server device could not be placed in connectable mode because the device user has configured the device to be non-connectable.
	 */
	L2CAPConnection acceptAndOpen() throws java.io.IOException;

}
