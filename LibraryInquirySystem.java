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
        
        ConnectDatabase connDB = new ConnectDatabase();
        connDB.connectDB();
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
                returnMainMenu = user.performOperation(userChoice);
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
    private static String dbAddress;
    private static String dbUserName;
    private static String dbPassword;
    private static Connection conn;

    public ConnectDatabase(){
        dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db60";
        dbUserName = "Group60";
        dbPassword = "CSCI3170";
        conn = null;
    }

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
    abstract boolean performOperation(int choice);
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
    boolean performOperation(int choice){
        switch(choice){
            case 1:
                createTable();
                break;
            case 2:
                deleteTable();
                break;
            case 3:
                loadData();
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
        System.out.println("createTable()");
        System.out.print("Processing...");
        
        try {
            Statement stmt = ConnectDatabase.getConn().createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS borrow;");  
            stmt.executeUpdate("DROP TABLE IF EXISTS libuser;");
            stmt.executeUpdate("DROP TABLE IF EXISTS user_category;");        
            stmt.executeUpdate("DROP TABLE IF EXISTS authorship;");  
            stmt.executeUpdate("DROP TABLE IF EXISTS copy;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book;");
            stmt.executeUpdate("DROP TABLE IF EXISTS book_category;");
            // stmt.executeUpdate("CREATE TABLE category ();");
            stmt.execute("CREATE TABLE user_category (ucid INTEGER NOT NULL PRIMARY KEY, max INTEGER NOT NULL,  period INTEGER NOT NULL);");
            stmt.execute("CREATE TABLE libuser (libuid CHAR(10) NOT NULL PRIMARY KEY, name VARCHAR(25) NOT NULL, address VARCHAR(100) NOT NULL, ucid INTEGER NOT NULL, FOREIGN KEY(ucid) REFERENCES user_category(ucid));");
            stmt.execute("CREATE TABLE book_category (bcid INTEGER NOT NULL PRIMARY KEY, bcname VARCHAR(30) NOT NULL);");
            stmt.execute("CREATE TABLE book (callnum VARCHAR(8) NOT NULL PRIMARY KEY, title VARCHAR(30) NOT NULL, publish DATE, rating FLOAT, tborrowed INTEGER NOT NULL, bcid INTEGER, FOREIGN KEY(bcid) REFERENCES book_category(bcid));"); // date format dd/mm/yy
            stmt.execute("CREATE TABLE copy (callnum VARCHAR(8) NOT NULL, copynum INTEGER NOT NULL, PRIMARY KEY (callnum, copynum), FOREIGN KEY (callnum) REFERENCES book(callnum));");
           // stmt.execute("CREATE TABLE borrow (libuid CHAR(10) NOT NULL, );"):
            stmt.execute("CREATE TABLE borrow (libuid CHAR(10) NOT NULL, callnum VARCHAR(8) NOT NULL, copynum INTEGER NOT NULL, checkout DATE NOT NULL, returndate DATE, PRIMARY KEY(libuid, callnum, copynum, checkout), FOREIGN KEY(libuid) REFERENCES libuser(libuid), FOREIGN KEY(callnum, copynum) REFERENCES copy(callnum, copynum));");
            stmt.execute("CREATE TABLE authorship(aname VARCHAR(25) NOT NULL, callnum VARCHAR(8) NOT NULL, PRIMARY KEY(aname, callnum), FOREIGN KEY(callnum) REFERENCES book(callnum));");
            //stmt.execute("show")
            stmt.close();
            System.out.println("Done. Database is initialized.");
        } catch (Exception e){
            System.out.println("[Error]: " + e.toString());
        }
        
    }
    
    private void deleteTable(){ //wait for debug
        System.out.println("deleteTable()");
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
            //TODO: handle exception
            System.out.println("[Error]: "+e.toString());
        }
    }
    
    private void loadData(){
        System.out.println("loadData()");
        //TODO
    }
    
    private void showRecords(){
        System.out.println("showRecords()");
        //TODO
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
    boolean performOperation(int choice){
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
    
    private void searchBooks(){
        System.out.println("searchBooks()");
        //TODO
    }
    
    private void showLoanRecords(){
        System.out.println("showLoanRecords()");
        //TODO
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
    boolean performOperation(int choice){
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
        System.out.println("borrowBook()");
        //TODO
    }
    
    private void returnBook(){
        System.out.println("returnBook()");
        //TODO
    }
    
    private void listUnreturnBook(){
        System.out.println("listUnreturnBook()");
        //TODO
    }
    
    @Override
    public String toString(){
        return "Librarian";
    }
}