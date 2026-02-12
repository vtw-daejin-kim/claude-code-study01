package com.genious.api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "허용되지 않은 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "엔티티를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근이 거부되었습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),

    // User
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "U001", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다."),

    // Product
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "카테고리를 찾을 수 없습니다."),

    // Inventory
    INSUFFICIENT_INVENTORY(HttpStatus.CONFLICT, "I001", "재고가 부족합니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "I002", "재고 예약을 찾을 수 없습니다."),
    RESERVATION_EXPIRED(HttpStatus.GONE, "I003", "재고 예약이 만료되었습니다."),

    // Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "장바구니를 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CT002", "장바구니 상품을 찾을 수 없습니다."),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O002", "유효하지 않은 주문 상태 변경입니다."),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "O003", "취소할 수 없는 주문입니다."),

    // Payment
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PM001", "결제에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PM002", "결제 정보를 찾을 수 없습니다."),
    REFUND_FAILED(HttpStatus.BAD_REQUEST, "PM003", "환불에 실패했습니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "R002", "리뷰를 작성할 수 없는 주문입니다."),
    DUPLICATE_REVIEW(HttpStatus.CONFLICT, "R003", "이미 리뷰를 작성한 주문입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
