package com.yilu.yinling.knowledge.service;
import com.yilu.yinling.knowledge.vo.KnowledgeUploadResponse; import org.springframework.web.multipart.MultipartFile;
public interface KnowledgeService { KnowledgeUploadResponse upload(MultipartFile file); }
