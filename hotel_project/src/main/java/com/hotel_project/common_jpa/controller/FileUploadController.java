package com.hotel_project.common_jpa.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class FileUploadController {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // 프로젝트 루트의 uploads 폴더를 절대 경로로 계산
    private String getUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads";
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) throws CommonExceptionTemplate {

        if (file == null || file.isEmpty()) {
            throw new CommonExceptionTemplate(400, "파일이 비어있습니다.");
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CommonExceptionTemplate(400, "파일 크기가 5MB를 초과할 수 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !isAllowedFileType(originalFileName)) {
            throw new CommonExceptionTemplate(400, "허용되지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
        }

        try {
            // 절대 경로 사용
            String uploadBasePath = getUploadPath();
            String targetPath = uploadBasePath + File.separator + folder;

            System.out.println("프로젝트 루트: " + System.getProperty("user.dir"));
            System.out.println("업로드 경로: " + targetPath);

            File uploadDir = new File(targetPath);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                System.out.println("디렉토리 생성 " + (created ? "성공" : "실패") + ": " + targetPath);
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            File destinationFile = new File(targetPath + File.separator + fileName);

            System.out.println("파일 저장 위치: " + destinationFile.getAbsolutePath());

            file.transferTo(destinationFile);

            Map<String, String> result = new HashMap<>();
            result.put("filePath", "/" + folder + "/" + fileName);
            result.put("originalName", originalFileName);
            result.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(ApiResponse.success(200, "Upload successful", result));

        } catch (IOException e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
            e.printStackTrace();
            throw new CommonExceptionTemplate(500, "파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    private boolean isAllowedFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") ||
                extension.equals("png") || extension.equals("gif");
    }
}