package com.example.jdbcdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// RestController is indirectly including @Component annotation inside its definition, which means it will create spring bean upon initialization.
// @Component is used manually in Spring projects to ask the application to create bean.
@RestController
public class ProductsController {
    // Create logs
    // getLogger(ProductsController) -> this line creates logger for this class
    private static Logger LOGGER = LoggerFactory.getLogger(ProductsController.class);

    //@Autowired - is used for dependency injection.
    //It first created bean of ProductService first and created bean of ProductController then injected ProductService class in this controller.
    //This is field DI using JAVA reflection
    @Autowired
    private ProductService productService;

    // Above is same as
    // This is constructor based DI
    //    public ProductsController(ProductService productService) {
    //        this.productService = productService;
    //    }

    @GetMapping("/products")
    public List<Product> getProducts()
    {
        return productService.getProducts();
    }


    // Ways of sending data in Request

    // Can pass request param name as "nm" . If not defined, it takes as "name" - the one mentioned in the function parameter
    @GetMapping("/hello")
    public String hello(@RequestParam("nm") String name)
    {
        return "Hello " + name;
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id)
    {
        return productService.getProduct(id);
    }

    // Creating product using POST call
    // @RequestBody converts JSON into Product Object that is passed over from client
    @PostMapping("/addproduct")
    public Product createProduct(@RequestBody Product product)
    {
        // This basic console
        // System.out.println(product);
        // Below one is using slf4j LOGGER
//        LOGGER.info("Request Data : {}" , product);
//        LOGGER.error("Got Exception : Some Exception");
//        LOGGER.warn("Got some warning here!");
//        LOGGER.debug("Something to debug");
//        LOGGER.trace("Something to trace");
        return productService.addProduct(product);
    }

}
