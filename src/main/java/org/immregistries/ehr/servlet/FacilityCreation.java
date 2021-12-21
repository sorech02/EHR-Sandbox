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
import org.hibernate.Transaction;
import org.immregistries.ehr.model.Facility;
import org.immregistries.ehr.model.Silo;
import org.immregistries.ehr.model.Tester;


public class FacilityCreation extends HttpServlet {
  public static final String PARAM_SHOW = "show";

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(true);
    Session dataSession = PopServlet.getDataSession();

    String name = req.getParameter("facility_name");

    Silo silo = new Silo();
    silo = (Silo) session.getAttribute("silo");

    Facility facility = new Facility();
    facility.setNameDisplay(name);
    facility.setLocation(req.getParameter("location"));
    facility.setSilo(silo);
    Transaction transaction = dataSession.beginTransaction();
    dataSession.save(facility);
    transaction.commit();
    Query query = dataSession.createQuery("from Facility where nameDisplay=?");
    query.setParameter(0, name);
    List<Facility> facilityList = query.list();
    session.setAttribute("facility", facilityList.get(0));
    resp.sendRedirect("facility_patient_display");
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
        String show = req.getParameter(PARAM_SHOW);
        out.println("<form method=\"post\" class=\"w3-container\" action=\"facility_creation\">\r\n"
            + "<label class=\"w3-text-green\"><b>Facility name</b></label>"
            + "                         <input type=\"text\" class = \"w3-input w3-margin w3-border \" required value=\"\" size=\"40\" maxlength=\"60\" name=\"facility_name\"/>\r\n"
            + "   <label class=\"w3-text-green\"><b>Location</b></label>"
            + "                         <input type=\"text\"  class = \"w3-input w3-margin w3-border\" required value=\"\" size=\"40\" maxlength=\"60\" name= \"location\"/>\r\n"


            + "                <button \" class=\"w3-button w3-round-large w3-green w3-hover-teal w3-margin \"  >Validate</button>\r\n"
            + "                </form> " + "            </div>");

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
    out.println("<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\" />");
    out.println("  </head>");
    out.println("  <body>");
    out.println("    <header class=\"w3-container w3-light-grey\">");
    out.println("          <h1>Facility creation</h1>\r\n" + "     </header>");

    out.println("<div class=\"w3-display-container w3-margin \" style=\"height:200px;\">");

  }


  public static void doFooter(PrintWriter out, HttpSession session) {
    out.println("</div>\r\n" + "    </body>\r\n" + "</html>");
  }

}
