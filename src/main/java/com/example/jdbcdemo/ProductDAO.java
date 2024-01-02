package com.example.jdbcdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class ProductDAO {
    @Value("${dbURL}")
    private String url;

    @Value("${usr}")
    private String user;

    @Value("${pass}")
    private String pass;

    @Value("${insertquery}")
    private String insertQuery;

    public Product getProductByID(Long id) {
        Connection connection = null;
        Product product = null;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String sql = "select * from product where id=" + id;

            // For executing select statement we use "executeQuery function"
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                product = new Product(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getDouble("price"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return product;
    }

    public Product createProduct(Product product) {
        Connection connection = null;
        Integer affectedRows = 0;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            Statement statement = connection.createStatement();
            String sql = "insert into product values (null, '" + product.getName() + "', '" + product.getCost() + "')";

            // Execute() method is used to create entry and return boolean value stating the command has been executed successfully or not.
            // ResultSet resultSet = statement.execute(sql);

            // We can also use executeUpdate() method which sends us the number of rows affected after CRUD operation
            // Statement.RETURN_GENERATED_KEYS -> this needs to be mentioned inorder to get the last generated id using last insert statement.
            affectedRows = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (affectedRows == 0)
                throw new SQLException("Creating Product Failed!");

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setID(generatedKeys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return product;
    }


// Note : If you see the code, some of the lines are redundant. Also, interfaces like ResultSet, Statement use input/output streams
// which may not be closed after execution making the program more memory intensive. Although if you see their definition
// they are actually extending AutoClosable interface which closes them after execution.

// Otherwise , you have to surround them with try block so that try can close it itself after work is done.
// Like this - try(Statement statement = connection.createStatement()) { // do further job ....}


// Using prepared Statement - for more readability

    public Product createProductWithPreparedStatement(Product product) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            String sql = insertQuery; // -> We can put our statements in properties files and fetch from there.
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getCost());
            // this gives error -> "You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near '?, ?)' at line 1"
            // int affectedRows = statement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);  ==> Need to check this on how to get generated keys
            // Check line 102 for fix.
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating Product Failed!");

//            ResultSet generatedKeys = statement.getGeneratedKeys();
//            if (generatedKeys.next()) {
//                product.setID(generatedKeys.getLong(1));
//            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return product;
    }


    // Using TRANSACTIONS
    public Product createProductWithPSTransaction(Product product) {
        Connection connection = null;
        boolean autoCommit = false;
        try {
            connection = DriverManager.getConnection(url, user, pass);

            // Get autoCommit reference
            autoCommit = connection.getAutoCommit();
            // Set it to false initially - before starting the transaction.
            connection.setAutoCommit(false);

            String sql = insertQuery;
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getCost());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating Product Failed!");

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                product.setID(generatedKeys.getLong(1));
            }

            // Commit once it's done
            connection.commit();

        } catch (SQLException e) {
            try {
                // Rollback in case of error.
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(autoCommit);
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return product;
    }
}
