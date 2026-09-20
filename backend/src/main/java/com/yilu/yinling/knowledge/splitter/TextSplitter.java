package com.yilu.yinling.knowledge.splitter;
import org.springframework.stereotype.Component; import java.util.*;
@Component public class TextSplitter {
 public List<String> split(String text){List<String> out=new ArrayList<>(); if(text==null)return out; String normalized=text.replace("\r\n","\n").trim(); for(int i=0;i<normalized.length();i+=800) out.add(normalized.substring(i,Math.min(i+800,normalized.length()))); return out.stream().filter(s->!s.isBlank()).toList();}
}
