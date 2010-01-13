//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Tue Mar 24 10:34:27 EET 2009
package de.enough.polish.android.bluetooth;

/**
 * 
 * The <code>L2CAPConnection</code> interface represents a
 * connection-oriented L2CAP channel.  This  interface is to be
 * used as part of the CLDC Generic Connection Framework.
 * To create a client connection, the protocol is <code>btl2cap</code>.
 * The target is the combination of the address
 * of the Bluetooth device to connect to and the Protocol
 * Service Multiplexor (PSM) of the service.
 * The PSM value is used by the
 * L2CAP to determine which higher level protocol or application is the
 * recipient of the messages the layer receives.
 * The parameters defined specific to L2CAP are ReceiveMTU (Maximum
 * Transmission Unit (MTU)) and TransmitMTU.  The ReceiveMTU and TransmitMTU
 * parameters are optional. ReceiveMTU
 * specifies the maximum payload size this connection can accept, and
 * TransmitMTU specifies the maximum payload size this connection can
 * send. An example of a valid L2CAP client connection string is:<BR>
 * <code>btl2cap://0050CD00321B:1003;ReceiveMTU=512;TransmitMTU=512</code>
 * 
 * <DD>1.2</DD>
 * <HR>
 * 
 * <!-- =========== FIELD SUMMARY =========== -->
 * 
 * <A NAME="field_summary"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TH ALIGN="left" COLSPAN="2"><FONT SIZE="+2">
 * <B>Field Summary</B></FONT></TH>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>static&nbsp;int</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#DEFAULT_MTU" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#DEFAULT_MTU">DEFAULT_MTU</A></B></CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Default MTU value for connection-oriented channels
 * is 672 bytes.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>static&nbsp;int</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#MINIMUM_MTU" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#MINIMUM_MTU">MINIMUM_MTU</A></B></CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Minimum MTU value for connection-oriented channels
 * is 48 bytes.</TD>
 * </TR>
 * </TABLE>
 * &nbsp;
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
 * <CODE>&nbsp;int</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#getReceiveMTU()" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#getReceiveMTU()">getReceiveMTU</A></B>()</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Returns the ReceiveMTU that the connection supports.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;int</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#getTransmitMTU()" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#getTransmitMTU()">getTransmitMTU</A></B>()</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Returns the MTU that the remote device supports.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;boolean</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#ready()" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#ready()">ready</A></B>()</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Determines if there is a packet that can be read via a call to
 * <code>receive()</code>.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;int</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#receive(byte[])" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#receive(byte[])">receive</A></B>(byte[]&nbsp;inBuf)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Reads a packet of data.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;void</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="L2CAPConnection.html#send(byte[])" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#send(byte[])">send</A></B>(byte[]&nbsp;data)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Requests that data be sent to the remote device.</TD>
 * </TR>
 * </TABLE>
 * &nbsp;
 * 
 * <!-- ============ FIELD DETAIL =========== -->
 * 
 * <A NAME="field_detail"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TH ALIGN="left" COLSPAN="1"><FONT SIZE="+2">
 * <B>Field Detail</B></FONT></TH>
 * </TR>
 * </TABLE>
 * 
 * <A NAME="DEFAULT_MTU"><!-- --></A><H3>
 * DEFAULT_MTU</H3>
 * <PRE>
 * static final int <B>DEFAULT_MTU</B></PRE>
 * <DD>Default MTU value for connection-oriented channels
 * is 672 bytes.
 * The value of <code>DEFAULT_MTU</code> is 0x02A0 (672).
 * <HR>
 * 
 * <A NAME="MINIMUM_MTU"><!-- --></A><H3>
 * MINIMUM_MTU</H3>
 * <PRE>
 * static final int <B>MINIMUM_MTU</B></PRE>
 * <DD>Minimum MTU value for connection-oriented channels
 * is 48 bytes.
 * The value of <code>MINIMUM_MTU</code> is 0x30 (48).
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
 * <A NAME="getTransmitMTU()"><!-- --></A><H3>
 * getTransmitMTU</H3>
 * <PRE>
 * int <B>getTransmitMTU</B>()
 * throws java.io.IOException</PRE>
 * <DD>Returns the MTU that the remote device supports. This value
 * is obtained after the connection has been configured. If the
 * application had specified TransmitMTU in the
 * <code>Connector.open()</code> string then this value should be equal to
 * that. If the application did
 * not specify any TransmitMTU, then this value should be  less than or
 * equal to the ReceiveMTU the remote device advertised during
 * channel configuration.
 * 
 * <code>send()</code> without losing any data
 * <DD><CODE>java.io.IOException</CODE> - if the connection is closed</DL>
 * <HR>
 * 
 * <A NAME="getReceiveMTU()"><!-- --></A><H3>
 * getReceiveMTU</H3>
 * <PRE>
 * int <B>getReceiveMTU</B>()
 * throws java.io.IOException</PRE>
 * <DD>Returns the ReceiveMTU that the connection supports. If the
 * connection string did not specify a ReceiveMTU, the value returned
 * will be less than or equal to the <code>DEFAULT_MTU</code>. Also, if
 * the connection string did specify an MTU, this value will be less than
 * or equal to the value specified in the connection string.
 * 
 * to <code>receive()</code>
 * <DD><CODE>java.io.IOException</CODE> - if the connection is closed</DL>
 * <HR>
 * 
 * <A NAME="send(byte[])"><!-- --></A><H3>
 * send</H3>
 * <PRE>
 * void <B>send</B>(byte[]&nbsp;data)
 * throws java.io.IOException</PRE>
 * <DD>Requests that data be sent to the remote device. The TransmitMTU
 * determines the amount of data that can be successfully sent in
 * a single send operation. If the size of <code>data</code> is
 * greater than the TransmitMTU, then only the first TransmitMTU bytes
 * of the packet are sent, and the rest will be discarded.  If
 * <code>data</code> is of length 0, an empty L2CAP packet will be sent.
 * <DD><CODE>java.io.IOException</CODE> - if <code>data</code> cannot be sent successfully
 * or if the connection is closed
 * <DD><CODE>java.lang.NullPointerException</CODE> - if the <code>data</code> is
 * <code>null</code></DL>
 * <HR>
 * 
 * <A NAME="receive(byte[])"><!-- --></A><H3>
 * receive</H3>
 * <PRE>
 * int <B>receive</B>(byte[]&nbsp;inBuf)
 * throws java.io.IOException</PRE>
 * <DD>Reads a packet of data. The amount of data received in
 * this operation is related to the value of ReceiveMTU.  If
 * the size of <code>inBuf</code> is greater than or equal to ReceiveMTU,
 * then no data will be lost. Unlike  <code>read()</code> on an
 * <code>java.io.InputStream</code>, if the size of <code>inBuf</code> is
 * smaller than ReceiveMTU, then the portion of the L2CAP payload that will
 * fit into <code>inBuf</code> will be placed in <code>inBuf</code>, the
 * rest will be discarded. If the application is aware of the number of
 * bytes (less than ReceiveMTU) it will receive in any transaction, then
 * the size of <code>inBuf</code> can be less than ReceiveMTU and no data
 * will be lost.  If <code>inBuf</code> is of length 0, all data sent in
 * one packet is lost unless the length of the packet is 0.
 * received; 0 if <code>inBuf</code> length is zero
 * <DD><CODE>java.io.IOException</CODE> - if an I/O error occurs or the connection has been
 * closed
 * <DD><CODE>java.io.InterruptedIOException</CODE> - if the request timed out
 * <DD><CODE>java.lang.NullPointerException</CODE> - if <code>inBuf</code>
 * is <code>null</code></DL>
 * <HR>
 * 
 * <A NAME="ready()"><!-- --></A><H3>
 * ready</H3>
 * <PRE>
 * boolean <B>ready</B>()
 * throws java.io.IOException</PRE>
 * <DD>Determines if there is a packet that can be read via a call to
 * <code>receive()</code>.  If <code>true</code>, a call to
 * <code>receive()</code> will not block the application.
 * 
 * <code>false</code> if there is no data to read
 * <DD><CODE>java.io.IOException</CODE> - if the connection is closed<DT><B>See Also:</B><DD><A HREF="L2CAPConnection.html#receive(byte[])" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html#receive(byte[])"><CODE>receive(byte[])</CODE></A></DL>
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
 * &nbsp;<A HREF="DiscoveryListener.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/DiscoveryListener.html" title="interface in javax.bluetooth"><B>PREV CLASS</B></A>&nbsp;
 * &nbsp;<A HREF="L2CAPConnectionNotifier.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnectionNotifier.html" title="interface in javax.bluetooth"><B>NEXT CLASS</B></A></FONT></TD>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * <A HREF="../../index.html-javax-bluetooth-L2CAPConnection.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/index.html?javax/bluetooth/L2CAPConnection.html" target="_top"><B>FRAMES</B></A>  &nbsp;
 * &nbsp;<A HREF="L2CAPConnection.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/bluetooth/L2CAPConnection.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
 * &nbsp;<SCRIPT type="text/javascript">
 * <!--
 * if(window==top) {
 * document.writeln('<A HREF="../../allclasses-noframe.html"tpa=http://java.sun.com/javame/reference/apis/jsr082/allclasses-noframe.html><B>All Classes</B></A>');
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
 * SUMMARY:&nbsp;NESTED&nbsp;|&nbsp;<A HREF="#field_summary">FIELD</A>&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_summary">METHOD</A></FONT></TD>
 * <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
 * DETAIL:&nbsp;<A HREF="#field_detail">FIELD</A>&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_detail">METHOD</A></FONT></TD>
 * </TR>
 * </TABLE>
 * <A NAME="skip-navbar_bottom"></A>
 * <!-- ======== END OF BOTTOM NAVBAR ======= -->
 * 
 * <HR>
 * <small>Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved. <b>Use is subject to <a href="http://java.sun.com/javame/reference/apis/license.html" target="_top">License Terms</a>.</b> Your use of this web site or any of its content or software indicates your agreement to be bound by these License Terms.<br><br>For more information, please consult the <a href="http://jcp.org/en/jsr/detail?id=82" target="_top">JSR 82 specification.</a></small>
 * </BODY>
 * <script language="JavaScript" src="../../../../../../js/omi/jsc/s_code_remote.js" tppabs="http://java.sun.com/js/omi/jsc/s_code_remote.js"></script></HTML>
 * 
 */
