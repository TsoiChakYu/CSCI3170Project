/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.lang.String;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;
/**
 *
 * @author
 */
public class LibraryInquirySystem {


    public static void main(String[] args) {
        // TODO code application logic here

        User user;
        Scanner scan = new Scanner(System.in);
        int mainChoice = 0;
        int userChoice = 0;
        boolean returnMainMenu = false;

        ConnectDatabase.connectDB();
        printmainMenu();
        while (scan.hasNext()){
            mainChoice = scan.nextInt();            //no input validation
            if(mainChoice == 1){
            user = new Administrator();
            }else if(mainChoice == 2){
                 user = new LibraryUser();
            }else if(mainChoice == 3){
                 user = new Librarian();
            }else if (mainChoice == 4){
                 break;
            }else{
                System.out.println("Invalid input!");
                printmainMenu();
                continue;
            }
            // Choose the menu option
            do {
                user.printMenu();
                userChoice = scan.nextInt();                //no input validation
                returnMainMenu = user.performOperation(userChoice,scan);
            } while (!returnMainMenu);
            System.out.println("");
            printmainMenu();
        }
        scan.close();
    }

    public static void printmainMenu(){
        System.out.println("Welcome to Library Inquiry System!");
        System.out.println();
        System.out.println("-----Main menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. Operations for Administrator");
        System.out.println("2. Operations for Library User");
        System.out.println("3. Operations for Librarian");
        System.out.println("4. Exit this program");
        System.out.print("Enter Your Choice: ");
    }
}

class ConnectDatabase{
    private static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db60";
    private static String dbUserName = "Group60";
    private static String dbPassword = "CSCI3170";
    private static Connection conn = null;


    public static void connectDB(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUserName, dbPassword);
            System.out.println("conn success");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LibraryInquirySystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConn(){
        return conn;
    }
}

abstract class User{
    abstract void printMenu();  //print different menu for the user
    abstract boolean performOperation(int choice, Scanner scan);
    public abstract String toString();
}

class Administrator extends User{

    @Override
    void printMenu(){
        System.out.println();
        System.out.println("-----Operations for administrator menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delete all tables");
        System.out.println("3. Load from datafile");
        System.out.println("4. Show number of records in each table");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");
    }

    @Override
    boolean performOperation(int choice, Scanner scan){
        switch(choice){
            case 1:
                createTable();
                break;
            case 2:
                deleteTable();
                break;
            case 3:
                loadData(scan);
                break;
            case 4:
                showRecords();
                break;
            case 5:
                return true;
            default:
                System.out.println("Invalid Input");
        }

        return false;
    }

    private void createTable(){
        //System.out.println("createTable()");
        System.out.print("Processing...");

        try {
            Statement stmt = ConnectDatabase.getConn().createStatement();
            //drop the same table if existed
            stmt.executeUpdate("DROP TABLE IF EXISTS borrow;");
            stmt.executeUpdate("DROP TABLE IF EXISTS libuser;");
            stmt.executeUpdate("DROP TABLE IF EXISTS user_category;");
            stmt.executeUpdate("DROP TABLE IF EXISTS authorship;");
            stmt.executeUpdate("DROP TABLE IF EXISTS copy;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book_category;");
            //create the tables
            stmt.execute("CREATE TABLE user_category (ucid INTEGER NOT NULL PRIMARY KEY, max INTEGER NOT NULL,  period INTEGER NOT NULL);");
            stmt.execute("CREATE TABLE libuser (libuid CHAR(10) NOT NULL PRIMARY KEY, name VARCHAR(25) NOT NULL, age INTEGER NOT NULL, address VARCHAR(100) NOT NULL, ucid INTEGER NOT NULL, FOREIGN KEY(ucid) REFERENCES user_category(ucid));");
            stmt.execute("CREATE TABLE book_category (bcid INTEGER NOT NULL PRIMARY KEY, bcname VARCHAR(30) NOT NULL);");
            stmt.execute("CREATE TABLE book (callnum VARCHAR(8) NOT NULL PRIMARY KEY, title VARCHAR(30) NOT NULL, publish DATE, rating FLOAT, tborrowed INTEGER NOT NULL, bcid INTEGER, FOREIGN KEY(bcid) REFERENCES book_category(bcid));"); // date format dd/mm/yy
            stmt.execute("CREATE TABLE copy (callnum VARCHAR(8) NOT NULL, copynum INTEGER NOT NULL, PRIMARY KEY (callnum, copynum), FOREIGN KEY (callnum) REFERENCES book(callnum));");
            stmt.execute("CREATE TABLE borrow (libuid CHAR(10) NOT NULL, callnum VARCHAR(8) NOT NULL, copynum INTEGER NOT NULL, checkout DATE NOT NULL, returndate DATE, PRIMARY KEY(libuid, callnum, copynum, checkout), FOREIGN KEY(libuid) REFERENCES libuser(libuid), FOREIGN KEY(callnum, copynum) REFERENCES copy(callnum, copynum));");
            stmt.execute("CREATE TABLE authorship(aname VARCHAR(25) NOT NULL, callnum VARCHAR(8) NOT NULL, PRIMARY KEY(aname, callnum), FOREIGN KEY(callnum) REFERENCES book(callnum));");
            //close statement
            stmt.close();
            System.out.println("Done. Database is initialized.");
        } catch (Exception e){
            System.out.println("[Error]: " + e.toString());
        }

    }

