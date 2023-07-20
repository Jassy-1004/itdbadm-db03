import java.sql.*;
import java.util.*;

public class orders {

    public String productCode;
    public String productName;
    public String productLine;
    public int quantityInStock;
    public float buyPrice;
    public float MSRP;

    public orders() {}

    public int getInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();

        try{
            Connection conn;
            conn = Driver.getConnection("");
            System.out.println("Connection Successful.");
            PreparedStatement pstmt = conn.prepareStatement("SELECT productName, productLine, quantityInStock, buyPrice, MSRP FROM products WHERE productCode=? FOR UPDATE");
            pstmt.setString(1, productCode);

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                productName = rs.getString("producttName");
                productLine = rs.getString("producttLine");
                quantityInStock = rs.getInt("quantityInStock");
                buyPrice = rs.getFloat("buyPrice");
                MSRP = rs.getFloat("MSRP");
            }

            rs.close();

            System.out.println("Product Name: " + productName);            
            System.out.println("Product Line: " + productLine);
            System.out.println("Quantity: " + quantityInStock);
            System.out.println("Buy Price: " + buyPrice);
            System.out.println("MSRP: " + MSRP);

            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.close();
            return 1;


        } catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }      
    }
    
    public int updateInfo() {
        return 1;
    }


    public static void main (String args[]) {

        try (Scanner sc = new Scanner (System.in)) {
            int choice = 0;
            // letting the user choose between the two functions
            System.out.println("Enter [1] Get Product Info  [2] Update Product");
            choice = sc.nextInt();
            orders p = new orders();
            if (choice == 1)    p.getInfo();
            if (choice == 2)    p.updateInfo();

            System.out.println("Press enter key to continue...");
            sc.nextLine();
        }



    }

}