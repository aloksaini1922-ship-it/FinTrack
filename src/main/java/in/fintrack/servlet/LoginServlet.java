package in.fintrack.servlet;

import in.fintrack.util.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,full_name,password_hash FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && BCrypt.checkpw(password, rs.getString("password_hash"))) {
                    HttpSession old = req.getSession(false);
                    if (old != null) old.invalidate();
                    HttpSession session = req.getSession(true);
                    session.setAttribute("userId", rs.getLong("id"));
                    session.setAttribute("userName", rs.getString("full_name"));
                    session.setMaxInactiveInterval(30 * 60);
                    resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
                }
            }
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (SQLException ex) {
            throw new ServletException("Could not log in. Check database setup.", ex);
        }
    }
}