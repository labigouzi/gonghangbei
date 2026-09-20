package com.yilu.yinling.fraud.controller;

import com.yilu.yinling.common.response.Result;
import com.yilu.yinling.fraud.dto.FraudTextRequest;
import com.yilu.yinling.fraud.service.FraudDetectionService;
import com.yilu.yinling.fraud.vo.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.yilu.yinling.fraud.ocr.OcrClient;
import com.yilu.yinling.fraud.ocr.OcrResult;
import com.yilu.yinling.common.exception.BusinessException;
import com.yilu.yinling.storage.FileStorageService;

@RestController
@org.springframework.context.annotation.Profile("!demo")
@RequestMapping("/api/v1/fraud")
public class FraudDetectionController {
    private final FraudDetectionService service;
    private final OcrClient ocrClient;
    private final FileStorageService fileStorageService;
    public FraudDetectionController(FraudDetectionService service, OcrClient ocrClient, FileStorageService fileStorageService) { this.service = service; this.ocrClient = ocrClient; this.fileStorageService = fileStorageService; }
    private Long userId(Authentication auth) { return (Long) auth.getDetails(); }
    @PostMapping("/detect/text")
    public Result<FraudResult> detectText(Authentication auth, @Valid @RequestBody FraudTextRequest request) {
        return Result.ok(service.detect(userId(auth), request.content()));
    }
    @GetMapping("/records")
    public Result<List<FraudRecordView>> records(Authentication auth) { return Result.ok(service.records(userId(auth))); }
    @PostMapping("/detect/image")
    public Result<FraudImageResult> detectImage(Authentication auth, @RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) throw new BusinessException("图片不能为空");
        if (file.getSize() > 5 * 1024 * 1024) throw new BusinessException("图片大小不能超过5MB");
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) throw new BusinessException("仅支持图片文件");
        String fileUrl = fileStorageService.upload(file);
        OcrResult ocr = ocrClient.recognize(file);
        return Result.ok(service.detectImage(userId(auth), ocr.content(), fileUrl, file.getOriginalFilename(), file.getSize(), file.getContentType()));
    }
}
