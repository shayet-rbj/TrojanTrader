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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String fieldToValidate = request.getParameter("field");
		String retrievedPassword = "";
		String username = "";
		String password = "";
		PrintWriter out = response.getWriter();
		
		if(fieldToValidate != null && fieldToValidate.equals("loginInfo")) {
			//username is case sensitive
			username = request.getParameter("username").strip().toLowerCase();
			//password isnt
			password = request.getParameter("password").strip();
		}
		
		if(!username.contains("@")) {
			out.println("Invalid Login: Please enter a valid email address.");
			return;
		}
		
		//connecting to the sql server, change this when we move to aws
		String z = "";
		boolean emailExists = false;
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);

			
			PreparedStatement prep = con.prepareStatement("SELECT pass FROM userInfo WHERE username=?");
			prep.setString(1, username);
			
			ResultSet r = prep.executeQuery();
			while(r.next()) {
				retrievedPassword += (r.getString("pass")).strip();
			}
			
			//checking if there even is a user w that email
			PreparedStatement prep2 = con.prepareStatement("SELECT COUNT(*) FROM userinfo WHERE username=?");
			prep2.setString(1, username);
			ResultSet r2 = prep2.executeQuery();
			r2.next();
			if(r2.getInt(1) > 0) {
				
				emailExists = true;
			}
			else emailExists = false;
			
			retrievedPassword.strip();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		if(password.equals(retrievedPassword)) {
			out.println("Login Successful");
			
			
		}

		else if(emailExists == false){
			out.println("Invalid Login: No account exists with that email.");
		}
		else {
			out.println("Invalid Login: Password is incorrect.");
		}
	}
	
}