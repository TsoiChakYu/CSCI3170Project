package schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Book {
    private String callnum;
    private String title;
    private String publish;
    private float rating;
    private int tborrowed;
    private String bcid;

    public Book(String callnum, String title, String publish, float rating, int tborrowed, String bcid) {
        this.callnum = callnum;
        this.title = title;
        this.publish = publish;
        this.rating = rating;
        this.tborrowed = tborrowed;
        this.bcid = bcid;
    }

    public void insertToDb(Connection con) {
        try {
            PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO Book (callnum, title, publish, rating, tborrowed, bcid) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, callnum);
            pstmt.setString(2, title);
            pstmt.setString(3, publish);
            pstmt.setFloat(4, rating);
            pstmt.setInt(5, tborrowed);
            pstmt.setString(6, bcid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[Error]" + e);
        }
    }

}