public interface L2CAPConnection
{
	/**
	 * Default MTU value for connection-oriented channels
	 * is 672 bytes.
	 * <P>
	 * The value of <code>DEFAULT_MTU</code> is 0x02A0 (672).
	 * <P>
	 * <DT><B>See Also:</B>
	 * 
	 */
	int DEFAULT_MTU = 0x02A0;

	/**
	 * Minimum MTU value for connection-oriented channels
	 * is 48 bytes.
	 * <P>
	 * The value of <code>MINIMUM_MTU</code> is 0x30 (48).
	 * <P>
	 * <DT><B>See Also:</B>
	 * 
	 * 
	 */
	int MINIMUM_MTU = 0x30;

	

	/**
	 * Returns the MTU that the remote device supports. This value
	 * is obtained after the connection has been configured. If the
	 * application had specified TransmitMTU in the
	 * <code>Connector.open()</code> string then this value should be equal to
	 * that. If the application did
	 * not specify any TransmitMTU, then this value should be  less than or
	 * equal to the ReceiveMTU the remote device advertised during
	 * channel configuration.
	 * <P>
	 * 
	 * 
	 * @return the maximum number of bytes that can be sent in a single call to send() without losing any data
	 * @throws java.io.IOException - if the connection is closed
	 */
	int getTransmitMTU() throws java.io.IOException;

