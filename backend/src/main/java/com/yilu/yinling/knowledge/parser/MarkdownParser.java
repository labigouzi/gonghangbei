package com.yilu.yinling.knowledge.parser;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
@Component public class MarkdownParser implements DocumentParser {
 public boolean supports(String n,String t){return n!=null&&n.toLowerCase().endsWith(".md");}
 public String parse(MultipartFile f){try{return new String(f.getBytes(), StandardCharsets.UTF_8);}catch(Exception e){throw new IllegalArgumentException("Markdown文件读取失败",e);}}
}
