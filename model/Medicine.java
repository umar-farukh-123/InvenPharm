package model;
import java.time.LocalDate;
public class Medicine 
{
    private String name;
    private String category;
    private double price;
    private int quantity;
    private LocalDate expiryDate;

    public Medicine() 
    {
    	//
    }
    
       public Medicine(String name,String category,double price,int quantity,LocalDate expiryDate) 
    {
        this.name=name;
        this.category=category;
        this.price=price;
        this.quantity=quantity;
        this.expiryDate=expiryDate;
    }

    public String getName() 
    { 
    	return name; 
    }
    public void setName(String name) 
    { 
    	this.name=name; 
    }
    
    
    
    public String getCategory() 
    { 
    	return category; 
    }
    public void setCategory(String category) 
    { 
    	this.category=category; 
    }

    public double getPrice() 
    { 
    	return price; 
    }
    public void setPrice(double price) 
    { 
    	this.price=price; 
    }

    public int getQuantity() 
    { 
    	return quantity; 
    }
    public void setQuantity(int quantity) 
    { 
    	this.quantity=quantity; 
    }
    
    
    public LocalDate getExpiryDate() 
    { 
    	return expiryDate; 
    }
    public void setExpiryDate(LocalDate expiryDate) 
    {
    	this.expiryDate=expiryDate; 
    }
    
}
