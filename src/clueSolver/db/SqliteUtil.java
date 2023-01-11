package clueSolver.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author sqlitetutorial.net
 */
public class SqliteUtil {
     /**
     * Connect to a sample database
     */
    public static Connection connect()  {
        Connection conn = null;
        try {
            // db parameters
        	//Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:/Users/Kloster/eclipse-workspace/clueSolver/db/clueSolverDb.sqlite";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            return conn;
            //System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
		}
//        finally {
//            try {
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
//            }
//        }
        return null;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
    }
}