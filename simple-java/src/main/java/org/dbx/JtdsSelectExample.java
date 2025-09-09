package org.dbx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JtdsSelectExample {

    public static void execute() {
        String url = "jdbc:jtds:sqlserver://localhost:1433/test";
        String user = "sa";
        String password = "Str0ng!Passw0rd";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            String query = "SELECT id, name FROM test.dbo.new_t WHERE id = 1;";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("name: " + rs.getString("name"));
                // Add more columns as needed
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
