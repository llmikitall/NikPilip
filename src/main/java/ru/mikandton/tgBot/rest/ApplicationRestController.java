package ru.mikandton.tgBot.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mikandton.tgBot.services.ApplicationService;
import ru.mikandton.tgBot.entities.ClientOrder;
import ru.mikandton.tgBot.entities.Product;

import java.util.List;

@RestController
public class ApplicationRestController {
    private final ApplicationService applicationService;

    public ApplicationRestController(ApplicationService applicationService){
        this.applicationService = applicationService;
    }

    @GetMapping("/rest/products/search")
    public List<Product> getProductsByCategoryId(@RequestParam Long categoryId){
        return applicationService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/rest/clients/{id}/orders")
    public List<ClientOrder> getClientOrders(@PathVariable Long id){
        return applicationService.getClientOrders(id);
    }

    @GetMapping("/rest/clients/{id}/products")
    public List<Product> getClientProduct(@PathVariable Long id){
        return applicationService.getClientProduct(id);
    }

    @GetMapping("/rest/products/popular")
    public List<Product> getTopPopularProducts(@RequestParam Integer limit){
        return applicationService.getTopPopularProducts(limit);
    }

}
