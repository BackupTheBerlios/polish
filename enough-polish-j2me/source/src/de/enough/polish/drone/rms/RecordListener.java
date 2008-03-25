// generated by de.enough.doc2java.Doc2Java (www.enough.de) on Tue Mar 25 12:43:45 CET 2008
package de.enough.polish.drone.rms;

/**
 * A listener interface for receiving Record Changed/Added/Deleted
 * events from a record store.
 * 
 * <DD>MIDP 1.0</DD>
 * <HR>
 * 
 * <!-- ======== NESTED CLASS SUMMARY ======== -->
 * 
 * 
 * <!-- =========== FIELD SUMMARY =========== -->
 * 
 * 
 * <!-- ======== CONSTRUCTOR SUMMARY ======== -->
 * 
 * 
 * <!-- ========== METHOD SUMMARY =========== -->
 * 
 * <A NAME="method_summary"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TD COLSPAN=2><FONT SIZE="+2">
 * <B>Method Summary</B></FONT></TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;void</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="RecordListener.html#recordAdded(javax.microedition.rms.RecordStore, int)" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordListener.html#recordAdded(javax.microedition.rms.RecordStore, int)">recordAdded</A></B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Called when a record has been added to a record store.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;void</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="RecordListener.html#recordChanged(javax.microedition.rms.RecordStore, int)" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordListener.html#recordChanged(javax.microedition.rms.RecordStore, int)">recordChanged</A></B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Called after a record in a record store has been changed.</TD>
 * </TR>
 * <TR BGCOLOR="white" CLASS="TableRowColor">
 * <TD ALIGN="right" VALIGN="top" WIDTH="1%"><FONT SIZE="-1">
 * <CODE>&nbsp;void</CODE></FONT></TD>
 * <TD><CODE><B><A HREF="RecordListener.html#recordDeleted(javax.microedition.rms.RecordStore, int)" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordListener.html#recordDeleted(javax.microedition.rms.RecordStore, int)">recordDeleted</A></B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</CODE>
 * 
 * <BR>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Called after a record has been deleted from a record store.</TD>
 * </TR>
 * </TABLE>
 * &nbsp;
 * 
 * <!-- ============ FIELD DETAIL =========== -->
 * 
 * 
 * <!-- ========= CONSTRUCTOR DETAIL ======== -->
 * 
 * 
 * <!-- ============ METHOD DETAIL ========== -->
 * 
 * <A NAME="method_detail"><!-- --></A>
 * <TABLE BORDER="1" WIDTH="100%" CELLPADDING="3" CELLSPACING="0" SUMMARY="">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TD COLSPAN=1><FONT SIZE="+2">
 * <B>Method Detail</B></FONT></TD>
 * </TR>
 * </TABLE>
 * 
 * <A NAME="recordAdded(javax.microedition.rms.RecordStore, int)"><!-- --></A><H3>
 * recordAdded</H3>
 * <PRE>
 * public void <B>recordAdded</B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</PRE>
 * <DD>Called when a record has been added to a record store.
 * <HR>
 * 
 * <A NAME="recordChanged(javax.microedition.rms.RecordStore, int)"><!-- --></A><H3>
 * recordChanged</H3>
 * <PRE>
 * public void <B>recordChanged</B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</PRE>
 * <DD>Called after a record in a record store has been changed. If the
 * implementation of this method retrieves the record, it will
 * receive the changed version.
 * <HR>
 * 
 * <A NAME="recordDeleted(javax.microedition.rms.RecordStore, int)"><!-- --></A><H3>
 * recordDeleted</H3>
 * <PRE>
 * public void <B>recordDeleted</B>(<A HREF="RecordStore.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordStore.html" title="class in javax.microedition.rms">RecordStore</A>&nbsp;recordStore,
 * int&nbsp;recordId)</PRE>
 * <DD>Called after a record has been deleted from a record store. If the
 * implementation of this method tries to retrieve the record
 * from the record store, an InvalidRecordIDException will be thrown.
 * <!-- ========= END OF CLASS DATA ========= -->
 * <HR>
 * 
 * 
 * <!-- ======= START OF BOTTOM NAVBAR ====== -->
 * <A NAME="navbar_bottom"><!-- --></A>
 * <A HREF="#skip-navbar_bottom" title="Skip navigation links"></A>
 * <TABLE BORDER="0" WIDTH="100%" CELLPADDING="1" CELLSPACING="0" SUMMARY="">
 * <TR>
 * <TD COLSPAN=3 BGCOLOR="#EEEEFF" CLASS="NavBarCell1">
 * <A NAME="navbar_bottom_firstrow"><!-- --></A>
 * <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="3" SUMMARY="">
 * <TR ALIGN="center" VALIGN="top">
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../../overview-summary.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/overview-summary.html"><FONT CLASS="NavBarFont1"><B>Overview</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-summary.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/package-summary.html"><FONT CLASS="NavBarFont1"><B>Package</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#FFFFFF" CLASS="NavBarCell1Rev"> &nbsp;<FONT CLASS="NavBarFont1Rev"><B>Class</B></FONT>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="class-use/RecordListener.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/class-use/RecordListener.html"><FONT CLASS="NavBarFont1"><B>Use</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="package-tree.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/package-tree.html"><FONT CLASS="NavBarFont1"><B>Tree</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../../deprecated-list.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/deprecated-list.html"><FONT CLASS="NavBarFont1"><B>Deprecated</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../../index-all.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/index-all.html"><FONT CLASS="NavBarFont1"><B>Index</B></FONT></A>&nbsp;</TD>
 * <TD BGCOLOR="#EEEEFF" CLASS="NavBarCell1">    <A HREF="../../../help-doc.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/help-doc.html"><FONT CLASS="NavBarFont1"><B>Help</B></FONT></A>&nbsp;</TD>
 * </TR>
 * </TABLE>
 * </TD>
 * <TD ALIGN="right" VALIGN="top" ROWSPAN=3><EM>
 * <strong>MID Profile</strong></EM>
 * </TD>
 * </TR>
 * 
 * <TR>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * &nbsp;<A HREF="RecordFilter.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordFilter.html" title="interface in javax.microedition.rms"><B>PREV CLASS</B></A>&nbsp;
 * &nbsp;NEXT CLASS</FONT></TD>
 * <TD BGCOLOR="white" CLASS="NavBarCell2"><FONT SIZE="-2">
 * <A HREF="../../../index.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/index.html" target="_top"><B>FRAMES</B></A>  &nbsp;
 * &nbsp;<A HREF="RecordListener.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/javax/microedition/rms/RecordListener.html" target="_top"><B>NO FRAMES</B></A>  &nbsp;
 * &nbsp;<SCRIPT type="text/javascript">
 * <!--
 * if(window==top) {
 * document.writeln('<A HREF="../../../allclasses-noframe.html"/*tpa=http://java.sun.com/javame/reference/apis/jsr118/allclasses-noframe.html><B>All Classes</B></A>');
 * }
 * //-->
 * </SCRIPT>
 * <NOSCRIPT>
 * <A HREF="../../../allclasses-noframe.html" tppabs="http://java.sun.com/javame/reference/apis/jsr118/allclasses-noframe.html"><B>All Classes</B></A>
 * </NOSCRIPT>
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
 * <small>Copyright &copy; 2006 Sun Microsystems, Inc. and Motorola, Inc. All rights reserved. <b>Use is subject to <a href="http://java.sun.com/javame/reference/apis/license.html" target="_top">License Terms</a>.</b> Your use of this web site or any of its content or software indicates your agreement to be bound by these License Terms.<br><br>For more information, please consult the <a href="http://jcp.org/en/jsr/detail?id=118" target="_top">JSR 118 specification.</a></small>
 * </BODY>
 * <script language="JavaScript" src="../../../../../../../js/omi/jsc/s_code_remote.js" tppabs="http://java.sun.com/js/omi/jsc/s_code_remote.js"></script></HTML>
 * 
 * @since 
 */
public interface RecordListener
{
	/**
	 * Called when a record has been added to a record store.
	 * <P>
	 * 
	 * @param recordStore - the RecordStore in which the record is stored
	 * @param recordId - the recordId of the record that has been added
	 */
	public void recordAdded( RecordStore recordStore, int recordId);

	/**
	 * Called after a record in a record store has been changed. If the
	 * implementation of this method retrieves the record, it will
	 * receive the changed version.
	 * <P>
	 * 
	 * @param recordStore - the RecordStore in which the record is stored
	 * @param recordId - the recordId of the record that has been changed
	 */
	public void recordChanged( RecordStore recordStore, int recordId);

	/**
	 * Called after a record has been deleted from a record store. If the
	 * implementation of this method tries to retrieve the record
	 * from the record store, an InvalidRecordIDException will be thrown.
	 * <P>
	 * 
	 * @param recordStore - the RecordStore in which the record was stored
	 * @param recordId - the recordId of the record that has been deleted
	 */
	public void recordDeleted( RecordStore recordStore, int recordId);

}
