import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/StockPriceServlet")
public class StockPriceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fieldToValidate = request.getParameter("field");
		String stock = "";
		String username = "";
		String type = "";
		double price = 0.0;
		int sqlQuant = 0;
		PrintWriter out = response.getWriter();
		
		if(fieldToValidate != null && fieldToValidate.equals("stockInfo")) {
			stock = request.getParameter("stockName").strip().toLowerCase();
			username = request.getParameter("username").strip().toLowerCase();
			type = request.getParameter("type").strip().toLowerCase();
		}
		
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);

			if(type.equals("current")) {
				PreparedStatement prep = con.prepareStatement("SELECT price FROM stockprices WHERE stockname=?");
				prep.setString(1, stock);
			
				ResultSet r = prep.executeQuery();
				r.next();
				price = r.getDouble(1);
				String finalS = "Current Price: $" + price;
				out.println(finalS);
			}
			else if(type.equals("average")) {
				PreparedStatement prep = con.prepareStatement("SELECT purchaseprice FROM alltransactions WHERE stockname=? AND username=?");
				prep.setString(1, stock);
				prep.setString(2, username);
				
				ResultSet r = prep.executeQuery();
				
				while(r.next()) {
					double z = r.getDouble(1);
					if(z>0) {
						price += z;
					}
				}
				int q = 0;
				PreparedStatement prep2 = con.prepareStatement("SELECT quantity FROM alltransactions WHERE stockname=? AND username=?");
				prep2.setString(1, stock);
				prep2.setString(2, username);
				
				ResultSet r2 = prep2.executeQuery();
				
				while(r2.next()) {
					double z = r2.getDouble(1);
					if(z > 0){
						q+= z;
					}
				}
				
				if(q>0) price = price / q;
				else price = 0.0;
				
				String finalS = "Avg. Purchase Price: $" + price;
				out.println(finalS);				
			}
			else if(type.equals("quantity")) {
				PreparedStatement prep = con.prepareStatement("SELECT quantity FROM transactionsbyuser WHERE stockname=? AND username=?");
				prep.setString(1, stock);
				prep.setString(2, username);
				
				ResultSet r = prep.executeQuery();
				
				while(r.next()) {
					sqlQuant += r.getInt(1);
				}
				String finalS = "Owned: " + sqlQuant + " shares";
				out.println(finalS);				
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
