package in.fintrack.servlet;
import in.fintrack.util.DB;import jakarta.servlet.ServletException;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.math.BigDecimal;import java.sql.*;import java.time.LocalDate;import java.util.*;
@WebServlet("/goals")
public class GoalServlet extends HttpServlet{
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  Long uid=uid(req);if(uid==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}List<Map<String,Object>> list=new ArrayList<>();
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("SELECT id,title,target_amount,saved_amount,target_date FROM savings_goals WHERE user_id=? ORDER BY id DESC")){p.setLong(1,uid);try(ResultSet r=p.executeQuery()){while(r.next()){Map<String,Object> m=new HashMap<>();m.put("id",r.getLong("id"));m.put("title",r.getString("title"));m.put("target",r.getBigDecimal("target_amount"));m.put("saved",r.getBigDecimal("saved_amount"));m.put("date",r.getDate("target_date"));list.add(m);}}}catch(SQLException e){throw new ServletException(e);}
  req.setAttribute("goals",list);req.getRequestDispatcher("/goals.jsp").forward(req,resp);
 }
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  Long uid=uid(req);if(uid==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
  String action=req.getParameter("action");
  try(Connection c=DB.getConnection()){
   if("delete".equals(action)){try(PreparedStatement p=c.prepareStatement("DELETE FROM savings_goals WHERE id=? AND user_id=?")){p.setLong(1,Long.parseLong(req.getParameter("id")));p.setLong(2,uid);p.executeUpdate();}}
   else if("save".equals(action)){BigDecimal target=new BigDecimal(req.getParameter("target")),saved=new BigDecimal(req.getParameter("saved"));String title=req.getParameter("title").trim();String rawDate=req.getParameter("targetDate");if(title.isEmpty()||target.signum()<=0||saved.signum()<0||saved.compareTo(target)>0)throw new IllegalArgumentException();
    try(PreparedStatement p=c.prepareStatement("INSERT INTO savings_goals(user_id,title,target_amount,saved_amount,target_date) VALUES(?,?,?,?,?)")){p.setLong(1,uid);p.setString(2,title);p.setBigDecimal(3,target);p.setBigDecimal(4,saved);if(rawDate==null||rawDate.isBlank())p.setNull(5,Types.DATE);else p.setDate(5,java.sql.Date.valueOf(LocalDate.parse(rawDate)));p.executeUpdate();}
   }else{resp.sendError(400,"Unknown action");return;}
   resp.sendRedirect(req.getContextPath()+"/goals");
  }catch(IllegalArgumentException e){resp.sendError(400,"Please enter valid goal details.");}catch(SQLException e){throw new ServletException(e);}
 }
 private Long uid(HttpServletRequest q){HttpSession s=q.getSession(false);Object v=s==null?null:s.getAttribute("userId");return v instanceof Long?(Long)v:null;}
}