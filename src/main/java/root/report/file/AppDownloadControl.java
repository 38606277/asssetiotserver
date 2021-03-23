package root.report.file;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.service.UploadService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@RestController
@RequestMapping("/reportServer/downloadApp")
public class AppDownloadControl {

    private static final Logger log = Logger.getLogger(AppDownloadControl.class);

    @Value("${apkDirPath}")
    private String fileDirPath;

    @Autowired
    private UploadService uploadService;

    @RequestMapping(value = "/android", produces = "text/plain;charset=UTF-8")
    public void downloadFile( String filePath, HttpServletResponse response) throws Exception {
        try {
            if (filePath == null) {
                log.error("文件名不能为空");
                throw new Exception("文件名不能为空");
            }
            String fileName=filePath.substring(filePath.lastIndexOf("/")+1,filePath.length());
            //判断文件是否存在
            File file = new File(fileDirPath,filePath);
            log.error("文件路径：" + fileDirPath +"----" + filePath);
            if(file.exists()){
                //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
           /* String realPath = request.getServletContdownLoadAssetImgext().getRealPath(
                    "//WEB-INF//");*/
                /*File file = new File(realPath, fileName);*/

                response.addHeader("Content-Length", "" + file.length());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/octet-stream");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}




