/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package libraryinquirysystem;
import java.lang.String;
import java.util.Scanner;
/**
 *
 * @author 
 */
public class LibraryInquirySystem {

    
    public static void main(String[] args) {
        // TODO code application logic here
        
        User user;
        Scanner scan = new Scanner(System.in);
        int choice = 0;
        printmainMenu();
        
        while (scan.hasNext()){
            choice = scan.nextInt();
            if(choice == 1){
            user = new Administrator();
            }else if(choice == 2){
                 user = new LibraryUser();
            }else if(choice == 3){
                 user = new Librarian();
            }else if (choice == 4) {
                break;
            } else{ 
                System.out.println("Invalid input!");
                printmainMenu();
                continue;
            }
            System.out.println(user.toString());
            user.printMenu();
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
        System.out.print("Enter Your Choice: ");
    }
    
}

abstract class User{
    abstract void printMenu();  //print different menu for the user
    abstract void performOperation(int i);
    public abstract String toString();
}

class Administrator 
        extends User{
    
    Administrator(){
        
    }

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
    void performOperation(int i){
        System.out.println(this.toString()+i);
    }
    
    @Override
    public String toString(){
        return "Administrator";
    }
}

class LibraryUser extends User{
    
    LibraryUser(){
        
    }
    
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
    void performOperation(int i){
        System.out.println(this.toString()+i);
    }
    
    @Override
    public String toString(){
        return "LibraryUser";
    }
}

class Librarian extends User{

    Librarian() {
    }
    
    @Override
    void printMenu(){
        System.out.println();
        System.out.println("-----Operations for librarian menu-----");
        System.out.println("What kinds of operations would you like to perform?");
        System.out.println("1. Book Borrowing");
        System.out.println("2. Book Returning");
        System.out.println("3. Book Returning");
        System.out.println("4. List all un-returned book copies which are cheched-out within a period");
        System.out.print("Enter Your Choice: ");
    }
    
    @Override
    void performOperation(int i){
        System.out.println(this.toString()+i);
    }
    
    @Override
    public String toString(){
        return "Librarian";
    }
}