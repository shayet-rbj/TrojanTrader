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

@WebServlet("/SellServlet")
public class SellServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fieldToValidate = request.getParameter("field");
		String stock = "";
		String quantity = "";
		double price = 0.0;
		String username = "";
		Double usersBalance = 0.0;
		
		//get the current time
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm a MM/dd/yyyy");  
		LocalDateTime now = LocalDateTime.now();
		String currentTime = dtf.format(now);
		PrintWriter out = response.getWriter();
		if(fieldToValidate != null && fieldToValidate.equals("stockInfo")) {
			stock = request.getParameter("stockName").strip();
			quantity = request.getParameter("quantity").strip();
			username = request.getParameter("username").strip();
		}
		
		System.out.println("Executing " + quantity + " sale of " + stock + " for " + username);
		boolean userHasXStock = false;
		boolean userAlreadyHasStock = false;
		if(quantity.strip() == "" || quantity == " " || Integer.parseInt(quantity) <= 0) {
			out.println("Please enter a valid quantity.");
			return;
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

			PreparedStatement prep2 = con.prepareStatement("SELECT price FROM stockprices WHERE stockname=?");
			prep2.setString(1, stock);	
			ResultSet r2 = prep2.executeQuery();
			r2.next();
			price = r2.getDouble(1);
			//System.out.println("Price of " + stock + " is " + price);
			
			PreparedStatement prep3 = con.prepareStatement("SELECT COUNT(*) FROM transactionsbyuser WHERE username=? AND stockname=?");
			prep3.setString(1, username);
			prep3.setString(2, stock);
			ResultSet r3 = prep3.executeQuery();
			r3.next();
			if(r3.getInt(1) > 0) {
				userAlreadyHasStock = true;
			}
			else userAlreadyHasStock = false;

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(userAlreadyHasStock == false) {
			//user does not own the stock.
			out.println("Cannot sell " + quantity + " shares of " + stock + " as you do not own that many.");
			System.out.println("User does not own any shares of " + stock);
			return;
		}
		
		try {
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, u, p);

			
			PreparedStatement prep3 = con.prepareStatement("SELECT QUANTITY FROM transactionsbyuser WHERE username=? AND stockname=?");
			prep3.setString(1, username);
			prep3.setString(2, stock);
			ResultSet r3 = prep3.executeQuery();
			r3.next();
			System.out.println("User owns " + r3.getInt(1) + " shares of " + stock);

			if(r3.getInt(1) >= Integer.parseInt(quantity)) {
				userHasXStock = true;
				System.out.println(username + " has enough stock to sell");

			}
			else{
				userHasXStock = false;
				System.out.println(username + " does not have enough stock to sell");

			}
	        con.close();

		}
		
		
		catch(Exception e) {
			e.printStackTrace();
		}
				
		//if users has enough to purchase the stock, decrement balance, add purchase to their list, and output that the 
		//purchase was successful
		double totalPrice = 0.0;
		try {
			totalPrice = price * Integer.parseInt(quantity);	
		}
		catch(Exception e) {
			out.println("Please enter a valid quantity of stock (whole number).");
			return;
		}
		
		
		if(userHasXStock == false) {
			//if they dont have enough stock to sell, output error
			out.println("Cannot sell " + quantity + " shares of " + stock + " as you do not own that many.");
			return;
		}
		else {
			//if they do, update database and take away balance, output successs
			double newbalance = usersBalance + (totalPrice);
			String url = "jdbc:mysql://localhost:3306/trojantrader"; 
			String u = "root"; 
			String p = "root";
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection(url, u, p);
				PreparedStatement prep = con.prepareStatement("update userinfo set balance = " + newbalance + " where username = ?");
				prep.setString(1, username);
				int i = prep.executeUpdate();
				
				PreparedStatement prep4 = con.prepareStatement("update transactionsbyuser set quantity = quantity - ? where username = ? AND stockname=?");
				prep4.setString(1, quantity);
				prep4.setString(2, username);
				prep4.setString(3, stock);
				prep4.executeUpdate();
				
				PreparedStatement prep2 = con.prepareStatement("insert into allTransactions(transactionID, username, stockname, purchaseprice, timeoftransaction, quantity) values(NULL, ?, ?, ?, ?, ?);");
				prep2.setString(1, username);
				prep2.setString(2, stock);
				prep2.setDouble(3, Integer.parseInt(quantity)*price * -1);
				prep2.setString(4, currentTime);
				prep2.setInt(5, Integer.parseInt(quantity) * -1);
				prep2.executeUpdate();

				out.println("Successfully sold " + quantity + " shares of " + stock + " for $" + totalPrice);
				System.out.println("Successfully purchased stock " + username);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}		
		
		
		
		