    private void deleteTable(){ //wait for debug
        System.out.println("Processing...");
        try {
            Statement stmt = ConnectDatabase.getConn().createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS borrow;");
            stmt.executeUpdate("DROP TABLE IF EXISTS libuser;");
            stmt.executeUpdate("DROP TABLE IF EXISTS user_category;");
            stmt.executeUpdate("DROP TABLE IF EXISTS authorship;");
            stmt.executeUpdate("DROP TABLE IF EXISTS copy;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book_category;");
            stmt.close();
            System.out.println("Done. Database is removed.");
        } catch (Exception e) {
            System.out.println("[Error]: "+e.toString() +"Please make sure you have already created the database.");
        }
    }

    private void loadData(Scanner scan){
        //System.out.println("loadData()");
        //TODO
        try{
            String folder_path;
            boolean missingFile = false;
            boolean failInsertData = false;
            System.out.println("");             //a blank line according to the demo provided
            System.out.print("Please enter the folder path:");
            folder_path = scan.next();
            System.out.print("Processing...");
            //check book_category.txt exist
            try{
                File bookCategory = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book_category.txt");
                Scanner scanFileData = new Scanner(bookCategory);
                scanFileData.close();
            }catch(Exception e){
                System.out.print("\n[Error]: book_category.txt not found. Please make sure you have inputed the correct folder path.");
                missingFile = true;
            }
            //check book.txt exist
            try{
                File book = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book.txt");
                Scanner scanFileData = new Scanner(book);
                scanFileData.close();
            }catch(Exception e){
                System.out.print("\n[Error]: book.txt not found. Please make sure you have inputed the correct folder path.");
                missingFile = true;
            }
            //check check_out.txt exist
            try{
                File checkOut = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/check_out.txt");
                Scanner scanFileData = new Scanner(checkOut);
                scanFileData.close();
            }catch(Exception e){
                System.out.print("\n[Error]: check_out.txt not found. Please make sure you have inputed the correct folder path.");
                missingFile = true;
            }
            //check user_category.txt exist
            try{
                File userCategory = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/user_category.txt");
                Scanner scanFileData = new Scanner(userCategory);
                scanFileData.close();
            }catch(Exception e){
                System.out.print("\n[Error]: user_category.txt not found. Please make sure you have inputed the correct folder path.");
                missingFile = true;
            }
            //check user.txt exist
            try{
                File userData = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/user.txt");
                Scanner scanFileData = new Scanner(userData);
                scanFileData.close();
            }catch(Exception e){
                System.out.print("\n[Error]: user.txt not found. Please make sure you have inputed the correct folder path.");
                missingFile = true;
            }
            //return if there is any file missing
            if(missingFile){
                System.out.println("");
                return;
            }
            //load book_category data
            try{
                File bookCategory = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book_category.txt");
                Scanner scanFileData = new Scanner(bookCategory);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] bookCategoryData = scanFileData.nextLine().split("\\t");
                    stmt.executeUpdate("INSERT INTO book_category VALUES('" + Integer.parseInt(bookCategoryData[0]) + "',\"" + bookCategoryData[1] + "\");");
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to book_category." + e.toString());
                failInsertData = true;
            }
            //load user_category data
            try{
                File userCategory = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/user_category.txt");
                Scanner scanFileData = new Scanner(userCategory);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] userCategoryData = scanFileData.nextLine().split("\\t");
                    stmt.executeUpdate("INSERT INTO user_category VALUES('" + Integer.parseInt(userCategoryData[0]) + "','" + Integer.parseInt(userCategoryData[1]) + "','" + Integer.parseInt(userCategoryData[2]) + "');");
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to user_category." + e.toString());
                failInsertData = true;
            }
            //Load libuser data
            try{
                File userData = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/user.txt");
                Scanner scanFileData = new Scanner(userData);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] libuserData = scanFileData.nextLine().split("\\t");
                    stmt.executeUpdate("INSERT INTO libuser VALUES(\"" + libuserData[0] + "\",\"" + libuserData[1] + "\",'" + Integer.parseInt(libuserData[2]) + "',\"" + libuserData[3] + "\",'" + Integer.parseInt(libuserData[4]) +"');");
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to libuser." + e.toString());
                failInsertData = true;
            }
            //Load book
            try{
                File book = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book.txt");
                Scanner scanFileData = new Scanner(book);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] bookData = scanFileData.nextLine().split("\\t");
                    if(bookData[4].equals("null")){
                        stmt.executeUpdate("INSERT INTO book VALUES(\"" + bookData[0] + "\",\"" + bookData[2] + "\",\"" + bookData[4] + "\"," + (bookData[5].equals("null")?"null":Float.parseFloat(bookData[5])) + ",'" + Integer.parseInt(bookData[6]) + "','" + Integer.parseInt(bookData[7]) +"');");   //can insert without '', will show warning if insert 'null'/ "null" instead of null
                    }else{
                        stmt.executeUpdate("INSERT INTO book VALUES(\"" + bookData[0] + "\",\"" + bookData[2] + "\",STR_TO_DATE('" + bookData[4] + "', '%d/%m/%Y')," + (bookData[5].equals("null")?"null":Float.parseFloat(bookData[5])) + ",'" + Integer.parseInt(bookData[6]) + "','" + Integer.parseInt(bookData[7]) +"');");
                    }
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to book." + e.toString());
                failInsertData = true;
            }
            //Load authorship
            try{
                File book = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book.txt");
                Scanner scanFileData = new Scanner(book);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] tmpData = scanFileData.nextLine().split("\\t");
                    String[] authorshipData = tmpData[3].split(",");
                    for(String author : authorshipData){
                        stmt.executeUpdate("INSERT INTO authorship VALUES(\"" + author +"\",\"" + tmpData[0] + "\");");
                    }
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to authorship." + e.toString());
                failInsertData = true;
            }
            //Load copy
            try{
                File book = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/book.txt");
                Scanner scanFileData = new Scanner(book);
                Statement stmt = ConnectDatabase.getConn().createStatement();
                while(scanFileData.hasNextLine()){
                    String[] tmpData = scanFileData.nextLine().split("\\t");
                    int tmpCopyNum = Integer.parseInt(tmpData[1]);
                    for(int i=1; i<=tmpCopyNum; i++){   //??? not sure whether this is correct or not
                        stmt.executeUpdate("INSERT INTO copy VALUES('" + tmpData[0] + "','" + i + "');");
                    }
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to copy." + e.toString());
                failInsertData = true;
            }
            //load borrow
            try{
                File checkOut = new File(System.getProperty("user.dir") + "/" + folder_path + "" + "/check_out.txt");
                Scanner scanFileData = new Scanner(checkOut);
                Statement stmt = ConnectDatabase.getConn().createStatement();

                while(scanFileData.hasNextLine()){
                    String[] checkOutData = scanFileData.nextLine().split("\\t");
                    if(checkOutData[4].equals("null")){
                        stmt.executeUpdate("INSERT INTO borrow VALUES(\"" + checkOutData[2] + "\",\"" + checkOutData[0] + "\",'" + Integer.parseInt(checkOutData[1]) + "',STR_TO_DATE('" + checkOutData[3] + "', '%d/%m/%Y')," + checkOutData[4] + ");");
                    }else{
                        stmt.executeUpdate("INSERT INTO borrow VALUES(\"" + checkOutData[2] + "\",\"" + checkOutData[0] + "\",'" + Integer.parseInt(checkOutData[1]) + "',STR_TO_DATE('" + checkOutData[3] + "', '%d/%m/%Y'),STR_TO_DATE('" + checkOutData[4] + "', '%d/%m/%Y'));");
                    }
                }
                stmt.close();
                scanFileData.close();
            }catch(Exception e){
                System.out.println("[Error]: Fail to insert data to borrow." + e.toString());
                failInsertData = true;
            }
            //return if fail to load any of the data
            if(failInsertData){
                return;
            }
            //Show the process is done.
            System.out.println("Done. Data is inputted to the database.");
        }catch(Exception e){
            System.out.println("[Error]: "+e.toString() +"Please make sure you have already created the database.");
        }
    }

    private void showRecords(){
        System.out.println("Number of records in each table:");
        //get recordNum of user_category
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_category;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("user_category: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of user_category. Please make sure the table is already created.");
        }
        //get recordNum of libuser
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM libuser;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("libuser: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of libuser. Please make sure the table is already created.");
        }
        //get recordNum of book_category
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM book_category;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("book_category: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of book_category. Please make sure the table is already created.");
        }
        //get recordNum of book
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM book;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("book: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of book. Please make sure the table is already created.");
        }
        //get recordNum of copy
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM copy;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("copy: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of copy. Please make sure the table is already created.");
        }
        //get recordNum of borrow
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM borrow;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("borrow: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of borrow. Please make sure the table is already created.");
        }
        //get recordNum of authorship
        try{
            Statement stmt = ConnectDatabase.getConn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM authorship;");
            rs.next();
            int recordNum = rs.getInt(1);
            System.out.println("authorship: " + recordNum);
            stmt.close();
        }catch(Exception e){
            System.out.println("[Error]: Fail to get the number of records of authorship. Please make sure the table is already created.");
        }
    }

    @Override
    public String toString(){
        return "Administrator";
    }
}

