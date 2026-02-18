package kr.go.ecommerce.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C002", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "Method not allowed"),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Authentication required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "Access denied"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "Invalid or expired token"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A004", "Invalid login credentials"),

    // User
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "U001", "Login ID already exists"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "Email already exists"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "User not found"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U004", "Current password does not match"),

    // Brand
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "Brand not found"),
    BRAND_ALREADY_DELETED(HttpStatus.CONFLICT, "B002", "Brand is already deleted"),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Product not found"),
    PRODUCT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "P002", "Product is not available for purchase"),
    STOCK_INSUFFICIENT(HttpStatus.CONFLICT, "P003", "Insufficient stock"),

    // Like
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "L001", "Like already exists"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "L002", "Like not found"),

    // Cart
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "Cart item not found"),
    CART_ITEM_DUPLICATE(HttpStatus.CONFLICT, "CT002", "Product already in cart"),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "Order not found"),
    INVALID_ORDER_STATE(HttpStatus.CONFLICT, "O002", "Invalid order state transition"),
    ORDER_EXPIRED(HttpStatus.CONFLICT, "O003", "Order has expired"),
    IDEMPOTENCY_CONFLICT(HttpStatus.CONFLICT, "O004", "Duplicate idempotency key");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
