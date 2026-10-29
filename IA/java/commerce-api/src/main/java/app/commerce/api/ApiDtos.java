package app.commerce.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {

    private ApiDtos() {
    }

    public static class UserInput {

        public String name;
        public String email;
        public String phone;
        public String password;

        public UserInput() {
        }
    }

    public static class UserView {

        public Long id;
        public String name;
        public String email;
        public String phone;

        public UserView(
            Long id,
            String name,
            String email,
            String phone
        ) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phone = phone;
        }
    }

    public static class CategoryBrief {

        public Long id;
        public String name;

        public CategoryBrief(
            Long id,
            String name
        ) {
            this.id = id;
            this.name = name;
        }
    }

    public static class ProductBrief {

        public Long id;
        public String name;

        public ProductBrief(
            Long id,
            String name
        ) {
            this.id = id;
            this.name = name;
        }
    }

    public static class CategoryView {

        public Long id;
        public String name;
        public List<ProductBrief> products;

        public CategoryView(
            Long id,
            String name,
            List<ProductBrief> products
        ) {
            this.id = id;
            this.name = name;
            this.products = products;
        }
    }

    public static class ProductView {

        public Long id;
        public String name;
        public String description;
        public BigDecimal price;
        public String imageUrl;
        public List<CategoryBrief> categories;

        public ProductView(
            Long id,
            String name,
            String description,
            BigDecimal price,
            String imageUrl,
            List<CategoryBrief> categories
        ) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.imageUrl = imageUrl;
            this.categories = categories;
        }
    }

    public static class PaymentView {

        public Long id;
        public Instant moment;

        public PaymentView(
            Long id,
            Instant moment
        ) {
            this.id = id;
            this.moment = moment;
        }
    }

    public static class OrderItemView {

        public ProductBrief product;
        public Integer quantity;
        public BigDecimal price;
        public BigDecimal subtotal;

        public OrderItemView(
            ProductBrief product,
            Integer quantity,
            BigDecimal price,
            BigDecimal subtotal
        ) {
            this.product = product;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = subtotal;
        }
    }

    public static class OrderView {

        public Long id;
        public Instant moment;
        public String status;
        public UserView customer;
        public List<OrderItemView> items;
        public PaymentView payment;
        public BigDecimal total;

        public OrderView(
            Long id,
            Instant moment,
            String status,
            UserView customer,
            List<OrderItemView> items,
            PaymentView payment,
            BigDecimal total
        ) {
            this.id = id;
            this.moment = moment;
            this.status = status;
            this.customer = customer;
            this.items = items;
            this.payment = payment;
            this.total = total;
        }
    }
}