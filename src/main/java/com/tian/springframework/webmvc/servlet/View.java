package com.tian.springframework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: tian
 * @Date: 2020/4/10 2:21
 * @Desc: 通过render()方法执行视图真正的渲染处理。
 */
public class View {

    private File viewFile;

    public View() {
    }

    public View(File viewFile) {
        this.viewFile = viewFile;
    }

    public void reader(HttpServletRequest request, HttpServletResponse response, Map<String,?> model) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        RandomAccessFile randomAccessFile = new RandomAccessFile(viewFile,"r");
        String line = null;

        while (null != (line = randomAccessFile.readLine() )){
            line = new String(line.getBytes("iso-8859-1"),"utf-8");

            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()){
                String paramName = matcher.group();
                //把 ￥{} 给替换掉，从model中根据key拿到值
                paramName = paramName.replaceAll("￥\\{|\\}", "");
                Object paramValue = model.get(paramName);
                if(paramValue == null){ continue; }
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);

            }

            stringBuffer.append(line);
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(stringBuffer.toString());
    }

    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
