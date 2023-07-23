/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Dell
 */
import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class transactions {
     
    public int      customerNumber;
    public String   requiredDate;
    public String   productCode;
    public int      quantityOrdered;
    public double   priceEach;
    
    public String   answer;
    public String   orderDate;
    public String   shippedDate;
    public String   status;
    public int      max_id;
    public int      orderNumber;
    public int      orderLineNumber;
    public int      quantityInStock;
    
    public transactions() {}
    
    public int orderDetailsInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();

        System.out.println("Enter Quantity of Product:");
        quantityOrdered = sc.nextInt(); 
        
        System.out.println("Enter Price Each:");
        priceEach = sc.nextDouble();
        return 1; 
    }

    public int createOrder()   {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Customer Number:");
        customerNumber = Integer.parseInt(sc.nextLine());
        
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();
            
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        orderDate = dtf.format(now); //Get date now for order date
        
        status = "In Process"; //Default status is In Process
        
        try {
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt;
            
            pstmt = conn.prepareStatement("SELECT (MAX(orderNumber)+1) AS max_id FROM orders");
            ResultSet rs = pstmt.executeQuery();   
            
            while (rs.next()) {
                max_id     = rs.getInt("max_id");
            }
            
            rs.close();
            System.out.println("Your Order Number is: " + max_id);
            orderNumber = max_id;
            
            pstmt = conn.prepareStatement ("INSERT INTO orders (orderNumber, orderDate, requiredDate, status, customerNumber) VALUES (?, ?, ?, ?, ?)");
            pstmt.setInt(1, orderNumber);
            pstmt.setString(2, orderDate);
            pstmt.setString(3, requiredDate);
            pstmt.setString(4, status);
            pstmt.setInt(5, customerNumber);
            pstmt.executeUpdate(); 
            
            do {
                orderDetailsInput();
                orderLineNumber++; 
                
                pstmt = conn.prepareStatement ("INSERT INTO orderdetails (orderNumber, productCode, quantityOrdered, priceEach, orderLineNumber) VALUES (?, ?, ?, ?, ?)");
                pstmt.setInt(1, orderNumber);
                pstmt.setString(2, productCode);
                pstmt.setInt(3, quantityOrdered);
                pstmt.setDouble(4, priceEach);
                pstmt.setInt(5, orderLineNumber);
                pstmt.executeUpdate();
                
                pstmt = conn.prepareStatement("SELECT quantityInStock FROM products WHERE productCode = ?");
                pstmt.setString(1, productCode);
                rs = pstmt.executeQuery();   
                while (rs.next()) {
                    quantityInStock = rs.getInt("quantityInStock");
                }
                rs.close();
                
                pstmt = conn.prepareStatement("UPDATE products SET quantityInStock=? WHERE productCode=?");
                pstmt.setInt(1, quantityInStock - quantityOrdered);
                pstmt.setString(2, productCode);
                pstmt.executeUpdate();

                System.out.println("Press [1] I want to buy more  [2] I'm done with my order: ");
                answer = sc.nextLine();
            } while ("1".equals(answer));
            
            if ("2".equals(answer)) {
                System.out.println("Thank you for confirming order.");
            }

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }    
    }
    
    public int cancelOrder()     {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number: ");
        orderNumber = sc.nextInt();
        
        ArrayList<String> productCodeArr = new ArrayList<>();
        ArrayList<Float> priceEachArr = new ArrayList<>();
        ArrayList<Integer> quantityOrderedArr = new ArrayList<>();
        ArrayList<Integer> quantityInStockArr = new ArrayList<>();
        
        try {
            Connection conn; 
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsales?useTimezone=true&serverTimezone=UTC&user=root&password=12345678");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
            
            // PreparedStatement pstmt = conn.prepareStatement("LOCK TABLE products WRITE/READ");
            // pstmt.executeUpdate();
            
            PreparedStatement pstmt = conn.prepareStatement("SELECT o.orderNumber, od.productCode, od.quantityOrdered, od.priceEach, o.orderDate, o.requiredDate, o.shippedDate, o.status, p.quantityInStock"
                    + "                                         FROM orders o JOIN orderdetails od ON o.orderNumber=od.orderNumber "
                    + "                                                       JOIN products p ON od.productCode=p.productCode"
                    + "                                         WHERE o.orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, orderNumber);
            
            System.out.println("Press enter key to start retrieving the data.");
            sc.nextLine();
            
            ResultSet rs = pstmt.executeQuery();   
            int i = 0;
            
            while (rs.next()) {
                orderNumber = rs.getInt("orderNumber");
                productCodeArr.add(rs.getString("productCode"));
                quantityOrderedArr.add(rs.getInt("quantityOrdered"));
                quantityInStockArr.add(rs.getInt("quantityInStock"));
                priceEachArr.add(rs.getFloat("priceEach"));
                orderDate = rs.getString("orderDate");
                requiredDate = rs.getString("requiredDate");
                shippedDate = rs.getString("shippedDate");
                status = rs.getString("status");

                i++;
            }

            rs.close();

            int j;

            if (shippedDate == null && !"Cancelled".equals(status)) {
                System.out.println("Order Number:     " + orderNumber);
                System.out.println("Order Date:       " + orderDate);
                System.out.println("Required Date:    " + requiredDate);

                System.out.println();

                for (j = 0; j < productCodeArr.size(); j++) {
                    System.out.println("Product Code:   " + productCodeArr.get(j));
                    System.out.println("Quantity:       " + quantityOrderedArr.get(j));
                    System.out.println("Price Each:     " + priceEachArr.get(j));
                    System.out.println();
                }

                sc.nextLine();

                System.out.println("Press enter key to cancel order");
                sc.nextLine();

                // Update the orders and products table

                pstmt = conn.prepareStatement("UPDATE orders SET status='Cancelled' WHERE orderNumber=?");
                pstmt.setInt(1, orderNumber);
                pstmt.executeUpdate();

                for (j = 0; j < productCodeArr.size(); j++) {
                    pstmt = conn.prepareStatement("UPDATE products SET quantityInStock=? WHERE productCode=?");
                    pstmt.setInt(1, (quantityInStockArr.get(j) + quantityOrderedArr.get(j)));
                    pstmt.setString(2, productCodeArr.get(j));
                    pstmt.executeUpdate();
                }

                System.out.println("Order has been cancelled. Press enter key to exit");
                
            } else if(shippedDate == null && "Cancelled".equals(status)){
                System.out.println("Transaction Is Already Cancelled. Press enter key to exit");
                sc.nextLine();
            } else{
                System.out.println("Cannot Cancel Transaction. Press enter key to exit");
                sc.nextLine(); 
            }
            
            pstmt.close();
            conn.commit();
            conn.close();
            return 1;
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
    
    public static void main (String args[]) {
        Scanner sc = new Scanner (System.in);
        int choice = 0;
        
        System.out.println("Enter [1] Create Order  [2] Get Product Information  [3] Get Order Information  [4] Cancel Order: ");
        choice = sc.nextInt();
        transactions t = new transactions();
        if (choice==1) t.createOrder();
        if (choice==4) t.cancelOrder();
        
        System.out.println("Thank you for transacting. Press enter key to continue....");
        sc.nextLine();
    }
    
}