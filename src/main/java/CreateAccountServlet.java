import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/CreateAccountServlet")
public class CreateAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String fieldToValidate = request.getParameter("field");
		String username = "";
		String password = "";
		PrintWriter out = response.getWriter();
		
		//marking sure fields arent empty
		if(fieldToValidate != null && fieldToValidate.equals("loginInfo")) {
			//username is case sensitive
			if(request.getParameter("username") == null || request.getParameter("password") == null || request.getParameter("username").strip() == "" || request.getParameter("password".strip()) == ""){
				out.println("Signup Unsuccessful: Please enter valid credentials.");
				return;
			}
			username = request.getParameter("username").strip().toLowerCase();
			//password isnt
			password = request.getParameter("password").strip();
		}
		
		//makes sure password is above 8 characters

		//makes sure that an account does not already exist with that email
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);
			PreparedStatement prep = con.prepareStatement("SELECT COUNT(*) FROM userinfo WHERE username=?");
			prep.setString(1, username);
			ResultSet r = prep.executeQuery();
			r.next();
			if(r.getInt(1) > 0) {
				out.println("Signup Unsuccessful: A user already exists with that email.");
				return;
			}
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if(password.length() < 8) {
			out.println("Signup Unsuccessful: Password must be at least 8 characters.");
			return;
		}
		if(!username.contains("@")) {
			out.println("Signup Unsuccessful: Please enter a valid email address.");
			return;
		}
		//connecting to the sql server, change this when we move to aws

		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);
			
			PreparedStatement prep = con.prepareStatement("insert into userInfo(username, pass, balance)"
					+ " values(?, ?, ?)");
			prep.setString(1, username);
			prep.setString(2, password);
			prep.setDouble(3, 10000.0);
			int i = prep.executeUpdate();
	        if (i > 0) {
	            System.out.println("ROW INSERTED");
	        } else {
	            System.out.println("ROW NOT INSERTED");
	        }
	        con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		out.println("Signup Successful");
	}
	
}