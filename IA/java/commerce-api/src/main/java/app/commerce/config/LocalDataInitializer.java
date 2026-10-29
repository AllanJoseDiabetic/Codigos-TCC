package app.commerce.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import app.commerce.domain.Category;
import app.commerce.domain.OrderItem;
import app.commerce.domain.OrderStatus;
import app.commerce.domain.Payment;
import app.commerce.domain.Product;
import app.commerce.domain.PurchaseOrder;
import app.commerce.domain.User;

import app.commerce.repository.CategoryRepository;
import app.commerce.repository.OrderRepository;
import app.commerce.repository.ProductRepository;
import app.commerce.repository.UserRepository;

import app.commerce.security.PasswordHasher;

@Configuration
@Profile("test")
public class LocalDataInitializer {

    @Bean
    public CommandLineRunner initializeLocalData(
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        ProductRepository productRepository,
        OrderRepository orderRepository,
        PasswordHasher passwordHasher
    ) {
        return args -> {

            boolean alreadyInitialized =
                userRepository.count() > 0
                || categoryRepository.count() > 0
                || productRepository.count() > 0
                || orderRepository.count() > 0;

            if (alreadyInitialized) {
                return;
            }

            User firstUser = new User(
                "Pessoa Exemplo A",
                "pessoa.a@example.invalid",
                "0000000001",
                passwordHasher.hash(
                    UUID.randomUUID().toString()
                )
            );

            User secondUser = new User(
                "Pessoa Exemplo B",
                "pessoa.b@example.invalid",
                "0000000002",
                passwordHasher.hash(
                    UUID.randomUUID().toString()
                )
            );

            firstUser = userRepository.save(firstUser);
            secondUser = userRepository.save(secondUser);

            Category stationery = new Category(
                "Papelaria"
            );

            Category electronics = new Category(
                "Eletronicos"
            );

            Category gifts = new Category(
                "Presentes"
            );

            stationery = categoryRepository.save(stationery);
            electronics = categoryRepository.save(electronics);
            gifts = categoryRepository.save(gifts);

            Product notebook = new Product(
                "Caderno de planejamento",
                "Caderno com folhas pautadas para anotacoes e organizacao.",
                new BigDecimal("25.50"),
                "https://images.example.invalid/caderno.png"
            );

            notebook.addCategory(stationery);
            notebook.addCategory(gifts);

            Product lamp = new Product(
                "Luminaria compacta",
                "Luminaria de mesa para iluminacao de ambientes internos.",
                new BigDecimal("89.90"),
                "https://images.example.invalid/luminaria.png"
            );

            lamp.addCategory(electronics);
            lamp.addCategory(gifts);

            Product pen = new Product(
                "Caneta de ponta fina",
                "Caneta para escrita cotidiana e trabalhos de precisao.",
                new BigDecimal("12.75"),
                "https://images.example.invalid/caneta.png"
            );

            pen.addCategory(stationery);

            notebook = productRepository.save(notebook);
            lamp = productRepository.save(lamp);
            pen = productRepository.save(pen);

            PurchaseOrder firstOrder = new PurchaseOrder(
                Instant.parse("2024-05-10T14:30:00Z"),
                OrderStatus.WAITING_PAYMENT,
                firstUser
            );

            firstOrder.addItem(
                new OrderItem(
                    notebook,
                    2,
                    new BigDecimal("25.50")
                )
            );

            firstOrder.addItem(
                new OrderItem(
                    lamp,
                    1,
                    new BigDecimal("89.90")
                )
            );

            orderRepository.save(firstOrder);

            PurchaseOrder secondOrder = new PurchaseOrder(
                Instant.parse("2024-05-12T09:20:00Z"),
                OrderStatus.PAID,
                secondUser
            );

            secondOrder.addItem(
                new OrderItem(
                    pen,
                    3,
                    new BigDecimal("12.75")
                )
            );

            secondOrder.addItem(
                new OrderItem(
                    notebook,
                    1,
                    new BigDecimal("25.50")
                )
            );

            Payment payment = new Payment(
                Instant.parse("2024-05-12T09:25:00Z")
            );

            secondOrder.setPayment(payment);

            orderRepository.save(secondOrder);
        };
    }
}