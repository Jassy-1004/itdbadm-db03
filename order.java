/**
 * @author Chua, He, Matienzo, Mojica - S16
 */

import java.sql.*;
import java.util.*;

public class order {

    public int      customerNumber;
    public String   requiredDate;
    public int      orderNumber;
    public String   productCode;
    public int      productQuantity;
    public double   priceEach;
    
    public order() {}

    public int createOrder()     {
        try {
            Class.forName("com.mysql.jdbc.Driver") ;
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales", "root", "") ;
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            return 1;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static void main (String args[]) {
        Scanner scanner = new Scanner (System.in);
        int     choice  = 0;
  
        System.out.println("Enter [1] Create and Order [2] Inquire for Products [3] Retrieve Info about Order [4] Cancel Order");
        choice = scanner.nextInt();
        order o = new order();

        if (choice == 1) o.createOrder();
        
        System.out.println("Press enter key to continue....");
        scanner.nextLine();
    }
}