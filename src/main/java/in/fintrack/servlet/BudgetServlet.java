package in.fintrack.servlet;
import in.fintrack.util.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;import java.math.BigDecimal;import java.sql.*;import java.time.LocalDate;import java.util.*;
@WebServlet("/budgets")
public class BudgetServlet extends HttpServlet{
 @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  Long uid=uid(req);if(uid==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
  List<Map<String,Object>> list=new ArrayList<>();
  try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("SELECT b.id,b.category,b.monthly_limit,b.month_start,COALESCE(SUM(t.amount),0) spent FROM budgets b LEFT JOIN transactions t ON t.user_id=b.user_id AND t.type='EXPENSE' AND t.category=b.category AND t.transaction_date>=b.month_start AND t.transaction_date<DATE_ADD(b.month_start,INTERVAL 1 MONTH) WHERE b.user_id=? GROUP BY b.id,b.category,b.monthly_limit,b.month_start ORDER BY b.month_start DESC,b.category")){
   p.setLong(1,uid);try(ResultSet r=p.executeQuery()){while(r.next()){Map<String,Object> m=new HashMap<>();m.put("id",r.getLong("id"));m.put("category",r.getString("category"));m.put("limit",r.getBigDecimal("monthly_limit"));m.put("month",r.getDate("month_start"));m.put("spent",r.getBigDecimal("spent"));list.add(m);}}
  }catch(SQLException e){throw new ServletException(e);}
  req.setAttribute("budgets",list);req.getRequestDispatcher("/budgets.jsp").forward(req,resp);
 }
 @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
  Long uid=uid(req);if(uid==null){resp.sendRedirect(req.getContextPath()+"/login.jsp");return;}
  String cat=req.getParameter("category");try{BigDecimal amount=new BigDecimal(req.getParameter("limit"));LocalDate month=LocalDate.parse(req.getParameter("month")+"-01");
   if(cat==null||cat.trim().isEmpty()||amount.signum()<=0)throw new IllegalArgumentException();
   try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO budgets(user_id,category,monthly_limit,month_start) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE monthly_limit=VALUES(monthly_limit)")){
    p.setLong(1,uid);p.setString(2,cat.trim());p.setBigDecimal(3,amount);p.setDate(4,java.sql.Date.valueOf(month));p.executeUpdate();
   }resp.sendRedirect(req.getContextPath()+"/budgets");
  }catch(IllegalArgumentException|NullPointerException e){resp.sendError(400,"Enter a valid category, amount and month.");}catch(SQLException e){throw new ServletException(e);}
 }
 private Long uid(HttpServletRequest q){HttpSession s=q.getSession(false);Object v=s==null?null:s.getAttribute("userId");return v instanceof Long?(Long)v:null;}
}