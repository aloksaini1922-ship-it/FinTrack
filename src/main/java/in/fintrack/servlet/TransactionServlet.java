package in.fintrack.servlet;

import in.fintrack.util.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/dashboard")
public class TransactionServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = userId(req);
        if (userId == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        List<Map<String,Object>> rows = new ArrayList<>();
        BigDecimal income = BigDecimal.ZERO, expenses = BigDecimal.ZERO;
        try (Connection c = DB.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT COALESCE(SUM(CASE WHEN type='INCOME' THEN amount ELSE 0 END),0) income, COALESCE(SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END),0) expenses FROM transactions WHERE user_id=?")) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { income=rs.getBigDecimal("income"); expenses=rs.getBigDecimal("expenses"); } }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT id,type,category,description,amount,transaction_date FROM transactions WHERE user_id=? ORDER BY transaction_date DESC,id DESC LIMIT 100")) {
                ps.setLong(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String,Object> row = new HashMap<>();
                        row.put("id",rs.getLong("id")); row.put("type",rs.getString("type"));
                        row.put("category",rs.getString("category")); row.put("description",rs.getString("description"));
                        row.put("amount",rs.getBigDecimal("amount")); row.put("date",rs.getDate("transaction_date"));
                        rows.add(row);
                    }
                }
            }
        } catch (SQLException ex) { throw new ServletException("Unable to load dashboard.", ex); }
        req.setAttribute("income",income); req.setAttribute("expenses",expenses);
        req.setAttribute("balance",income.subtract(expenses)); req.setAttribute("transactions",rows);
        req.getRequestDispatcher("/dashboard.jsp").forward(req,resp);
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = userId(req);
        if (userId == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        req.setCharacterEncoding("UTF-8");
        String type=req.getParameter("type"), category=trim(req.getParameter("category")), description=trim(req.getParameter("description"));
        try {
            BigDecimal amount=new BigDecimal(req.getParameter("amount"));
            LocalDate date=LocalDate.parse(req.getParameter("transactionDate"));
            if ((! "INCOME".equals(type) && ! "EXPENSE".equals(type)) || category.isBlank() || description.isBlank() || amount.signum()<=0) throw new IllegalArgumentException();
            try (Connection c=DB.getConnection(); PreparedStatement ps=c.prepareStatement("INSERT INTO transactions(user_id,type,category,description,amount,transaction_date) VALUES(?,?,?,?,?,?)")) {
                ps.setLong(1,userId); ps.setString(2,type); ps.setString(3,category); ps.setString(4,description); ps.setBigDecimal(5,amount); ps.setDate(6,Date.valueOf(date)); ps.executeUpdate();
            }
            resp.sendRedirect(req.getContextPath()+"/dashboard");
        } catch (IllegalArgumentException | NullPointerException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Please provide valid transaction details.");
        } catch (SQLException ex) { throw new ServletException("Unable to save transaction.",ex); }
    }
    private Long userId(HttpServletRequest req) { Object id=req.getSession(false)==null?null:req.getSession(false).getAttribute("userId"); return id instanceof Long?(Long)id:null; }
    private String trim(String s) { return s==null?"":s.trim(); }
}