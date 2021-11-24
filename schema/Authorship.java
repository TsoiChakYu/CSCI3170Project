package schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Authorship {
    private String aname;
    private String callnum;

    public Authorship(String aname, String callnum) {
        this.aname = aname;
        this.callnum = callnum;
    }

    public void insertToDb(Connection con) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO authorship (aname, callnum) VALUES (?, ?)");
            pstmt.setString(1, aname);
            pstmt.setString(2, callnum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[Error]" + e);
        }
    }
}

