import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BalanceServlet")
public class BalanceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fieldToValidate = request.getParameter("field");
		String username = "";
		double usersBalance;
		//get the current time
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		String currentTime = dtf.format(now);
		PrintWriter out = response.getWriter();
		if(fieldToValidate != null && fieldToValidate.equals("stockInfo")) {
			username = request.getParameter("username").strip();
		}
		
		//check balance
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);

			
			PreparedStatement prep = con.prepareStatement("SELECT balance FROM userInfo WHERE username=?");
			prep.setString(1, username);
			
			ResultSet r = prep.executeQuery();
			r.next();
			usersBalance = r.getDouble(1);
			String finalS = "$" + usersBalance;
			out.println(finalS);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}		
		
		
		
		