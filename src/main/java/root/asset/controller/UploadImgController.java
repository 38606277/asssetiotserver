package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;

@RestController
//@RequestMapping(value = "/reportServer/gateway")
@RequestMapping(value = "/reportServer/uploadAssetImg")
public class UploadImgController {
    @Value("${fileDirPath}")
    private String fileDirPath;

    @RequestMapping(value="/uploadAssetImg",produces = "text/plain;charset=UTF-8")
    public String uploadAssetImg(@RequestParam("file") MultipartFile file) {
        JSONObject jsonObject = new JSONObject();

        if (file.isEmpty()) {
            jsonObject.put("resultCode","2000");
            jsonObject.put("message","上传失败，请选择文件");

            return JSON.toJSONString(jsonObject);
        }
        String fileName = System.currentTimeMillis() + "_" +  file.getOriginalFilename();
        File dest = new File(fileDirPath + fileName);
        try {
            file.transferTo(dest);
            jsonObject.put("resultCode", "1000");
            jsonObject.put("message", "上传成功");
            jsonObject.put("data", new HashMap<String, String>(){{
                put("fileName", fileName);
            }});
            System.out.println("文件路径:"  + dest.getPath());
        } catch (IOException e) {
            jsonObject.put("resultCode","2000");
            jsonObject.put("message","上传失败");
        }
        return JSON.toJSONString(jsonObject);
    }

    //文件下载相关代码
    @RequestMapping(value = "/downloadAssetImg", produces = "text/plain;charset=UTF-8")
    public String downloadAssetImg(String fileName, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        //String fileName = "123.JPG";
        System.out.println("the file is : "+fileName);
        String fileUrl = fileDirPath + fileName;
        if (fileDirPath != null&& fileName!= null && 0<fileName.length()) {
            //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
           /* String realPath = request.getServletContdownLoadAssetImgext().getRealPath(
                    "//WEB-INF//");*/
            /*File file = new File(realPath, fileName);*/
            File file = new File(fileUrl);
            if (file.exists()) {
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/force-download");
                response.setHeader("Content-Disposition", "attachment;fileName=" +   URLEncoder.encode(fileName,"UTF-8"));
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }
}
