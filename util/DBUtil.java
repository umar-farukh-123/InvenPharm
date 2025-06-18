package util;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil 
{

    private static final String URL="jdbc:oracle:thin:@//DESKTOP-40MNTPG:1521/xe";  
         private static final String USER="SYS as SYSDBA";                     
      private static final String PASS="sql";                       
   
      public static Connection getConnection()        throws Exception 
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }


}
