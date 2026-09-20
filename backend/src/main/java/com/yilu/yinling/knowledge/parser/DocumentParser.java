package com.yilu.yinling.knowledge.parser;
import org.springframework.web.multipart.MultipartFile;
public interface DocumentParser { boolean supports(String filename, String contentType); String parse(MultipartFile file); }
