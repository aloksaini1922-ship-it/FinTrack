package in.fintrack.servlet;

import in.fintrack.util.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/transaction-action")
public class TransactionActionServlet extends HttpServlet {
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  HttpSession session=req.getSession(false);
  if(session==null||!(session.getAttribute("userId") instanceof Long)){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
  long userId=(Long)session.getAttribute("userId");
  String action=req.getParameter("action"), raw=req.getParameter("id");
  try{
   long id=Long.parseLong(raw);
   if("delete".equals(action)){
    try(Connection c=DB.getConnection();PreparedStatement ps=c.prepareStatement("DELETE FROM transactions WHERE id=? AND user_id=?")){
     ps.setLong(1,id);ps.setLong(2,userId);ps.executeUpdate();
    }
   }else{resp.sendError(400,"Unknown transaction action");return;}
   resp.sendRedirect(req.getContextPath()+"/dashboard");
  }catch(NumberFormatException ex){resp.sendError(400,"Invalid transaction id");}
  catch(SQLException ex){throw new ServletException("Unable to update transaction.",ex);}
 }
}