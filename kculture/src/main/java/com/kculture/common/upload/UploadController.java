package com.kculture.common.upload;

// [Claude 작성 - 신규 파일]
// 퀘스트 미션 인증 사진을 서버 로컬 디스크에 저장하고, 다시 접근 가능한 URL을 돌려주는
// 업로드 엔드포인트입니다. QuestProgressService.completeMission()이 이미 photoUrl(문자열)을
// 받는 구조라, 프론트는 여기로 먼저 업로드해서 받은 url을 그대로 completeMission에 넘기면 됩니다.
// 기존 파일은 전혀 건드리지 않았습니다.

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public record UploadResponse(String url) {
    }

    @PostMapping
    public UploadResponse upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 비어있어요.");
        }

        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
                if (ext.length() > 10) {
                    ext = ""; // 이상하게 긴 확장자는 무시
                }
            }
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            file.transferTo(target.toFile());

            // 요청이 들어온 host:port를 그대로 사용해서, 프론트가 다시 접근 가능한 절대 URL을 만든다.
            // (에뮬레이터는 10.0.2.2:8080으로 요청하므로, 그 값 그대로 돌려줘야 다시 접근 가능하다.)
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            return new UploadResponse(baseUrl + "/uploads/" + filename);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사진 저장에 실패했어요: " + e.getMessage());
        }
    }
}
