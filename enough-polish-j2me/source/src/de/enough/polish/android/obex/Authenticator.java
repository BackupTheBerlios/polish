//#condition polish.android
// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Tue Mar 24 10:34:27 EET 2009
package de.enough.polish.android.obex;

/**
 * 
 * This interface provides a way to respond to authentication challenge and
 * authentication response headers.  When a client or server receives an
 * authentication challenge or authentication response header, the
 * <code>onAuthenticationChallenge()</code> or
 * <code>onAuthenticationResponse()</code> will be called, respectively, by
 * the implementation.
 * For more information on how the authentication procedure works in OBEX,
 * please review the IrOBEX specification at
 * <A HREF="http://www.irda.org/">http://www.irda.org</A>.
 * <STRONG>Authentication Challenges</STRONG>
 * When a client or server receives an authentication challenge header, the
 * <code>onAuthenticationChallenge()</code> method will be invoked by the
 * OBEX API implementation.  The application will then return the user name
 * (if needed) and password via a <code>PasswordAuthentication</code> object.
 * The password in this object is not sent in the authentication response.
 * Instead, the 16-byte challenge received in the authentication challenge is
 * combined with the password returned from the
 * <code>onAuthenticationChallenge()</code> method and passed through the MD5
 * hash algorithm.  The resulting value is sent in the authentication response
 * along with the user name if it was provided.
 * <STRONG>Authentication Responses</STRONG>
 * When a client or server receives an authentication response header, the
 * <code>onAuthenticationResponse()</code> method is invoked by the API
 * implementation with the user name received in the authentication response
 * header.  (The user name will be <code>null</code> if no user name was
 * provided in the authentication response header.)  The application must
 * determine the correct password.  This value should be returned from the
 * <code>onAuthenticationResponse()</code> method.  If the authentication
 * request should fail without the implementation checking the password,
 * <code>null</code> should
 * be returned by the application.  (This is needed for reasons like not
 * recognizing the user name, etc.)  If the returned value is not
 * <code>null</code>, the OBEX API implementation will combine the password
 * returned from the <code>onAuthenticationResponse()</code> method and
 * challenge sent via the authentication challenge, apply the MD5 hash
 * algorithm, and compare the result to the response hash received in the
 * authentication response header.  If the values are not equal, an
 * <code>IOException</code> will be thrown if the client requested
 * authentication. If the server requested authentication, the
 * <code>onAuthenticationFailure()</code> method will be called on the
 * <code>ServerRequestHandler</code> that failed authentication.  The
 * connection is <B>not</B> closed if authentication failed.
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
 * <CODE>&nbsp;<A HREF="PasswordAuthentication.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/PasswordAuthentication.html" title="class in javax.obex">PasswordAuthentication</A></CODE></FONT></TD>
 * <TD><CODE><B><A HREF="Authenticator.html#onAuthenticationChallenge(java.lang.String, boolean, boolean)" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/Authenticator.html#onAuthenticationChallenge(java.lang.String, boolean, boolean)">onAuthenticationChallenge</A></B>(java.lang.String&nbsp;description,
 * boolean&nbsp;isUserIdRequired,
 * boolean&nbsp;isFullAccess)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Called when a client or a server receives an authentication challenge
 * header.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;byte[]</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="Authenticator.html#onAuthenticationResponse(byte[])" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/Authenticator.html#onAuthenticationResponse(byte[])">onAuthenticationResponse</A></B>(byte[]&nbsp;userName)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Called when a client or server receives an authentication response
 * header.</TD>
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
 * <A NAME="onAuthenticationChallenge(java.lang.String, boolean, boolean)"><!-- --></A><H3>
 * onAuthenticationChallenge</H3>
 * <PRE>
 * <A HREF="PasswordAuthentication.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/PasswordAuthentication.html" title="class in javax.obex">PasswordAuthentication</A> <B>onAuthenticationChallenge</B>(java.lang.String&nbsp;description,
 * boolean&nbsp;isUserIdRequired,
 * boolean&nbsp;isFullAccess)</PRE>
 * <DD>Called when a client or a server receives an authentication challenge
 * header. It should respond to the challenge with a
 * <code>PasswordAuthentication</code> that contains the correct user name
 * and password for the challenge.
 * should be used; if no description is provided in the authentication
 * challenge or the description is encoded in an encoding scheme that is
 * not supported, an empty string will be provided<DD><CODE>isUserIdRequired</CODE> - <code>true</code> if the user ID is required;
 * <code>false</code> if the user ID is not required<DD><CODE>isFullAccess</CODE> - <code>true</code> if full access to the server
 * will be granted; <code>false</code> if read only access will be
 * granted
 * user name and password used for authentication</DL>
 * <HR>
 * 
 * <A NAME="onAuthenticationResponse(byte[])"><!-- --></A><H3>
 * onAuthenticationResponse</H3>
 * <PRE>
 * byte[] <B>onAuthenticationResponse</B>(byte[]&nbsp;userName)</PRE>
 * <DD>Called when a client or server receives an authentication response
 * header.  This method will provide the user name and expect the correct
 * password to be returned.
 * may be <code>null</code>
 * <code>null</code> is returned then the authentication request failed</DL>
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
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-summary.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/package-summary.html"><FONT CLASS="NavBarFont1"><B>Package</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#FFFFFF" CLASS="NavBarCell1Rev"> &nbsp;<FONT CLASS="NavBarFont1Rev"><B>Class</B></FONT>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-tree.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/package-tree.html"><FONT CLASS="NavBarFont1"><B>Tree</B></FONT></A>&nbsp;</TD>
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
 * &nbsp;PREV CLASS&nbsp;
 * &nbsp;<A HREF="ClientSession.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/ClientSession.html" title="interface in javax.obex"><B>NEXT CLASS</B></A></FONT></TD>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * <A HREF="../../index.html-javax-obex-Authenticator.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/index.html?javax/obex/Authenticator.html" target="_top"><B>FRAMES</B></A>  &nbsp;
 * &nbsp;<A HREF="Authenticator.html" tppabs="http://java.sun.com/javame/reference/apis/jsr082/javax/obex/Authenticator.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
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
 * SUMMARY:&nbsp;NESTED&nbsp;|&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_summary">METHOD</A></FONT></TD>
 * <TD VALIGN="top" CLASS="NavBarCell3"><FONT SIZE="-2">
 * DETAIL:&nbsp;FIELD&nbsp;|&nbsp;CONSTR&nbsp;|&nbsp;<A HREF="#method_detail">METHOD</A></FONT></TD>
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
public interface Authenticator
{
	/**
	 * Called when a client or a server receives an authentication challenge
	 * header. It should respond to the challenge with a
	 * <code>PasswordAuthentication</code> that contains the correct user name
	 * and password for the challenge.
	 * <P>
	 * 
	 * @param description - the description of which user name and password should be used; if no description is provided in the authentication challenge or the description is encoded in an encoding scheme that is not supported, an empty string will be provided
	 * @param isUserIdRequired - true if the user ID is required; false if the user ID is not required
	 * @param isFullAccess - true if full access to the server will be granted; false if read only access will be granted
	 * @return a PasswordAuthentication object containing the user name and password used for authentication
	 */
	PasswordAuthentication onAuthenticationChallenge(java.lang.String description, boolean isUserIdRequired, boolean isFullAccess);

	/**
	 * Called when a client or server receives an authentication response
	 * header.  This method will provide the user name and expect the correct
	 * password to be returned.
	 * <P>
	 * 
	 * @param userName - the user name provided in the authentication response; may be null
	 * @return the correct password for the user name provided; if null is returned then the authentication request failed
	 */
	byte[] onAuthenticationResponse(byte[] userName);

}
