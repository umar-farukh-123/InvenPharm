package util;
import model.Medicine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicinesDAO 
{

    public static List<Medicine>getAllMedicines() 
    {
        List<Medicine> medicines=new ArrayList<>();
        String query="SELECT * FROM medicines";

        try(Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query);
             ResultSet rs=stmt.executeQuery()) 
        {

            while(rs.next()) 
            {
                medicines.add(new Medicine(
                       rs.getString("name"),
                         rs.getString("category"),
                        rs.getDouble("price"),
                            rs.getInt("quantity"),
                        rs.getDate("expiry_date").toLocalDate()
                ));
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return medicines;
    }

    
    
    public static boolean insertMedicine(Medicine med) 
    {
        String query="INSERT INTO medicines (name, category, price, quantity, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query)) 
        {

            stmt.setString(1,med.getName());
            stmt.setString(2,med.getCategory());
            stmt.setDouble(3,med.getPrice());
            stmt.setInt(4,med.getQuantity());
            stmt.setDate(5,Date.valueOf(med.getExpiryDate()));

            return stmt.executeUpdate() > 0;

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    
    
    
    public static boolean updateMedicine(Medicine med) 
    {
        String query="UPDATE medicines SET category=?, price=?, quantity=?, expiry_date=? WHERE name=?";

        try(Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query)) 
        {

            stmt.setString(1,med.getCategory());
            stmt.setDouble(2,med.getPrice());
            stmt.setInt(3,med.getQuantity());
            stmt.setDate(4,Date.valueOf(med.getExpiryDate()));
            stmt.setString(5,med.getName());

            return stmt.executeUpdate() >0;

        } 
        catch(Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMedicine(String name) 
    {
        String query="DELETE FROM medicines WHERE name=?";

        try(Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query)) 
        {

            stmt.setString(1,name);
            return stmt.executeUpdate() >0;

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Medicine> searchMedicines(String keyword) 
    {
        List<Medicine> result=new ArrayList<>();
        String query="SELECT * FROM medicines WHERE LOWER(name) LIKE ? OR LOWER(category) LIKE ?";

        try(Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query)) 
        {

            String searchTerm="%"+keyword.toLowerCase()+"%";
            stmt.setString(1,searchTerm);
            stmt.setString(2,searchTerm);
            try (ResultSet rs=stmt.executeQuery()) 
            {
                while (rs.next()) 
                {
                    result.add(new Medicine(
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDate("expiry_date").toLocalDate()
                    ));
                }
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return result;
    }

    public static Medicine getMedicineByName(String name) 
    {
        String query="SELECT * FROM medicines WHERE name = ?";

        try(Connection conn=DBUtil.getConnection();
             PreparedStatement stmt=conn.prepareStatement(query)) 
        {

            stmt.setString(1, name);
            try(ResultSet rs=stmt.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return new Medicine(
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDate("expiry_date").toLocalDate()
                    );
                }
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return null;
    }
}
