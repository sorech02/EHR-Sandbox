package org.immregistries.ehr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Query;
import org.hibernate.Session;
import org.immregistries.ehr.model.Silo;
import org.immregistries.ehr.model.Tester;

public class Silos extends HttpServlet {

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
    Session dataSession = PopServlet.getDataSession();
    try {
      {
        doHeader(out, session);
        session.setAttribute("facility", null);
        Tester tester = new Tester();
        tester = (Tester) session.getAttribute("tester");
        List<Silo> siloList = null;
        Query query = dataSession.createQuery("from Silo where tester=?");
        query.setParameter(0, tester);
        siloList = query.list();
        String show = req.getParameter(PARAM_SHOW);
        out.println(
            "  <table class=\"w3-display-topleft w3-table-all w3-margin\"style=\"width:40% ;overflow:auto\"> \r\n"
            + "<thead>"
            + "<tr class=\"w3-green\">"
            + "<th> Silos</th>"
            + "</thead>"
            + "<tbody>"
            );
        if(req.getParameter("chooseSilo")!=null) {
        out.println("<div class = \" w3-margin\">"
            + "<label class=\"w3-text-red w3-margin w3-margin-bottom\"><b class=\"w3-margin\">Choose a silo</b></label><br/>"
            +"</div>"
            + "<div class = \"w3-left\">");    
        }
        for (Silo siloDisplay : siloList) {
          String link = "paramSiloId=" + siloDisplay.getSiloId();
          out.println("<br/><tr>"
              + "<td class = \"w3-hover-teal\">"     
              + "<a href=\'facility_patient_display?"+ link+"\'style = \"text-decoration:none \">\r\n"
              + "<div style=\"text-decoration:none;height:100%\">"  
              + siloDisplay.getNameDisplay()  
              + "</div>"
             + "</a>"              
              + "</td>"
             +"</tr>");
              
        }
        out.println(
             "</div>"
             + "</tbody>"
            + "</table>"
            + "  <div class=\"w3-display-right\" style=\"width=15%\">\r\n "
            + "<button onclick=\"location.href=\'silo_creation\'\"  class=\"w3-button w3-round-large w3-green w3-hover-teal\">Create new silo</button>"
            //+ "		</div>\r\n" 	
            + "</div>\r\n");
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
    out.println("<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">"
        + "<script type=\"text/javascript\" src=\"inc/Silos.js\"></script>");
    out.println("  </head>");
    out.println("  <body>");
    // out.println("<div class=\"w3-container \">");
    out.println("<header >\r\n" + "<div class=\"w3-bar w3-green w3-margin-bottom\">\r\n"
        + "  <a href = \'silos \' class=\"w3-bar-item w3-button\">List of silos </a>\r\n"
        + "  <a href = \'facility_patient_display\' class=\"w3-bar-item w3-button\">Facilities/patients list</a>\r\n"
        
        + "  <a href = \'Settings\' class=\"w3-bar-item w3-right w3-button\">Settings </a>\r\n"
        + "</div>" + "    	</header>");
    out.println("<div class=\"w3-display-container w3-margin\" style=\"height:600px;\">");
  }

  public static void doFooter(PrintWriter out, HttpSession session) {
    out.println("</div>\r\n" + "    </body>\r\n" + "</html>");
  }

}

