package com.example.jdbcdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.xml.transform.Result;
import java.sql.*;

@Repository
public class ProductDAO {
    @Value("${dbURL}")
    private String url;

    @Value("${user}")
    private String user;

    @Value("${pass}")
    private String pass;
    public Product getProductByID(Long id) {
        Connection connection = null;
        Product product = null;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String sql = "select * from product where id=" + id;

            // For executing select statement we use "executeQuery function"
            ResultSet resultSet = statement.executeQuery(sql);

            while(resultSet.next())
            {
                product = new Product(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getDouble("price"));
            }
        }
        catch(SQLException ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            if(connection != null)
            {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return product;
    }

    public int createProduct(Product product)
    {
        Connection connection = null;
        Integer affectedRows = 0;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String sql = "insert into product values (null, '"+product.getName()+"', '"+product.getCost()+"')";

            // Execute() method is used to create entry and return boolean value stating the command has been executed successfully or not.
            // ResultSet resultSet = statement.execute(sql);

            // We can also use executeUpdate() method which sends us the number of rows affected after CRUD operation
            affectedRows = statement.executeUpdate(sql);
            if(affectedRows == 0)
                throw new SQLException("Creating Product Failed!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally
        {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return affectedRows;
    }
}
