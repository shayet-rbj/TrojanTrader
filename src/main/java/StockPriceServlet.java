import java.io.IOException;
import java.io.PrintWriter;

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
		PrintWriter out = response.getWriter();
		
		if(fieldToValidate != null && fieldToValidate.equals("stockInfo")) {
			stock = request.getParameter("stockName").strip().toLowerCase();
		}
		
		
		out.println("Price of " + stock + " is [implement]");
	}
}
