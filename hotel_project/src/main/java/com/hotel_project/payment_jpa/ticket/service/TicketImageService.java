// TicketImageService.java
package com.hotel_project.payment_jpa.ticket.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketImageService {

    private String getTicketUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads" + File.separator + "ticket";
    }

    /**
     * 티켓 이미지 업로드 (프론트에서 캡처한 이미지)
     */
    public String uploadTicketImage(MultipartFile file, String barcode)
            throws CommonExceptionTemplate {
        try {
            if (file == null || file.isEmpty()) {
                throw new CommonExceptionTemplate(400, "티켓 이미지 파일이 없습니다");
            }

            // 업로드 폴더 생성
            File uploadDir = new File(getTicketUploadPath());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = "ticket_" + barcode + extension;

            File destinationFile = new File(uploadDir, fileName);

            // 파일 저장
            file.transferTo(destinationFile);

            log.info("✅ 티켓 이미지 업로드 완료: {}", fileName);
            return "/ticket/" + fileName;

        } catch (IOException e) {
            log.error("❌ 티켓 이미지 업로드 실패", e);
            throw new CommonExceptionTemplate(500, "티켓 이미지 업로드에 실패했습니다");
        }
    }

    /**
     * 티켓 이미지 삭제
     */
    public void deleteTicketImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return;
            }

            String uploadBasePath = getTicketUploadPath();
            File file = new File(uploadBasePath + imagePath.replace("/ticket/", File.separator));

            if (file.exists()) {
                if (file.delete()) {
                    log.info("✅ 티켓 이미지 삭제 완료: {}", imagePath);
                } else {
                    log.warn("⚠️ 티켓 이미지 삭제 실패: {}", imagePath);
                }
            }
        } catch (Exception e) {
            log.error("❌ 티켓 이미지 삭제 중 오류", e);
        }
    }

    /**
     * 티켓 이미지 경로 가져오기
     */
    public String getTicketImagePath(String barcode) {
        File uploadDir = new File(getTicketUploadPath());
        File[] files = uploadDir.listFiles((dir, name) -> name.startsWith("ticket_" + barcode));

        if (files != null && files.length > 0) {
            return "/ticket/" + files[0].getName();
        }

        return null;
    }
}