package com.yilu.yinling.knowledge.parser;
import org.apache.pdfbox.Loader; import org.apache.pdfbox.text.PDFTextStripper; import org.springframework.stereotype.Component; import org.springframework.web.multipart.MultipartFile;
@Component public class PdfParser implements DocumentParser {
 public boolean supports(String n,String t){return n!=null&&n.toLowerCase().endsWith(".pdf");}
 public String parse(MultipartFile f){try(var doc=Loader.loadPDF(f.getBytes())){return new PDFTextStripper().getText(doc);}catch(Exception e){throw new IllegalArgumentException("PDF文件解析失败",e);}}
}
