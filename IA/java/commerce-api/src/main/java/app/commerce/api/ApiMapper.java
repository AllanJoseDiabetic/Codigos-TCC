package app.commerce.api;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import app.commerce.domain.Category;
import app.commerce.domain.OrderItem;
import app.commerce.domain.Payment;
import app.commerce.domain.Product;
import app.commerce.domain.PurchaseOrder;
import app.commerce.domain.User;

import app.commerce.api.ApiDtos.CategoryBrief;
import app.commerce.api.ApiDtos.CategoryView;
import app.commerce.api.ApiDtos.OrderItemView;
import app.commerce.api.ApiDtos.OrderView;
import app.commerce.api.ApiDtos.PaymentView;
import app.commerce.api.ApiDtos.ProductBrief;
import app.commerce.api.ApiDtos.ProductView;
import app.commerce.api.ApiDtos.UserView;

public final class ApiMapper {

    private ApiMapper() {
    }

    public static UserView toUserView(User user) {
        return new UserView(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getPhone()
        );
    }

    public static CategoryBrief toCategoryBrief(
        Category category
    ) {
        return new CategoryBrief(
            category.getId(),
            category.getName()
        );
    }

    public static ProductBrief toProductBrief(
        Product product
    ) {
        return new ProductBrief(
            product.getId(),
            product.getName()
        );
    }

    public static CategoryView toCategoryView(
        Category category
    ) {
        List<ProductBrief> products = category
            .getProducts()
            .stream()
            .sorted(
                Comparator.comparing(Product::getId)
            )
            .map(ApiMapper::toProductBrief)
            .collect(Collectors.toList());

        return new CategoryView(
            category.getId(),
            category.getName(),
            products
        );
    }

    public static ProductView toProductView(
        Product product
    ) {
        List<CategoryBrief> categories = product
            .getCategories()
            .stream()
            .sorted(
                Comparator.comparing(Category::getId)
            )
            .map(ApiMapper::toCategoryBrief)
            .collect(Collectors.toList());

        return new ProductView(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getImageUrl(),
            categories
        );
    }

    public static OrderItemView toOrderItemView(
        OrderItem item
    ) {
        return new OrderItemView(
            toProductBrief(item.getProduct()),
            item.getQuantity(),
            item.getPrice(),
            item.getSubtotal()
        );
    }

    public static PaymentView toPaymentView(
        Payment payment
    ) {
        if (payment == null) {
            return null;
        }

        return new PaymentView(
            payment.getId(),
            payment.getMoment()
        );
    }

    public static OrderView toOrderView(
        PurchaseOrder order
    ) {
        List<OrderItemView> items = order
            .getItems()
            .stream()
            .sorted(
                Comparator.comparing(OrderItem::getId)
            )
            .map(ApiMapper::toOrderItemView)
            .collect(Collectors.toList());

        return new OrderView(
            order.getId(),
            order.getMoment(),
            order.getStatus().name(),
            toUserView(order.getCustomer()),
            items,
            toPaymentView(order.getPayment()),
            order.getTotal()
        );
    }
}