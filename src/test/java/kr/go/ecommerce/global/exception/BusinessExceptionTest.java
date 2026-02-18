package kr.go.ecommerce.global.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void businessExceptionShouldCarryErrorCode() {
        BusinessException ex = new BusinessException(ErrorCode.INTERNAL_ERROR);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(ex.getMessage()).isEqualTo(ErrorCode.INTERNAL_ERROR.getMessage());
    }

    @Test
    void businessExceptionShouldSupportCustomMessage() {
        BusinessException ex = new BusinessException(ErrorCode.INTERNAL_ERROR, "custom msg");
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(ex.getMessage()).isEqualTo("custom msg");
    }

    @Test
    void entityNotFoundExceptionShouldExtendBusinessException() {
        EntityNotFoundException ex = new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    void duplicateExceptionShouldExtendBusinessException() {
        DuplicateException ex = new DuplicateException(ErrorCode.DUPLICATE_LOGIN_ID);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_LOGIN_ID);
    }

    @Test
    void stockInsufficientExceptionShouldUseDefaultErrorCode() {
        StockInsufficientException ex = new StockInsufficientException();
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.STOCK_INSUFFICIENT);
    }

    @Test
    void stockInsufficientExceptionShouldSupportCustomMessage() {
        StockInsufficientException ex = new StockInsufficientException("Product 1: only 3 left");
        assertThat(ex.getMessage()).isEqualTo("Product 1: only 3 left");
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.STOCK_INSUFFICIENT);
    }

    @Test
    void invalidStateExceptionShouldExtendBusinessException() {
        InvalidStateException ex = new InvalidStateException(ErrorCode.INVALID_ORDER_STATE);
        assertThat(ex).isInstanceOf(BusinessException.class);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_ORDER_STATE);
    }
}