class LibraryUser extends User{

    @Override
    void printMenu(){
        System.out.println();
        System.out.println("-----Operations for library user menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. Search for Books");
        System.out.println("2. Show loan record of a user");
        System.out.println("3. Return to the main menu");
        System.out.print("Enter Your Choice: ");
    }

    @Override
    boolean performOperation(int choice, Scanner scan){
        switch(choice){
            case 1:
                searchBooks();
                break;
            case 2:
                showLoanRecords();
                break;
            case 3:
                return true;
            default:
                System.out.println("Invalid Input");
        }

        return false;
    }

    private void searchBooks() {
        System.out.println("Choose the Search criterion:");
        System.out.println("1. call number");
        System.out.println("2. title");
        System.out.println("3. author");
        System.out.print("Choose the search criterion: ");
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();
        scan.nextLine();
        if (choice < 1 || choice > 3) {
          System.out.println("Invalid input");
          return;
        }
        System.out.print("Type in the Search Keyword: ");
        String keyword = scan.nextLine();
    
        switch (choice) {
          /* Search by callnum */
          case 1:
            try {
              Statement stmt = ConnectDatabase.getConn().createStatement();
              ResultSet rs = stmt.executeQuery(
                "SELECT B.callnum, B.title, BC.bcname, GROUP_CONCAT(DISTINCT A.aname SEPARATOR ', '), B.rating, COUNT(DISTINCT(C.copynum)) " +
                "FROM book B, copy C, book_category BC, authorship A " +
                "WHERE B.callnum = C.callnum AND B.bcid = BC.bcid AND B.callnum = A.callnum AND " +
                "B.callnum = '" +
                keyword +
                "' " +
                "HAVING B.callnum IS NOT NULL " +
                "ORDER BY B.callnum ASC;"
              );
              if (!rs.isBeforeFirst()) {
                System.out.println("Call Num is invalid");
                return;
              } else {
                System.out.println(
                  "|Call Num|Title|Book Category|Author|Rating|Available No. of Copy|"
                );
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();
                while (rs.next()) {
                  System.out.print("|");
                  for (int i = 1; i <= colNum; i++) {
                    String fieldString = rs.getString(i);
                    System.out.print(fieldString + "|");
                  }
                  System.out.println();
                }
                System.out.println("End of Query");
              }
            } catch (Exception e) {
              System.out.println("[Error]: " + e.toString());
              return;
            }
            break;
          /* Search by title */
          case 2:
            try {
              Statement stmt = ConnectDatabase.getConn().createStatement();
              ResultSet rs = stmt.executeQuery(
                "SELECT B.callnum, B.title, BC.bcname, GROUP_CONCAT(DISTINCT A.aname SEPARATOR ', '), B.rating, COUNT(DISTINCT(C.copynum)) " +
                "FROM book B, copy C, book_category BC, authorship A " +
                "WHERE B.callnum = C.callnum AND B.bcid = BC.bcid AND B.callnum = A.callnum AND B.title LIKE BINARY '%" +
                keyword +
                "%' " +
                "GROUP BY B.callnum " +
                "ORDER BY B.callnum ASC;"
              );
              if (!rs.isBeforeFirst()) {
                System.out.println("No partial match title");
                return;
              } else {
                System.out.println(
                  "|Call Num|Title|Book Category|Author|Rating|Available No. of Copy|"
                );
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();
                while (rs.next()) {
                  System.out.print("|");
                  for (int i = 1; i <= colNum; i++) {
                    String fieldString = rs.getString(i);
                    System.out.print(fieldString + "|");
                  }
                  System.out.println();
                }
                System.out.println("End of Query");
              }
            } catch (Exception e) {
              System.out.println("[Error]: " + e.toString());
              return;
            }
            break;
          /* Search by aname */
          case 3:
            try {
              Statement stmt = ConnectDatabase.getConn().createStatement();
              ResultSet rs = stmt.executeQuery(
                "SELECT B.callnum, B.title, BC.bcname, GROUP_CONCAT(DISTINCT A.aname SEPARATOR ', '), B.rating, COUNT(DISTINCT(C.copynum)) " +
                "FROM book B, copy C, book_category BC, authorship A " +
                "WHERE B.callnum = C.callnum AND B.bcid = BC.bcid AND B.callnum = A.callnum AND A.aname LIKE BINARY '%" +
                keyword +
                "%' " +
                "GROUP BY B.callnum " +
                "ORDER BY B.callnum ASC;"
              );
              if (!rs.isBeforeFirst()) {
                System.out.println("No partial match author name");
                return;
              } else {
                System.out.println(
                  "|Call Num|Title|Book Category|Author|Rating|Available No. of Copy|"
                );
                ResultSetMetaData rsmd = rs.getMetaData();
                int colNum = rsmd.getColumnCount();
                while (rs.next()) {
                  System.out.print("|");
                  for (int i = 1; i <= colNum; i++) {
                    String fieldString = rs.getString(i);
                    System.out.print(fieldString + "|");
                  }
                  System.out.println();
                }
                System.out.println("End of Query");
              }
            } catch (Exception e) {
              System.out.println("[Error]: " + e.toString());
              return;
            }
            break;
          default:
            System.out.println("[Error]: Invalid input");
            return;
        }
      }

