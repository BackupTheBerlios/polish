/*
 * Created on 29.05.2007 at 10:58:16.
 * 
 * Copyright (c) 2007 Robert Virkus / Enough Software
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
package de.enough.polish.sysinfoprovider;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * A simple servlet creating a wml page to download the sysinfoMIDlet from.
 * Writes the jad file depending on the received user agent.
 *
 * <p>Copyright Enough Software 2007</p>
 * <pre>
 * history
 *        29.05.2007 - timon creation
 * </pre>
 * @author Timon Gruetzmacher, timon@enough.de
 */
public class SysInfoProviderServlet extends HttpServlet{

	private static final long serialVersionUID = -9035665504200566555L;

	private static Logger logger = Logger.getLogger(SysInfoProviderServlet.class);

	private String buildDate = "unknown";

	public void init()
		 throws ServletException
	{
		logger.info("SysInfoProvider init()");

		String tmp = getInitParameter("build-date");

		if (tmp != null) {
			this.buildDate = tmp;
		}
	}

	private String readJad(HttpServletRequest request, boolean versionOnly)
	{
		StringBuffer sb = new StringBuffer();
		InputStream is = getClass().getResourceAsStream("/sysinfo.jad");

		if (is == null) {
			logger.error("cannot open sysinfo.jad as input stream");
			return "";
		}

		BufferedReader jadReader = null;

		try {
			String line = null;
			String jarUrl = "MIDlet-Jar-URL: " + request.getRequestURL() + "/?download=jar";
			jadReader = new BufferedReader(new InputStreamReader(is));

			while ((line = jadReader.readLine()) != null) {
				if (versionOnly && line.startsWith("MIDlet-Version:")) {
					String version = line.substring("MIDlet-Version:".length());
					version = version.trim();
					return version;
				}

				if (line.startsWith("MIDlet-Jar-URL")) {
					line = jarUrl;
				}

				sb.append(line);
				sb.append("\n");
			}
		}
		catch (IOException e) {
			logger.error("error reading sysinfo.jad file");
		}
		finally {
			CloseUtil.close(jadReader);
		}

		return sb.toString();
	}

	/**
	 * create the wml page or send the sysinfoMIDlet jad file depending on the request parameter
	 * &quot;download&quot;
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		String download = request.getParameter("download");
		String pVersion = request.getParameter("sysinfoversion");

		logger.info("request from " + request.getRemoteAddr());
		logger.info("parameter download: " + download );
		logger.info("parameter sysinfoversion: " + pVersion );

		// Send version of current sysinfo midlet.
		if ("send".equals(pVersion)) {
			// Get current version.
			String version = readJad(request, true);

			response.setContentType("text/plain");
			response.getWriter().write(version);
			response.setContentLength(version.length());
		}
		// Send JAD file.
		else if ("jad".equals(download)) {
			// Read JAD.
			String jadContent = readJad(request, false);

			// Create response text.
			StringBuffer sb = new StringBuffer(jadContent);

			// Add User-Agent to JAD as application property.
			// Too long lines in the JAD need to be wrapped for some
			// devices like Series60.
			String userAgent = request.getHeader("user-agent");

			if (userAgent != null) {
				String tmp = "user-agent: " + userAgent;

				/*
				 * Removing wrapping since it breaks most other phones, Nokia 6300, 
				 * http://www.j2mepolish.org/forum/viewtopic.php?t=1171
				 while (tmp.length() > 72) {
					sb.append(tmp.substring(0, 72));
					sb.append('\n');

					tmp = " " + tmp.substring(72);
				}*/

				sb.append(tmp);
			}

			response.setContentType("text/vnd.sun.j2me.app-descriptor");
			response.setHeader("Content-Disposition", "attachment;filename=\"sysinfo.jad\"");
			response.getWriter().write(sb.toString());
			response.setContentLength(sb.length());
		}
		// Send JAR file.
		else if ("jar".equals(download)) {
			InputStream is = getClass().getResourceAsStream("/sysinfo.jar");

			if (is == null) {
				logger.error("Could not load sysinfo.jar");
				response.sendError(500);
				return;
			}

			BufferedInputStream bis = null;
			ServletOutputStream os = null;

			try {
				// Send the jar file.
				response.setContentType("application/java-archive");
				
				bis = new BufferedInputStream(is);
				os = response.getOutputStream();
				int readBytes = 0;

				//read from the file; write to the ServletOutputStream
				while ((readBytes = bis.read()) != -1) {
					os.write(readBytes);
				}
			}
			catch (Exception e) {
				logger.error("Could not send sysinfo.jar",e);
				response.sendError(500);
			}
			finally {
				CloseUtil.close(os);
				CloseUtil.close(bis);
			}
		}
		// Send XHTML or WAP page.
		else {
			// Check if XHTML is accepted.
			boolean sendXHTML = false;
			String accept = request.getHeader("accept");

			if (accept != null && accept.contains("application/xhtml+xml")) {
				sendXHTML = true;
			}

			logger.info(sendXHTML ? "Sending XHTML..." : "Sending WML...");
		
			StringBuffer sb = new StringBuffer();
			String requestURI = request.getRequestURI();

			if (sendXHTML) {
				sb.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\n");
				sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
				sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n");
				sb.append("<head><title>SysInfoMIDlet</title></head>\n");
				sb.append("<body>\n");
				sb.append("<h1 align=\"center\">Download the SysInfoMIDlet</h1>\n");
				sb.append("<p align=\"center\">\n");
				sb.append("<form name=\"sysinfoform\" method=\"GET\" action=\"" + requestURI + "\">\n");
				sb.append("<input type=\"submit\" value=\"download here\"/>\n");
				sb.append("<input type=\"hidden\" name=\"download\" value=\"jad\" />\n");
				sb.append("</form>\n");
				sb.append("</p>\n");
				sb.append("<p align=\"center\"><small><i>last update: " + this.buildDate + "</i></small></p>\n");
				sb.append("</body>\n");
				sb.append("</html>");
				response.setContentType("application/xhtml+xml");
			}
			else {
				sb.append("<?xml version=\"1.0\"?>\n");
				//sb.append("<%@ page contentType=\"text/vnd.wap.wml\" %>");
				sb.append("<!DOCTYPE wml PUBLIC \"-//WAPFORUM//DTD WML 1.2//EN\" \"http://www.wapforum.org/DTD/wml12.dtd\">\n");
				sb.append("<wml>\n<card id=\"download\" title=\"SysInfoMIDlet\">\n");
				sb.append("<p align=\"center\">\n");
				sb.append("<anchor>\n");
				sb.append("Download the SysInfoMIDlet");
				sb.append("<go href=\"" + requestURI + "\" method=\"get\">\n");
				sb.append("<postfield name=\"download\" value=\"jad\"/>\n");
				sb.append("</go></anchor>\n</p>\n");
				sb.append("<p>\n<small><i>last update: " + this.buildDate + "</i></small>\n</p>\n");
				sb.append("</card>\n</wml>");
				response.setContentType("text/vnd.wap.wml");
			}

			response.setContentLength(sb.length());
			response.getWriter().write(sb.toString());
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		doGet(request, response);
	}
}
