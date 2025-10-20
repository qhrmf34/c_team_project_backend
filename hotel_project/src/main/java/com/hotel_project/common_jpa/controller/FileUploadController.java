package com.hotel_project.common_jpa.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
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

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CommonExceptionTemplate(400, "파일 크기가 5MB를 초과할 수 없습니다.");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !isAllowedFileType(originalFileName)) {
            throw new CommonExceptionTemplate(400, "허용되지 않는 파일 형식입니다. (jpg, jpeg, png, gif만 가능)");
        }

        try {
            String uploadBasePath = getUploadPath();
            String targetPath = uploadBasePath + File.separator + folder;

            File uploadDir = new File(targetPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            File destinationFile = new File(targetPath + File.separator + fileName);

            file.transferTo(destinationFile);

            Map<String, String> result = new HashMap<>();
            result.put("filePath", "/" + folder + "/" + fileName);
            result.put("originalName", originalFileName);
            result.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(ApiResponse.success(200, "Upload successful", result));

        } catch (IOException e) {
            throw new CommonExceptionTemplate(500, "파일 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    // 파일 삭제 API
    @DeleteMapping("/delete-file")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @RequestParam("filePath") String filePath) throws CommonExceptionTemplate {

        if (filePath == null || filePath.isEmpty()) {
            throw new CommonExceptionTemplate(400, "파일 경로가 비어있습니다.");
        }

        try {
            String uploadBasePath = getUploadPath();
            // filePath는 "/city/uuid.jpg" 형식
            File file = new File(uploadBasePath + filePath);

            if (!file.exists()) {
                // 파일이 없어도 성공으로 처리 (이미 삭제됨)
                return ResponseEntity.ok(ApiResponse.success(200, "파일이 존재하지 않거나 이미 삭제되었습니다.", "deleted"));
            }

            // 파일이 uploads 디렉토리 안에 있는지 확인 (보안)
            String canonicalFilePath = file.getCanonicalPath();
            String canonicalUploadPath = new File(uploadBasePath).getCanonicalPath();

            if (!canonicalFilePath.startsWith(canonicalUploadPath)) {
                throw new CommonExceptionTemplate(403, "허용되지 않는 파일 경로입니다.");
            }

            boolean deleted = file.delete();

            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(200, "파일이 삭제되었습니다.", "deleted"));
            } else {
                throw new CommonExceptionTemplate(500, "파일 삭제에 실패했습니다.");
            }

        } catch (IOException e) {
            throw new CommonExceptionTemplate(500, "파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isAllowedFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") ||
                extension.equals("png") || extension.equals("gif");
    }
}