    private void showLoanRecords() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter The User ID: ");
        String userId = scan.nextLine();
        try {
          Statement stmt = ConnectDatabase.getConn().createStatement();
          ResultSet rs = stmt.executeQuery(
            "SELECT BOR.callnum, BOR.copynum, B.title, GROUP_CONCAT(DISTINCT A.aname SEPARATOR ', '), BOR.checkout, " +
            "CASE WHEN isNull(BOR.returndate) " +
            "	THEN 'No' " +
            "	ELSE 'Yes' " +
            "END AS isreturned " +
            "FROM book B, copy C, authorship A, libuser U, borrow BOR " +
            "WHERE B.callnum = C.callnum AND B.callnum = A.callnum AND BOR.callnum = B.callnum AND U.libuid = BOR.libuid AND U.libuid = '" +
            userId +
            "' " +
            "GROUP BY BOR.callnum " +
            "ORDER BY BOR.checkout DESC;"
          );
          if (!rs.isBeforeFirst()) {
            System.out.println("No loan record for this user");
            return;
          } else {
            System.out.println("Loan Record:");
            System.out.println(
              "|CallNum|CopyNum|Title|Author|Chcek-out|Returned?|"
            );
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            while (rs.next()) {
              System.out.print("|");
              for (int i = 1; i <= colNum; i++) {
                String fieldString = rs.getString(i);
                System.out.print(fieldString + "|");
              }
              System.out.println();
            }
            System.out.println("End of Query");
          }
        } catch (Exception e) {
          System.out.println("[Error]: " + e.toString());
          return;
        }
      }

    @Override
    public String toString(){
        return "LibraryUser";
    }
}

