/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package project;
import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author yuan
*/
public class Project {   
    Connection conn = null;
    Statement stmt = null;
    // User Login
    public String userLogin(Connection conn) throws SQLException{
          Scanner input = new Scanner(System.in);
          try{  

                System.out.println("what's your userID?");
                String user = input.next();
                System.out.println("what's your password?");
                String pass = input.next();
                String findUser = "select * from customer where userID =?";
                PreparedStatement pmst = conn.prepareStatement(findUser);                          
                pmst.setString(1, user);
                ResultSet r = pmst.executeQuery();
                System.out.println("Matching Record....");
                while (r.next()){
                    String userID = r.getString("userID");
                    String cPassword = r.getString("cPassword");
                    System.out.println(userID+ " " + cPassword);
                    if (pass.equals(cPassword)){
                        pmst.close();
                        return userID;                       
                    }
                    else{
                        pmst.close();
                        System.out.print("Wrong password");
                    }
                }                 
            
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Got an exception!");
                
            }                 
          return "Login Fail!";
    }
    // staff login
     public String staffLogin(Connection conn) throws SQLException{
          Scanner input = new Scanner(System.in);
          try{  

                System.out.println("what's your userID?");
                String user = input.next();
                System.out.println("what's your password?");
                String pass = input.next();
                String findUser = "select * from staff where staffID =?";
                PreparedStatement pmst = conn.prepareStatement(findUser);                          
                pmst.setString(1, user);
                ResultSet r = pmst.executeQuery();
                System.out.println("Matching Record....");
                while (r.next()){
                    String userID = r.getString("staffID");
                    String cPassword = r.getString("sPassword");
                    System.out.println(userID+ " " + cPassword);
                    if (pass.equals(cPassword)){
                        pmst.close();
                        return userID;                       
                    }
                    else{
                        pmst.close();
                        System.out.print("Wrong password");
                    }
                }                 
            
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Got an exception!");
                
            }                 
          return "Login Fail!";
    }
    // create a new account for new  customer; Update customer table and address table
    public String createAccount(Connection conn) throws SQLException, ClassNotFoundException{
         Scanner input = new Scanner(System.in);
         System.out.println("Create a new Account:");
         System.out.println("UserID:");         
         String userID = input.next();
         System.out.println("New Password:");
         String cPassword = input.next();
         System.out.println("Name:");
         String cName = input.next();
         
         //check whether userID exists in record
        try(PreparedStatement pstmt = conn.prepareStatement("select count(1) as count from customer where userID = ?")){
             pstmt.setString(1, userID);
             ResultSet rs = pstmt.executeQuery();
             while(rs.next()){
                 int count = rs.getInt("count");
                 System.out.println(count);
                 if (count==0){
                     System.out.println("Creating Account...");
                 }else{
                     System.out.println("This userId has been registed");
                     return "Another UserID";
                 }
             }
           
         }        
         try(PreparedStatement pStmt = conn.prepareStatement("insert into customer values(?,?,?,?)")){                            
            //update customer table           
            pStmt.setString(1, userID);       
            pStmt.setString(2, cPassword);
            pStmt.setString(3, cName);
            pStmt.setDouble(4,0);
            pStmt.executeUpdate();
            pStmt.close();
            
            //update address table
            System.out.println("We will  need your address info as well...");
            this.addAddress(conn, userID);
            pStmt.close();
            
         }catch (Exception e) {
            e.printStackTrace();
            System.err.println("Got an exception!");
         }
        
        return userID;       

    }
    
