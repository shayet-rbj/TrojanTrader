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

@WebServlet("/LastTenServlet")
public class LastTenServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fieldToValidate = request.getParameter("field");
		String username = "";
		String output = "";
		int num = 1;
		if(fieldToValidate != null && fieldToValidate.equals("stockInfo")) {
			username = request.getParameter("username").strip();
		}
		PrintWriter out = response.getWriter();

		//check balance
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);

			
			PreparedStatement prep = con.prepareStatement("SELECT * FROM allTransactions WHERE username=? ORDER BY transactionID DESC LIMIT 10");
			prep.setString(1, username);
			
			ResultSet r = prep.executeQuery();
			while(r.next()) {
				String stockname = r.getString(3);
				double purchaseprice = r.getDouble(4);
				String timeOfTransaction = r.getString(5);
				int quantity = r.getInt(6);
				if(quantity > 0) {
					output += num + ") Purchased " + quantity + " share(s) of " + stockname + " for a total of $" + purchaseprice + " at " + timeOfTransaction + ".";
					++num;
				}
				else {
					output += num + ") Sold " + quantity * -1 + " share(s) of " + stockname + " for a total of $" + purchaseprice * -1 + " at " + timeOfTransaction + ".";
					++num;

				}

				output += "\n";
			}
			//System.out.println(output);
			out.print(output);
			out.flush();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}		
		
		
		
		