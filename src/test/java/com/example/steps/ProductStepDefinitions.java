package com.example.steps;

import com.example.domain.Product;
import com.example.repo.ProductRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ProductStepDefinitions {
    private static final Logger log = LoggerFactory.getLogger(ProductStepDefinitions.class);
    @Autowired
    ProductRepository repository;
    @Value("${app.url}")
    private String appUrl;

    private WebClient webClient;
    private WebClient.ResponseSpec response;

    @Before
    public void before() {
        webClient = WebClient.builder()
                .baseUrl(appUrl + "/v1/products")
                .build();
        var addData = Flux.just(
                new Product(null, "latte", 2.99),
                new Product(null, "decaf", 4.99),
                new Product(null, "green tea", 3.99)
        ).flatMap(repository::save);

        repository.deleteAll()
                .thenMany(addData)
                .thenMany(repository.findAll())
                .doOnNext(product -> log.info(product.toString()))
                .blockLast();
    }

    @Given("API Service is started")
    public void apiServiceIsStarted() throws IOException, URISyntaxException {
//      ping if application is up and running
        URI uri = new URI(appUrl);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(uri.getHost(), uri.getPort()), 1000);
        socket.close();

    }

    @When("when client requests all products")
    public void whenClientRequestsAllProducts() {
        response = webClient.get()
                .retrieve();
    }

    @Then("total of {int} products are returned")
    public void productsAreReturned(int count) {
        var products = response.bodyToFlux(Product.class)
                .collectList()
                .block();
        assertNotNull(products);
        assertEquals(count, products.size());
    }

}