	/**
	 * Returns the ReceiveMTU that the connection supports. If the
	 * connection string did not specify a ReceiveMTU, the value returned
	 * will be less than or equal to the <code>DEFAULT_MTU</code>. Also, if
	 * the connection string did specify an MTU, this value will be less than
	 * or equal to the value specified in the connection string.
	 * <P>
	 * 
	 * 
	 * @return the maximum number of bytes that can be read in a single call to receive()
	 * @throws java.io.IOException - if the connection is closed
	 */
	int getReceiveMTU() throws java.io.IOException;

	/**
	 * Requests that data be sent to the remote device. The TransmitMTU
	 * determines the amount of data that can be successfully sent in
	 * a single send operation. If the size of <code>data</code> is
	 * greater than the TransmitMTU, then only the first TransmitMTU bytes
	 * of the packet are sent, and the rest will be discarded.  If
	 * <code>data</code> is of length 0, an empty L2CAP packet will be sent.
	 * <P>
	 * 
	 * @param data - data to be sent
	 * @throws java.io.IOException - if data cannot be sent successfully or if the connection is closed
	 * @throws java.lang.NullPointerException - if the data is null
	 */
	void send(byte[] data) throws java.io.IOException;

	/**
	 * Reads a packet of data. The amount of data received in
	 * this operation is related to the value of ReceiveMTU.  If
	 * the size of <code>inBuf</code> is greater than or equal to ReceiveMTU,
	 * then no data will be lost. Unlike  <code>read()</code> on an
	 * <code>java.io.InputStream</code>, if the size of <code>inBuf</code> is
	 * smaller than ReceiveMTU, then the portion of the L2CAP payload that will
	 * fit into <code>inBuf</code> will be placed in <code>inBuf</code>, the
	 * rest will be discarded. If the application is aware of the number of
	 * bytes (less than ReceiveMTU) it will receive in any transaction, then
	 * the size of <code>inBuf</code> can be less than ReceiveMTU and no data
	 * will be lost.  If <code>inBuf</code> is of length 0, all data sent in
	 * one packet is lost unless the length of the packet is 0.
	 * <P>
	 * 
	 * @param inBuf - byte array to store the received data
	 * @return the actual number of bytes read; 0 if a zero length packet is received; 0 if inBuf length is zero
	 * @throws java.io.IOException - if an I/O error occurs or the connection has been closed
	 * @throws java.io.InterruptedIOException - if the request timed out
	 * @throws java.lang.NullPointerException - if inBuf is null
	 */
	int receive(byte[] inBuf) throws java.io.IOException;

	/**
	 * Determines if there is a packet that can be read via a call to
	 * <code>receive()</code>.  If <code>true</code>, a call to
	 * <code>receive()</code> will not block the application.
	 * <P>
	 * 
	 * 
	 * @return true if there is data to read; false if there is no data to read
	 * @throws java.io.IOException - if the connection is closed
	 * @see #receive(byte[])
	 */
	boolean ready() throws java.io.IOException;

}