    public void modifyPerson(Connection conn, String userID) throws SQLException, ClassNotFoundException{
        Scanner input = new Scanner(System.in);
        String modify;
        System.out.println("What do you want to modify? A. add address; B. add credit card; C. delete address; D delete credit card; E. display credit card; F. get address; G. modify address; H. modify credit card; Q. Quit");
        modify = input.next();
        while(!modify.equals("Quit")){
            switch(modify){
                case "A": this.addAddress(conn, userID);
                          break;
                case "B": this.addCredit(conn, userID);
                          break;
                case "C": this.deleteAddress(conn, userID);
                          break;
                case "D": this.deleteCredit(userID, conn);
                          break;
                case "E": this.displayCredit(conn, userID);
                          break;
                case "F": this.getAddress(conn, userID);
                          break;
                case "G": this.modifyAddress(conn, userID, 0);
                          break;
                case "H": this.modifyCredit(conn, modify, userID);
                          break;
                case "Q":  break;   
                
            }
            System.out.println("What else do you want to modify?");
            modify = input.next();
        }
    }
    // insert customer creditCard Information;
    public String addCredit(Connection conn, String cOwner) throws ClassNotFoundException, SQLException{
        Scanner input = new Scanner(System.in);
        System.out.println("cardNO?");
        String cardNo = input.next();
        System.out.println("card address?");
        int cAddress=this.addAddress(conn, cOwner);
        System.out.println("card limit?");
        double limit = input.nextDouble();
        try(PreparedStatement stmt = conn.prepareStatement("insert into creditCard values(?,?,?,?)")){
                stmt.setString(1,cardNo);
                stmt.setString(2, cOwner);
                stmt.setInt(3, cAddress);
                stmt.setDouble(4, limit);   
                stmt.execute();
        }catch(Exception e) {
            e.printStackTrace();
            System.err.println("Got an exception!");
        }
        return cardNo;
    }
    