class Librarian extends User{

    @Override
    void printMenu(){
        System.out.println();
        System.out.println("-----Operations for librarian menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. Book Borrowing");
        System.out.println("2. Book Returning");
        System.out.println("3. List all un-returned book copies which are cheched-out within a period");
        System.out.println("4. Return to the main menu");
        System.out.print("Enter Your Choice: ");
    }

    @Override
    boolean performOperation(int choice, Scanner scan){
        switch(choice){
            case 1:
                borrowBook();
                break;
            case 2:
                returnBook();
                break;
            case 3:
                listUnreturnBook();
                break;
            case 4:
                return true;
            default:
                System.out.println("Invalid Input");
        }

        return false;
    }

    private void borrowBook(){

        // get borrow information
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter The User ID: ");
        String userID = sc.nextLine();
        System.out.print("Enter The Call Number: ");
        String callNum = sc.nextLine();
        System.out.print("Enter The Copy Number: ");
        int copyNum = sc.nextInt();

        // check whether the book is available
        try {
            Connection conn = ConnectDatabase.getConn();

            // get all borrow records with no return date for this copy
            PreparedStatement pstmtBorrowed = conn.prepareStatement("SELECT * FROM borrow WHERE callnum = ? AND copynum = ? AND returndate IS NULL");
            pstmtBorrowed.setString(1, callNum);
            pstmtBorrowed.setInt(2, copyNum);

            ResultSet borrowedCopies = pstmtBorrowed.executeQuery();

            // see if the book is available
            if (borrowedCopies.next()) {
                // the copy was already borrowed, thus not available
                System.out.println("The book is not available.");
            } else {
                // the copy was not yet borrowed, perform borrowing

                // create new borrow record
                PreparedStatement pstmtToBorrow = conn.prepareStatement("INSERT INTO borrow (libuid, callnum, copynum, checkout, returndate) VALUES (?, ?, ?, ?, NULL)");

                pstmtToBorrow.setString(1, userID);
                pstmtToBorrow.setString(2, callNum);
                pstmtToBorrow.setInt(3, copyNum);
                pstmtToBorrow.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                pstmtToBorrow.execute();

                // finish
                System.out.println("Book borrowing performed successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error in borrowing book: " + e);
        }
    }

    private void returnBook(){

        // get return information
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter The User ID: ");
        String userID = sc.nextLine();
        System.out.print("Enter The Call Number: ");
        String callNum = sc.nextLine();
        System.out.print("Enter The Copy Number: ");
        int copyNum = sc.nextInt();
        System.out.print("Enter Your Rating of the Book: ");
        int rating = sc.nextInt();

        // check whether the book is being borrowed
        try {
            Connection conn = ConnectDatabase.getConn();

            // search for the borrow record
            PreparedStatement pstmtIsBorrowed = conn.prepareStatement("SELECT * FROM borrow WHERE libuid = ? AND callnum = ? AND copynum = ? AND returndate IS NULL");
            pstmtIsBorrowed.setString(1, userID);
            pstmtIsBorrowed.setString(2, callNum);
            pstmtIsBorrowed.setInt(3, copyNum);

            ResultSet borrowingCopies = pstmtIsBorrowed.executeQuery();

            // check whether the borrow record exists
            if (borrowingCopies.next()) {
                // pending record exists, perform return
                PreparedStatement pstmtReturn = conn.prepareStatement("UPDATE borrow SET returndate = ? WHERE libuid = ? AND callnum = ? AND copynum = ? AND returndate IS NULL");
                pstmtReturn.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
                pstmtReturn.setString(2, userID);
                pstmtReturn.setString(3, callNum);
                pstmtReturn.setInt(4, copyNum);
                pstmtReturn.executeUpdate();

                // update rating of the book

                // see if the book has rating or not
                PreparedStatement pstmtCheckRating = conn.prepareStatement("SELECT rating FROM book WHERE callnum = ?");
                pstmtCheckRating.setString(1, callNum);
                ResultSet bookRating = pstmtCheckRating.executeQuery();
                bookRating.next();
                String ratingString = bookRating.getString(1);

                if (ratingString == null) {
                    // no rating, set rating as initial rating
                    PreparedStatement pstmtInitRating = conn.prepareStatement("UPDATE book SET rating = ? WHERE callnum = ?");
                    pstmtInitRating.setFloat(1, (float) rating);
                    pstmtInitRating.setString(2, callNum);
                    pstmtInitRating.executeUpdate();
                } else {
                    // rating exists, update rating
                    PreparedStatement pstmtUpdateRating = conn.prepareStatement("UPDATE book SET rating = (rating * tborrowed + ?) / (tborrowed + 1) WHERE callnum = ?");
                    pstmtUpdateRating.setFloat(1, (float) rating);
                    pstmtUpdateRating.setString(2, callNum);
                    pstmtUpdateRating.executeUpdate();
                }

                // increase number of times the book borrowed
                PreparedStatement pstmtIncreaseBorrowedCount = conn.prepareStatement("UPDATE book SET tborrowed = tborrowed + 1 WHERE callnum = ?");
                pstmtIncreaseBorrowedCount.setString(1, callNum);
                pstmtIncreaseBorrowedCount.executeUpdate();

                System.out.println("Book returning performed successfully.");
            } else {
                // no borrowed record found
                System.out.println("No borrowed record is found");
            }
        } catch (Exception e) {
            System.out.println("Error in returning book: " + e);
        }
    }

    private void listUnreturnBook(){

        // get date range
        Scanner sc = new Scanner(System.in);
        System.out.print("Type in the starting date [dd/mm/yyyy]: ");
        String startDateDMY = sc.nextLine();
        System.out.print("Type in the ending date [dd/mm/yyyy]: ");
        String endDateDMY = sc.nextLine();


        // query for all unreturned copies within specified date inclusively
        try {
            java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
            java.util.Date startDate = df.parse(startDateDMY);
            java.util.Date endDate = df.parse(endDateDMY);

            Connection conn = ConnectDatabase.getConn();

            PreparedStatement pstmtUnreturned = conn.prepareStatement("SELECT libuid, callnum, copynum, checkout FROM borrow WHERE checkout BETWEEN ? AND ? AND returndate IS NULL ORDER BY checkout DESC ");
            pstmtUnreturned.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmtUnreturned.setDate(2, new java.sql.Date(endDate.getTime()));

            ResultSet unreturnedCopies = pstmtUnreturned.executeQuery();

            if (!unreturnedCopies.isBeforeFirst()) {
                // empty result
                System.out.println("No unreturned books");
            } else {
                // display list of unreturned copies
                System.out.println("|LibUID|CallNum|CopyNum|Checkout|");
                ResultSetMetaData rsmd = unreturnedCopies.getMetaData();
                int colNum = rsmd.getColumnCount();
                while (unreturnedCopies.next()) {
                    System.out.print("|");
                    for (int i = 1; i <= colNum; i++) {
                        String fieldString = unreturnedCopies.getString(i);
                        System.out.print(fieldString + "|");
                    }
                    System.out.println("");
                }
                System.out.println("End of Query");
            }

        } catch (Exception e) {
            System.out.println("Error in listing unreturned books: " + e);
        }

    }

    @Override
    public String toString(){
        return "Librarian";
    }
}
