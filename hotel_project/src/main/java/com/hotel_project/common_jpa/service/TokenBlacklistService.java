package com.hotel_project.common_jpa.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JWT 토큰 블랙리스트 관리 서비스
 * 로그아웃된 토큰들을 메모리에 저장하여 무효화 처리
 */
@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenBlacklistService() {
        // 1시간마다 만료된 토큰들을 정리
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    /**
     * 토큰을 블랙리스트에 추가 (로그아웃 시 사용)
     * @param jwtId JWT ID
     */
    public void addToBlacklist(String jwtId) {
        if (jwtId != null && !jwtId.isEmpty()) {
            blacklistedTokens.add(jwtId);
            System.out.println("토큰이 블랙리스트에 추가되었습니다: " + jwtId);
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param jwtId JWT ID
     * @return 블랙리스트에 있으면 true
     */
    public boolean isBlacklisted(String jwtId) {
        return jwtId != null && blacklistedTokens.contains(jwtId);
    }

    /**
     * 만료된 토큰들을 정리 (메모리 절약)
     * 실제 운영환경에서는 Redis 등을 사용하여 만료 시간을 자동으로 관리하는 것이 좋습니다.
     */
    private void cleanupExpiredTokens() {
        // 여기서는 간단하게 모든 토큰을 24시간 후 삭제
        // 실제로는 각 토큰의 만료 시간을 추적해야 합니다.
        int sizeBefore = blacklistedTokens.size();
        // 현재는 단순하게 크기가 1000을 넘으면 절반을 삭제
        if (sizeBefore > 1000) {
            blacklistedTokens.clear();
            System.out.println("블랙리스트 토큰들이 정리되었습니다.");
        }
    }

    /**
     * 서비스 종료 시 스케줄러 정리
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}