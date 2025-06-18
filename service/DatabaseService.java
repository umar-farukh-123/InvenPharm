package service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Medicine;
import util.DBUtil;

public class DatabaseService 
{
    // Load all medicines from database
    public static List<Medicine> loadInventory() 
    {
        List<Medicine> list=new ArrayList<>();
        String sql="SELECT * FROM medicines";

        try(Connection con=DBUtil.getConnection();
             PreparedStatement pst=con.prepareStatement(sql);
             ResultSet rs=pst.executeQuery()) 
        {

            while (rs.next()) 
            {
                Medicine m=new Medicine();
                m.setName(rs.getString("name"));
                m.setCategory(rs.getString("category"));
                m.setQuantity(rs.getInt("quantity"));
                m.setPrice(rs.getDouble("price"));
                m.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                list.add(m);
            }

        } 
        catch(Exception e) 
        {
            e.printStackTrace();
        }
        
        return list;
    }

    // Save inventory (clear all and insert current list)
    public static void saveInventory(List<Medicine> inventory) 
    {
        String deleteSQL="DELETE FROM medicines where id =1";
        String insertSQL="INSERT INTO medicines (name, category, quantity, price, expiry_date) VALUES (?, ?, ?, ?, ?)";

        try(Connection con=DBUtil.getConnection();
             Statement stmt=con.createStatement()) 
        {

            // Delete all old entries
            stmt.executeUpdate(deleteSQL);

            // Insert all new entries
            try(PreparedStatement pst=con.prepareStatement(insertSQL)) 
            {
                for(Medicine m:inventory) 
                {
                    pst.setString(1,m.getName());
                    pst.setString(2,m.getCategory());
                    pst.setInt(3,m.getQuantity());
                    pst.setDouble(4,m.getPrice());
                    pst.setDate(5,Date.valueOf(m.getExpiryDate()));
                    pst.addBatch();
                }
                
                pst.executeBatch();
            }

        } 
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }
}
