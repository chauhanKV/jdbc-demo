package com.example.jdbcdemo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    List<Product> productList = new ArrayList<>();
    private AtomicLong nextID = new AtomicLong(0);

    @Autowired
    private ProductDAO productDAO;

    public List<Product> getProducts()
    {
        return productList;
    }

    // To inject into the lifecycle of this bean Products
    // When you add a breakpoint to this method and when it hits here, the application will not be ready to accept the request at this point.
    // It is only ready when all the beans are created.
    // This is a annotation based project ( earlier we did xml based bean injection )
    // Here init method is initialized using POSTCONTRUCT annotation
    @PostConstruct
    public void initMethod()
    {
        // This is the part of lifeCycle of this bean
        productList.add(new Product(nextID.incrementAndGet(), "Product1", 32423.345));
        productList.add(new Product(nextID.incrementAndGet(),"Product2", 3463.476));
    }

    public Product getProduct(Long ID)
    {
//        Product result = null;
//        for(Product prod : productList)
//        {
//            if(prod.getID() == ID)
//            {
//                result = prod;
//            }
//        }
//        return result;
        return productDAO.getProductByID(ID);
    }

    public Product addProduct(Product product)
    {
//        product.setID(nextID.incrementAndGet());
//        productList.add(product);
        productDAO.createProduct(product);
        return product;
    }
}
