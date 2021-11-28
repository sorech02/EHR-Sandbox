package org.immregistries.ehr.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class facility_patient_display
 */
public class facility_patient_display extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String PARAM_SHOW = "show";

	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {
	    doGet(req, resp);
	  }

	  @Override
	  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException, IOException {

	    HttpSession session = req.getSession(true);
	    resp.setContentType("text/html");
	    PrintWriter out = new PrintWriter(resp.getOutputStream());
	    try {
	      {
	        doHeader(out, session);
	        String show = req.getParameter(PARAM_SHOW);
	        out.println("<div class=\"container\">\r\n"
	        		+ "  <div class=\"toppane\">top pane</div>\r\n"
	        		+ "  <div class=\"leftpane\">\r\n"
	        		+ "  	left </div>\r\n"
	        		+ "  <div class=\"middlepane\">middle</div>\r\n"
	        		+ "  <div class=\"rightpane\">\r\n right"
	        		+       "<buttononclick=\\\"location.href=\\'http://localhost:9091/ehr-sandbox/patient_creation'\\\">create new patient</button>\r\n"
	        		+      "<buttononclick=\\\"location.href=\\'http://localhost:9091/ehr-sandbox/Silo_creation'\\\">create new silo</button>"
	        		+ "		</div>\r\n"
	        		+ "</div>");  
	        doFooter(out, session);
	      }
	    } catch (Exception e) {
	      e.printStackTrace(System.err);
	    }
	    out.flush();
	    out.close();
	  }

	  public static void doHeader(PrintWriter out, HttpSession session) {
	    out.println("<html>");
	    out.println("  <head>");
	    out.println("    <title>EHR Sandbox</title>");
	    out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"inc/facility_patient_display.css\" />");
	    out.println("  </head>");
	    out.println("  <body>");
	    out.println("    <header class=\"w3-container w3-light-grey\">");
	    out.println("<header>\r\n"
	    		+ "    		<h1>patient facility</h1>\r\n"
	    		+ "    	</header>");
	    out.println("<div id=\"formulaire\">");

	  }

	  public static void doFooter(PrintWriter out, HttpSession session) {
	    out.println("</div>\r\n"
	    		+ "    </body>\r\n"
	    		+ "</html>");
	  }


}
