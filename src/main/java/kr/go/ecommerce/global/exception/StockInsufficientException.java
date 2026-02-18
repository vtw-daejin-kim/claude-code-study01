package kr.go.ecommerce.global.exception;

public class StockInsufficientException extends BusinessException {

    public StockInsufficientException() {
        super(ErrorCode.STOCK_INSUFFICIENT);
    }

    public StockInsufficientException(String message) {
        super(ErrorCode.STOCK_INSUFFICIENT, message);
    }
}