    // delete customer creditCard Information
    public void deleteCredit(String userID, Connection conn) throws SQLException{
        Scanner input = new Scanner(System.in);
        String query = "delete from creditCard where cardNo = ? ";
        System.out.println("Which cardNo do you want to delete");
        String cardNo = input.next();
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, cardNo);
        pstmt.executeQuery();
    }
    
    // modify customer creditCard Information
    public void modifyCredit(Connection conn, String cardNo, String cOwner) throws SQLException{
        Scanner input = new Scanner(System.in);
        System.out.println("Here is your current card");
        displayCredit(conn, cOwner);
        System.out.println("Which card would you like to update");
        cardNo = input.next();
        System.out.println("Your current address are here, choose one please");
        getAddress(conn, cOwner);
        int addrID = 0;
        int chooseAddress = input.nextInt();
        addrID = chooseAddress;
        System.out.println("Input your limit please");
        double limit = input.nextDouble();
        String query ="update creditcard set cOwner =?, cAddress =?, cLimit=? where cardNo =?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, cOwner);
        pstmt.setInt(2, addrID);
        pstmt.setDouble(3,limit);
        pstmt.setString(4, cardNo);
        pstmt.execute();
        //update credit card address
        System.out.println("Update Successful!");
        pstmt.close();
    }
    
    // delete address
    public void deleteAddress(Connection conn, String sOwner) throws SQLException{
        Scanner input = new Scanner(System.in);
        System.out.println("which address do you want to delete");
        this.getAddress(conn, sOwner);
        int addrID = input.nextInt();

        String query = "delete address where addrID =?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, addrID);
        pstmt.execute();
        pstmt.close();
    }
    
    // modify address
    public void modifyAddress(Connection conn, String userID, int addrID) throws SQLException{
         Scanner input = new Scanner(System.in);   
         System.out.println("Which address ID would you like to update");
         addrID = input.nextInt();
         System.out.println("strNo");
         while(!input.hasNextInt()) {
             input.next();
             System.out.println("integer please");
         }   
         int strNo = input.nextInt();
         System.out.println("strName");
         String strName = input.next();
         System.out.println("city");
         String city = input.next();
         System.out.println("state:");
         String aState = input.next();
         System.out.println("zip");
         String zip = input.next();
         
        String query = "update address set strNo =?, strName =?, city =?, aState=?, zip =? where addrID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, strNo);
        pstmt.setString(2, strName);
        pstmt.setString(3, city);
        pstmt.setString(4, aState);
        pstmt.setString(5, zip);
        pstmt.setInt(6, addrID);
        pstmt.execute();
        pstmt.close();
    }
    private void getAddress(Connection conn, String sOwner) throws SQLException {
         //To change body of generated methods, choose Tools | Templates.
         PreparedStatement pstmt = conn.prepareStatement("select * from address where userID = ?");
         pstmt.setString(1, sOwner);
         ResultSet rs = pstmt.executeQuery();
         while(rs.next()){
            System.out.print(rs.getString("addrID")+' ');
            System.out.print(rs.getString("strNo")+' ');
            System.out.print(rs.getString("strName")+' ');
            System.out.print(rs.getString("city")+' ');
            System.out.print(rs.getString("aState")+' ');
            System.out.println(rs.getString("zip")+' ');
         }
    }
    // add address
    public int addAddress(Connection conn, String userID)throws SQLException{
         Scanner input = new Scanner(System.in);   
         System.out.println("strNo");
         while(!input.hasNextInt()) {
             input.next();
             System.out.println("integer please");
         }   
         int strNo = input.nextInt();
         System.out.println("strName");
         String strName = input.next();
         System.out.println("city");
         String city = input.next();
         System.out.println("state");
         String aState = input.next();
         System.out.println("zip");
         String zip = input.next();
      
         // create an address ID
        String queryA = "SELECT COALESCE(MAX(Address.addrID), 0) as addrID FROM Address";
        PreparedStatement Ostmt = conn.prepareStatement(queryA);
        ResultSet rs = Ostmt.executeQuery(queryA);
        int newaID=1;
        if (rs.next()){
            newaID = rs.getInt("addrID")+1;
            if (rs.wasNull()){
                newaID =1;
            }
        }
        System.out.println(newaID);
        try(PreparedStatement stmt = conn.prepareStatement("insert into address values(?,?,?,?,?,?,?)")){
            stmt.setInt(1, newaID);
            stmt.setString(2, userID);
            stmt.setInt(3, strNo);
            stmt.setString(4,strName);
            stmt.setString(5, city);
            stmt.setString(6, aState);
            stmt.setString(7, zip);
            stmt.executeUpdate();
            stmt.close();
        
        }
        return newaID;
    }       
    
    public double price(Connection conn, String pName, String State) throws SQLException{
        String Query = "select price from product where pname = ? and pstate = ?";
        PreparedStatement pstmt = conn.prepareStatement(Query);
        pstmt.setString(1,pName);
        pstmt.setString(2,State);
        ResultSet rs = pstmt.executeQuery();
        double price=0;
        while(rs.next()){
             price = rs.getDouble("price");
        }
        
           pstmt.close();
        return price;
      
    }
    
    // add products to the shopping cart table
    public void addShoppingCart(Connection conn,String sOwner, int home) throws SQLException{
        //get product unit price
        double price = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Buy or Quit to continue:");
        String BuyOrQuit = input.next();
        String product;
        int pCount;
        String updateShoppingCart; 
        String checkPrimary;   
        PreparedStatement cstmt;
        ResultSet rs;
        String checkProduct;
        String checkOwner;
        PreparedStatement upstmt;
        String queryP;
        PreparedStatement pstmt2;
        ResultSet rs2;
        String query;
        PreparedStatement pstmt;
        String state;
        
        double checkPrice;
        this.displayProducts(conn, home);
        while(BuyOrQuit.equals("Buy")){   
            this.displayProducts(conn, home);
            System.out.println("What product do you want?");
            product = input.next();
            System.out.println("Which state?");
            state = input.next();
            System.out.println("How many do you want?");
            pCount = input.nextInt();
            // match price
            price = this.price(conn, product, state);
            //check (product and sOwner) primary key, if already in primary key, update table
            updateShoppingCart = "update shoppingCart set pCount = ?+shoppingCart.pCount where product = ? and sOwner =? and price =?";
            checkPrimary = "select product, sOwner, price from shoppingCart";
            cstmt = conn.prepareStatement(checkPrimary);
            rs = cstmt.executeQuery();
            while(rs.next()){
                checkProduct = rs.getString("product");
                checkOwner = rs.getString("sOwner");
                checkPrice = rs.getDouble("price");
                if(checkProduct.equals(product) && checkOwner.equals(sOwner) && checkPrice==price){
                    upstmt = conn.prepareStatement(updateShoppingCart);
                    System.out.println("updating table");
                    upstmt.executeUpdate();
                    upstmt.close();
                }
            }
            cstmt.close();
            
            queryP = "select * from product where pName = ? and price=?";
            pstmt2 = conn.prepareStatement(queryP);
            pstmt2.setString(1, product);
            pstmt2.setDouble(2, price);
            rs2 = pstmt2.executeQuery();
            
            
            query = "insert into shoppingCart values(?, ?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, product);
            pstmt.setInt(2, pCount);
            pstmt.setString(3, sOwner);
            pstmt.setDouble(4, price);           
            
            
            pstmt.executeUpdate();
            pstmt2.close();
            pstmt.close();
            
            System.out.println("Enter Buy to add to cart; Enter else to Quit adding:");
            BuyOrQuit = input.next();
        }
    }
    
    public void modifyShoppingCart(Connection conn, String sOwner, int home) throws SQLException{
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Add to add more products; Enter Quit to quit; enter else to modify products");
        String addOrDelete = input.next();
        String deleteProduct;
        int count;
        double price;
        
        String query = "delete from shoppingCart where product =? and sOwner =?";
        String query2 = "update shoppingCart set pCount =? where product =? and sOwner =? and price =?";
        PreparedStatement pmst;
        while(!addOrDelete.equals("Quit")){
            if(addOrDelete.equals("Add")){
                this.addShoppingCart(conn, sOwner, home);
                break;
            }else{
                System.out.println("enter D to delete product; enter Q to modify quantity");
                deleteProduct = input.next();
                if(deleteProduct.equals("D")){
                    System.out.println("What product do you want to delete?");
                    pmst = conn.prepareStatement(query);
                    deleteProduct = input.next();
                    pmst.setString(1, deleteProduct);
                    pmst.setString(2, sOwner);
                   
                    pmst.executeUpdate();
                    pmst.close();
                }else{
                    System.out.println("What product do you want to change quantity?");
                    deleteProduct = input.next();
                    System.out.println("what's the unit price for it?");
                    price = input.nextDouble();
                    System.out.println("Change to how many?");
                    
                    count = input.nextInt();
                    pmst = conn.prepareStatement(query2);
                    pmst.setInt(1, count);
                    pmst.setString(2, deleteProduct);
                    pmst.setString(3,sOwner);
                    pmst.setDouble(4,price);
                    pmst.executeUpdate();
                    pmst.close();
                }
            
            System.out.println("Enter Add to add more products; Enter Quit to quit; enter else to modify products");
            addOrDelete = input.next();
                
            
            }
        }
        
    }
    
    public double getbalance(Connection conn, String sOwner) throws SQLException{
        String query = "select balance from customer where userID = ?";
        PreparedStatement bstmt= conn.prepareStatement(query);
        bstmt.setString(1,sOwner);
        ResultSet rs = bstmt.executeQuery();
        double balance = 0;
        while(rs.next()){
            balance = rs.getDouble("balance");
        }
        bstmt.close();
        return balance;
    }
    
    public void addbalance(Connection conn, String sOwner) throws SQLException{
        Scanner input = new Scanner(System.in);
        System.out.println("How much do you want to add?");
        double charge = input.nextDouble();
        double oldBalance = getbalance(conn,sOwner);
        double newBalance = charge + oldBalance;
        String query = "update customer set balance =? where userID = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDouble(1,newBalance);
        pstmt.setString(2,sOwner);
        pstmt.executeUpdate();
        pstmt.close();
        System.out.println("Charge Successful!");
    }

    public void updateBalance(Connection conn, String sOwner,double balance)throws SQLException{     
        String query = "update customer set balance =? where userid =?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setDouble(1, balance);
        pstmt.setString(2, sOwner);
        pstmt.execute();
        pstmt.close();
    }
    
    public double payment (Connection conn, String sOwner,int home) throws SQLException, ClassNotFoundException{
        Scanner input = new Scanner(System.in);
        double subtotal = 0;

        // modify shopping cart items       
        System.out.println( "Do you want to modify shopping cart items? Y or else");
        String modify = input.next();
        if(modify.equals("Y")){
            this.modifyShoppingCart(conn, sOwner, home);
        }
        
        // calculate total price;
        String query = "select distinct * from shoppingCart";
        PreparedStatement pstmt= conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery(query);
        String product = "None";
        while (rs.next()){
            product = rs.getString("product");
            int count = rs.getInt("pCount");
            double price = rs.getDouble("price");
            System.out.println(product+"    "+count + "    "+ price);
            for (int i=0; i<count; i++){
                subtotal += price;  
            }           
        }
        System.out.println(subtotal);
        // add payment method
        System.out.println("Do you want to pay with balance or credit card? B or C");
        String pMethod = input.next();
        String find ="None";
        if(pMethod.equals("B")){
            double balance = 0;
            balance = this.getbalance(conn, sOwner);
            System.out.println("Your current balance is "+balance);
            if(balance < subtotal){
                System.out.println("Balance is not enough. Do you want to add balance? Y or N");
                String aBalance = input.next();
                if(aBalance.equals("Y")){
                    this.addbalance(conn,sOwner);
                    double nbl = this.getbalance(conn, sOwner) - subtotal;
                    updateBalance(conn,sOwner,nbl);
                }
                else{
                    System.out.print("Payment fail!");
                    System.exit(0);
                }
            }
        }
        else if(pMethod.equals("C")){
            System.out.println("Do you want to use a new credit card to pay? Y or N");
            String newC = input.next();
            if (newC.equals("N")){
                System.out.println("Which credit card do you want to use to pay?");
                this.displayCredit(conn,sOwner);
                find = input.next();
                if (find.equals("No card record found!")){
                    System.out.println("No card record found, so let's add a new one");
                    find = this.addCredit(conn, sOwner);               
                }else{
                    System.out.print("Your exsiting card No is "+find);
                }
            }else{
                this.addCredit(conn, sOwner);
            }
        }
        System.out.println("total cost is "+ subtotal);
        System.out.println("Order confirmed? Y or N");
        String order = input.next();
        if(order.equals("Y")){
            //?????INSERT ORDER ITEMS FIRST
            this.orders(conn, sOwner, home, find);
        }
        return subtotal;
    }
    
    private void displayCredit(Connection conn, String sOwner) throws SQLException {
         String query = "select * from creditCard where cOwner = ?";
         PreparedStatement pstmt = conn.prepareStatement(query);
         pstmt.setString(1, sOwner);
         ResultSet r = pstmt.executeQuery();
         while(r.next()){
            System.out.print(r.getString("cardNo")+' ');
            System.out.print(r.getString("cOwner")+' ');
            System.out.print(r.getString("cAddress")+' ');
            System.out.println(r.getString("cLimit")+' '); 
         }
         
    }
    
    //get products ordered from shopping cart, delete items from shopping cart  and keep it in orders table
    public void orders(Connection conn, String customer, int dAddress, String cardNo) throws SQLException{
        // create an order ID
        String queryO = "SELECT COALESCE(MAX(orders.orderID), 0) as orderID FROM orders";
        PreparedStatement Ostmt = conn.prepareStatement(queryO);
        ResultSet rs = Ostmt.executeQuery(queryO);
        int newID =0;
        while(rs.next()){
            newID = rs.getInt("orderID")+1;
        }        
        System.out.println(customer+dAddress+cardNo);
        System.out.println(newID);
        Ostmt.close();
        
        String queryC = "select * from shoppingCart where sOwner = ?";
        PreparedStatement pstmt = conn.prepareStatement(queryC);
        pstmt.setString(1, customer); 
        ResultSet rs2= pstmt.executeQuery();
        System.out.println("Creating order FOR "+customer);
       
        // insert values for the order
        String queryOrder = "insert into orders values(?,?,?,?,?,?)";
        PreparedStatement pstmt3 = conn.prepareStatement(queryOrder);
        //delete shopping cart
        String deShopping = "delete from shoppingCart where sOwner = ? and product = ?";
        PreparedStatement pstmt4 = conn.prepareStatement(deShopping);
        
        String product;
        while(rs2.next()){
            product = rs2.getString("product");
            customer = rs2.getString("sOwner");
           
            pstmt3.setInt(1, newID);
            pstmt3.setString(2, customer);
            pstmt3.setString(3, product);
            pstmt3.setInt(4, dAddress);
            pstmt3.setString(5, cardNo);
            pstmt3.setString(6, "issued");
            pstmt3.executeUpdate(); 

            
            pstmt4.setString(1, customer);
            pstmt4.setString(2, product);
            pstmt4.executeUpdate();

        }
        pstmt3.close();
        pstmt4.close();
        System.out.println("Your order id is "+ newID);

        
    }
    
    public void displayProducts(Connection conn, int home) throws SQLException{
        //get state info
        String query3 = "select aState from Address where addrID = ?";
        PreparedStatement findState= conn.prepareStatement(query3);
        String homeState = "None";
        findState.setInt(1, home);
        ResultSet r=findState.executeQuery();
        while(r.next()){
            homeState = r.getString("aState");
        }
        
        String query ="select * from product where pstate = ?";
        String query2 ="select * from product";
        System.out.println("Display products from home state? If yes, Y. Else, display all products");       
        ResultSet rs;
        Scanner input = new Scanner(System.in);
        String chooseHome = input.next();

        if(chooseHome.equals("Y")){
            try(PreparedStatement pstmt = conn.prepareStatement(query)){
                pstmt.setString(1, homeState);
                rs = pstmt.executeQuery();
                    while(rs.next()) { 
                        System.out.print(rs.getString("pName")+' ');
                        System.out.print(rs.getString("price")+' ');
                        System.out.print(rs.getString("pSize")+' ');
                        System.out.print(rs.getString("pState")+' ');
                        System.out.println(rs.getString("pType")+' ');
                    }
                 pstmt.close();
               
            }
        }else{
            try(PreparedStatement pstmt2 = conn.prepareStatement(query2)){
                ResultSet rs2 = pstmt2.executeQuery();   
                while(rs2.next()) { 
                System.out.print(rs2.getString("pName")+' ');
                System.out.print(rs2.getString("price")+' ');
                System.out.print(rs2.getString("pSize")+' ');
                System.out.print(rs2.getString("pState")+' ');
                System.out.println(rs2.getString("pType")+' ');  
                }
                pstmt2.close();
            }       
        
        }
    }
    
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException{
        // TODO code application logic here
        // connect to database
        
        String myDriver= "oracle.jdbc.driver.OracleDriver";
        Class.forName(myDriver);
        Project p = new Project();
        Scanner input = new Scanner(System.in);
        System.out.println("staff or customer, enter S for staff login. Else, customer login");
        String who = input.next();
        	        if(who.equalsIgnoreCase("S")){
	        	
	        	Connection conn= null;
				Statement  stmt=null;
				ResultSet  rs=null;

				try {

					Class.forName("oracle.jdbc.driver.OracleDriver");
					
					conn = DriverManager.getConnection(
							"jdbc:oracle:thin:@fourier.cs.iit.edu:1521:orcl", "yhuan134",
							"piyrwq123.");
					
					stmt= conn.createStatement();
					
					Project myTest= new Project();
					
					myTest.staffLogin(conn);
					
					System.out.println("Please enter w for update warehouse info or p for updating product info:");
					Scanner input111= new Scanner(System.in);
					String choice=input111.nextLine();
				
					if (choice.equalsIgnoreCase("w")){
						try{
						System.out.println("Please enter the warehouseID you need to add product: ");
						Scanner input1=new Scanner(System.in);
						
						int aID=input1.nextInt();
						System.out.println("This is the ID of chosen warehouse: "+ aID);
						
					    System.out.println("Enter the product name: ");
						
						Scanner input2=new Scanner(System.in);
						
						String newpdt= input2.nextLine();   // newpdt is the product that staff add
						
						System.out.println("Enter the count of this product:");
						
						Scanner input3=new Scanner(System.in);
						
						double capacity=input3.nextDouble();
									
						String pdtadd=  "insert into stock (product,warehouse,scount) values ('"+newpdt+"','"+aID+"','"+capacity+"')";
						
						int rs1 = stmt.executeUpdate(pdtadd);
						
						System.out.println("The product is succussfully added!");
						input1.close();
						input2.close();
						input3.close();
					}catch (SQLException e) {

						System.out.println("Something Wrong!");
						e.printStackTrace();
						}
					
					
				}else{
						
					
					System.out.println("Please choose one of these options: add, delete, modify, setprice: ");
					
					Scanner input101=new Scanner(System.in);
					
					String option= input101.nextLine();   
					
					if (option.equalsIgnoreCase("add")){
						
						System.out.println("Please enter the product name: ");
						
						Scanner input2=new Scanner(System.in);
						
						String pName=input2.nextLine(); //pAdd stands the name of the product that staff add 
					
						System.out.println("Please enter the product price: ");
						
						Scanner input3=new Scanner(System.in);
						
						double pPrice=input3.nextDouble();
						System.out.println("Please enter the state of product: ");
						
						Scanner input30=new Scanner(System.in);
						
						String pState=input30.nextLine();
						
						System.out.println("Please enter the product size: ");
						
						Scanner input4=new Scanner(System.in);
						
						double pSize=input4.nextDouble();

						System.out.println("Please enter the product type: ");

						Scanner input5= new Scanner (System.in);
						
						String pType=input5.nextLine();			
						
						
							String pAdd="insert into product values ('"+pName+"','"+pPrice+"','"+pState+"','"+pSize+"','"+pType+"')";
						
							int rs1=stmt.executeUpdate(pAdd);
							
							System.out.println("You successfully add the product!");
							input2.close();
							input3.close();
							input4.close();
							input5.close();
					}else if(option.equalsIgnoreCase("delete")){
						System.out.println("Please enter the product you want to delete:");
						Scanner input6=new Scanner(System.in);
						String dProduct= input6.nextLine();
						System.out.println("Please enter the state of the product:");
						Scanner input66=new Scanner(System.in);
						String dState= input6.nextLine();
							String pDel="delete from product where pName='"+dProduct+"' and pState='"+dState+"'";
							int rs5=stmt.executeUpdate(pDel);
							System.out.println("You successfully delete this product!");
							
						}else if(option.equalsIgnoreCase("modify")){
							System.out.println("Please enter any one type of data among name, price, size or type to modify:");
								Scanner input7=new Scanner(System.in);
								String modify= input7.nextLine();
									if (modify.equalsIgnoreCase("name")){
										System.out.println("Enter the product name you want to modify:");
										Scanner input20=new Scanner(System.in);
										String oName=input20.nextLine();
										System.out.println("Enter the state of this product:");
										Scanner input201=new Scanner(System.in);
										String pdstate=input201.nextLine();
										System.out.println("Please enter the new name:");
										Scanner input8=new Scanner(System.in);
										String nName=input8.nextLine();  //nName means new name 
									    int rs1=stmt.executeUpdate("update product set pName='"+nName+"' where pName='"+oName+"' and pState='"+pdstate+"'");
									    System.out.println("done!");
									}else if(modify.equalsIgnoreCase("price")){
										System.out.println("Enter the name of product you want to modify:");
										Scanner input21=new Scanner(System.in);
										String oName=input21.nextLine();
										System.out.println("Enter the state of this product:");
										Scanner input202=new Scanner(System.in);
										String pdstate=input202.nextLine();
										System.out.println("Please enter the new price:");
										Scanner input9=new Scanner(System.in);
										String nPrice=input9.nextLine();  //nPrice means new price 
									    int rs2=stmt.executeUpdate("update product set price='"+nPrice+"' where pName='"+oName+"' and pState='"+pdstate+"'");
									    System.out.println("done!");
									}else if (modify.equalsIgnoreCase("size")){
										System.out.println("Enter the name of product you want to modify:");
										Scanner input22=new Scanner(System.in);
										String oName=input22.nextLine();
										System.out.println("Enter the state of this product:");
										Scanner input203=new Scanner(System.in);
										String pdstate=input203.nextLine();
										System.out.println("Please enter the new size:");
										Scanner input0=new Scanner(System.in);
										String nSize=input0.nextLine();  //nSize means new size 
									    int rs3=stmt.executeUpdate("update product set pSize='"+nSize+"' where pName='"+oName+"' and pState='"+pdstate+"'");
									    System.out.println("done!");
									}else if (modify.equalsIgnoreCase("type")){
										System.out.println("Enter the name of product you want to modify:");
										Scanner input23=new Scanner(System.in);
										String oName=input23.nextLine();
										System.out.println("Enter the state of this product:");
										Scanner input204=new Scanner(System.in);
										String pdstate=input204.nextLine();
										System.out.println("Please enter the new type:");
										Scanner input11=new Scanner(System.in);
										String nType=input11.nextLine();  //nType means new name 
									    int rs4=stmt.executeUpdate("update product set pType='"+nType+"' where pName='"+oName+"' and pState='"+pdstate+"'");
									    System.out.println("done!");
									}else{
										System.out.println("wrong!retype!");
									}
									}
					else if(option.equalsIgnoreCase("setprice")){
										System.out.println("Please enter the product name:");
										Scanner input12=new Scanner(System.in);
										String sProduct=input12.nextLine();
										System.out.println("Please enter the product state(Abbreviation):");
										Scanner input13=new Scanner(System.in);
										String sState=input13.nextLine();
										System.out.println("Please enter the product price:");
										Scanner input14=new Scanner(System.in);
										double sPrice=input14.nextDouble();
											int rs5=stmt.executeUpdate("Update product set price='"+sPrice+"' where pName='"+sProduct+"' and pState='"+sState+"'" );
											System.out.println("done!");
											
											
											input12.close();
											input13.close();
											input14.close();
								 }else{
										System.out.println("WRONG!Retype please! ");
									}
									
				}
				}	
				 catch (SQLException e) {
					System.out.println("Something wrong!");
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				finally{
					
					try{
						if (rs !=null){
							rs.close();
						}
						if (stmt != null ){
							stmt.close();
						}
						if (conn !=null){
							conn.close();
						}

					}catch(SQLException e){
						e.printStackTrace();
						
					}
				
		
		
				}
           
            
            
          
            
            
          
            
            
            
            
        }else{
        
      
       /* System.out.println("database user Name");
        String hi = input.next();
        System.out.println("database password");
        String bye = input.next();*/
    
        try(Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@fourier.cs.iit.edu:1521:orcl", "yhuan134", "piyrwq123.")) {
            //User login or Create an account
            System.out.println("r u an existing customer? Answer Y or N");
            String Existing = input.next();

            String user = "Nope";
            if ("Y".equals(Existing)){
                   int i =0;
                   user = p.userLogin(conn);
                   while(user.equals("Login Fail!") && i<2){
                       System.out.println("Try again!");
                       user = p.userLogin(conn);
                       i++;                     
                   }          
               
            }else{
                
               user = p.createAccount(conn);
               if(user.equals("Another UserID")){
                   user = p.createAccount(conn);
               }
            } 
            //User select products and make payment
            System.out.println("Please select your delivery address id");
            p.getAddress(conn, user);
            int home = input.nextInt();
            
            //////////
            System.out.println("What do you want to do? 1. Display products 2. Add product to shopping cart 3. Make Payment 4. Modify Account Info 5.Quit");
            int option = input.nextInt();
            while (option!=5){
                switch(option){
                    case 1: p.displayProducts(conn, home);
                            break;
                    case 2: p.addShoppingCart(conn, user, home);
                            break;
                    case 3: p.payment(conn, user, home);
                            break;
                    case 4: p.modifyPerson(conn, user);
                            break;
                    case 5: break;
                }
                System.out.println(" ");
                System.out.println("What do you want to do? 1. Display products 2. Add product to shopping cart 3. Make Payment 4. Modify account Information 5.Quit");
                option = input.nextInt();           
            }                       
       conn.close();
    }   
    }
  
    }   
}
