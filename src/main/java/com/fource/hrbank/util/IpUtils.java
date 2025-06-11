package com.fource.hrbank.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 클라이언트의 실제 Ip 주소를 추출하는 유틸리티 클래스
 */
@Slf4j
@Component
public class IpUtils {

    /**
     * HttpServletRequest에서 클라이언트의 실제 Ip주소를 추출
     * <p>
     * 여러 프록시 환경에서 사용되는 HTTP 헤더를 순차적으로 확인 여러 Ip가 콤마로 구분되어 있는 경우 (예: "192.168.1.1, 10.0.0.1"), 첫 번째
     * IP 주소를 반환
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트의 IP 주소. 추출할 수 없는 경우 {@code request.getRemoteAddr()} 값 반환
     * @throws NullPointerException request가 null인 경우
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 여러 IP가 있는 경우 첫 번째만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * Spring의  {@link RequestContextHolder}를 사용하여 현재 요청의 {@link HttpServletRequest}를 자동으로 가져와 IP
     * 주소를 추출.
     * <p>
     * 이 메서드는 다음과 같은 상황에서 안전하게 사용할 수 있습니다: 웹 요청 처리 중 (Controller, Service, Component 등) Spring Web
     * 황경에서 실행되는 경우
     *
     * @return 클라이언트의 IP 주소. 추출할 수 없는 경우 "unknown" 반환
     */
    public static String getCurrentClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return getClientIp(request);
        } catch (Exception e) {
            // 예외 발생 시 로그만 남기고 기본값 반환
            log.warn("클라이언트 IP 추출 실패, 기본값 사용: {}", e.getMessage());
            return "unknown";
        }
    }
}
