/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.String;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author 
 */
public class LibraryInquirySystem {
    private static final String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group60";
    private static final String dbUserName = "Group60";
    private static final String dbPassword = "CSCI3170";
    
    public static void main(String[] args) {
        // TODO code application logic here
        ConnectDB();
        
        User user;
        Scanner scan = new Scanner(System.in);
        int mainChoice = 0;
        int userChoice = 0;
        boolean returnMainMenu = false;
        
        printmainMenu();
        while (scan.hasNext()){
            mainChoice = scan.nextInt();            //no input validation
            if(mainChoice == 1){
            user = new Administrator();
            }else if(mainChoice == 2){
                 user = new LibraryUser();
            }else if(mainChoice == 3){
                 user = new Librarian();
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
    
    public static void ConnectDB(){
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbAddress, dbUserName, dbPassword);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LibraryInquirySystem.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        //TODO
    }
    
    private void deleteTable(){
        System.out.println("deleteTable()");
        //TODO
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