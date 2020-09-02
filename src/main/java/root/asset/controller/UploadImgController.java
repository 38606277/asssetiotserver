package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.report.util.UUIDUtil;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

@RestController
//@RequestMapping(value = "/reportServer/gateway")
@RequestMapping(value = "/reportServer/uploadAssetImg")
public class UploadImgController {

    private static final String THUMBNAIL_PREFIX = "thumbnail_";//缩略图前缀

    @Value("${fileDirPath}")
    private String fileDirPath;

    @Value("${thumbnailBoundsSize}")
    private int thumbnailBoundsSize;//缩略图大小 宽高 等比缩放

    @Value("${thumbnailFileSize}")
    private int thumbnailFileSize;//缩略图文件大小 单位KB



    @RequestMapping(value="/uploadAssetImg",produces = "text/plain;charset=UTF-8")
    public String uploadAssetImg(@RequestParam("file") MultipartFile file)  {
        JSONObject jsonObject = new JSONObject();

        if (file.isEmpty()) {
            jsonObject.put("resultCode","2000");
            jsonObject.put("message","上传失败，请选择文件");

            return JSON.toJSONString(jsonObject);
        }
        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        String prefix = System.currentTimeMillis() + "_" + UUIDUtil.getUid();
        String newFileName =  prefix + suffix;

        File dest = new File(fileDirPath + newFileName);
        try {

            //保存文件
            //使用此方法保存必须要绝对路径且文件夹必须已存在,否则报错
            file.transferTo(dest);

            //生成缩略图
            buildThumbnailImage(dest);

            jsonObject.put("resultCode", "1000");
            jsonObject.put("message", "上传成功");
            jsonObject.put("data", new HashMap<String, String>(){{
                put("fileName", newFileName);
            }});
            System.out.println("文件路径:"  + dest.getPath());
        } catch (IOException e) {
            jsonObject.put("resultCode","2000");
            jsonObject.put("message","上传失败");
        }
        return JSON.toJSONString(jsonObject);
    }

    /**
     * 构建缩略图 先压宽高 再压文件大小
     * @param sourceFile
     * @throws IOException
     */
   private String buildThumbnailImage(File sourceFile) throws IOException {
       String sourceFilePath = sourceFile.getPath();
       String thumbnailFileName = THUMBNAIL_PREFIX + sourceFile.getName();
       String thumbnailFilePath  = fileDirPath + thumbnailFileName;

       String tempFilePath;//缩放后的文件

       if(0 < thumbnailBoundsSize ) {
           //缩放压缩
           BufferedImage sourcesBufferedImage  = ImageIO.read(sourceFile);;
           float height = sourcesBufferedImage.getHeight();
           float width = sourcesBufferedImage.getWidth();
           float bounds = Math.max(height,width);
           float scale = thumbnailBoundsSize / bounds;
           Thumbnails.of(sourceFile).scale(scale).toFile(thumbnailFilePath);
           tempFilePath = thumbnailFilePath;
       }else{
           tempFilePath = sourceFilePath;
       }


       if(0 < thumbnailFileSize){
           File tempFile = new File(tempFilePath);
           //图片尺寸不变，压缩图片文件大小outputQuality实现,参数1为最高质量
           long size = tempFile.length();
           double outputQuality = 1.0d ;//压缩比例值
           if(size >= thumbnailFileSize * 1024){
               outputQuality = (thumbnailFileSize * 1024f) / size;
               //压缩图片并保存
               Thumbnails.of(tempFile).scale(1).outputQuality(outputQuality).toFile(thumbnailFilePath);
           }
       }else{
           Thumbnails.of(tempFilePath).scale(1).toFile(thumbnailFilePath);
       }

       return thumbnailFileName;
    }


    //文件下载相关代码
    @RequestMapping(value = "/downloadAssetImg", produces = "text/plain;charset=UTF-8")
    public String downloadAssetImg(String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(fileName == null || fileName.length() == 0){
            System.err.println("downloadAssetImg fileName is empty");
            return null;
        }

        if(fileDirPath == null || fileDirPath.length() ==0){
            System.err.println("downloadAssetImg fileDirPath is empty");
            return null;
        }

        String fileUrl = fileDirPath + fileName;
        File file = new File(fileUrl);
        //判断文件是否存在
        if (!file.exists()) {
            //不存在
            if(fileName.startsWith("thumbnail_")){//是否为缩略图
                //判断原图是否存在
                String sourceFileUrl = fileDirPath + fileName.substring(THUMBNAIL_PREFIX.length());
                File sourceFile = new File(sourceFileUrl);
                if(sourceFile.exists()){//原图存在则创建一个压缩图
                   String thumbnailImageName = buildThumbnailImage(sourceFile);
                   file = new File(fileDirPath + thumbnailImageName);
                }
            }
        }
        if(file.exists()){
               //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
           /* String realPath = request.getServletContdownLoadAssetImgext().getRealPath(
                    "//WEB-INF//");*/
               /*File file = new File(realPath, fileName);*/

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

        return null;
    }
}
