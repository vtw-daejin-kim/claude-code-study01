package kr.go.ecommerce.global.util;

import kr.go.ecommerce.global.exception.BusinessException;
import kr.go.ecommerce.global.exception.ErrorCode;
import kr.go.ecommerce.global.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "No authenticated user");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    }

    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "No authenticated user");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getRole();
    }
}
