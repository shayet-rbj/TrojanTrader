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

@WebServlet("/BuyServlet")
public class BuyServlet extends HttpServlet {
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
		
		System.out.println("Executing " + quantity + " purchase of " + stock + " for " + username);
		if(quantity.strip() == "" || quantity == " ") {
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
	        con.close();

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(username + " has a balance of " + usersBalance);
		
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
		if(usersBalance >= totalPrice) {
			System.out.println(username + " has enough to purchase the stock. Now purchasing.");
			try {
				double newbalance = usersBalance - (totalPrice);
				String url = "jdbc:mysql://localhost:3306/trojantrader"; 
				String u = "root"; 
				String p = "root";
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection(url, u, p);
				PreparedStatement prep = con.prepareStatement("update userinfo set balance = " + newbalance + " where username = ?");
				prep.setString(1, username);
				int i = prep.executeUpdate();
				
				//insert into allTransactions table
				PreparedStatement prep2 = con.prepareStatement("insert into allTransactions(transactionID, username, stockname, purchaseprice, timeoftransaction, quantity) values(NULL, ?, ?, ?, ?, ?);");
				prep2.setString(1, username);
				prep2.setString(2, stock);
				prep2.setDouble(3, Integer.parseInt(quantity)*price);
				prep2.setString(4, currentTime);
				prep2.setInt(5, Integer.parseInt(quantity));

				int i2 = prep2.executeUpdate();
				
				//tests if a user already owns this stock
				boolean userAlreadyHasStock = false;
				PreparedStatement prep3 = con.prepareStatement("SELECT COUNT(*) FROM transactionsbyuser WHERE username=? AND stockname=?");
				prep3.setString(1, username);
				prep3.setString(2, stock);
				ResultSet r3 = prep3.executeQuery();
				r3.next();
				if(r3.getInt(1) > 0) {
					userAlreadyHasStock = true;
				}
				else userAlreadyHasStock = false;
				
				//if user already has stock in transactionsByUser, update the quantity
				if(userAlreadyHasStock) {
					System.out.println("User already owns some shares of " + stock + ". Updating their row in TransactionsByUser");
					PreparedStatement prep4 = con.prepareStatement("update transactionsbyuser set quantity = quantity + ? where username = ? AND stockname=?");
					prep4.setString(1, quantity);
					prep4.setString(2, username);
					prep4.setString(3, stock);
					prep4.executeUpdate();
				}
				//if not, insert a new row into TransactionsByUser
				else {
					System.out.println("User does not already owns some shares of " + stock + ". Creating new row in TransactionsByUser.");
					PreparedStatement prep4 = con.prepareStatement("insert into transactionsbyuser(transactionID, username, stockname, quantity) values(NULL, ?, ?, ?);");
					prep4.setString(1, username);
					prep4.setString(2, stock);
					prep4.setString(3, quantity);
					prep4.executeUpdate();
				}
				
		        out.println("Successfully purchased " + quantity + " shares of " + stock + " for $" + totalPrice);
		        con.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		//if user does NOT have enough to purchase the stock, output that the purchase was unsuccessful
		else {
			out.println("Unable to purchase " + quantity + " shares of " + stock + " for " + totalPrice + ". You only have $" + usersBalance);
			System.out.println(username + " does not have enough to purchase the stock. Unable to purchase.");
		}
	}
}
