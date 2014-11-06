import java.io.*;
import java.sql.*;
public class dbconnection
{
    
    
static int login(String username,String password)
{
    int n=0;
       
  try
  {
         Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        Connection conn=DriverManager.getConnection("jdbc:odbc:DB2COPY1","sridhar","sridhar");
            //Statement st = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement("select * from SRIDHAR.login where username = ? and password = ?");
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
           while(rs.next())
                       {  
                
                System.out.print("Login Successfull ");
               
                      n=1;
                      return n;
                
           }
                           
           
  }
            catch(Exception e)
            {}
        return n;
           }
}
