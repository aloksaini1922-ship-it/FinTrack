package in.fintrack.servlet;

import in.fintrack.util.DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = clean(req.getParameter("name"));
        String email = clean(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        if (name.isBlank() || email.isBlank() || password == null || password.length() < 8) {
            req.setAttribute("error", "Enter your name, email, and a password of at least 8 characters.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp); return;
        }
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO users(full_name,email,password_hash) VALUES(?,?,?)")) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            ps.executeUpdate();
            resp.sendRedirect(req.getContextPath() + "/login.jsp?registered=1");
        } catch (SQLIntegrityConstraintViolationException ex) {
            req.setAttribute("error", "That email is already registered.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (SQLException ex) {
            throw new ServletException("Could not create account. Check database setup.", ex);
        }
    }
    private String clean(String value) { return value == null ? "" : value.trim(); }
}