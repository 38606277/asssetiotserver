package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.service.AssetService;
import root.report.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 外部接口
 */
@RestController
@RequestMapping("/reportServer/external")
public class ExternalInterfaceController extends RO {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping(value = "/getAssetAddressInfoByAssetTag", produces = "text/plain;charset=UTF-8")
    public String getAssetAddressInfoByAssetTag(@RequestBody JSONObject pJson){
        if(!pJson.containsKey("asset_tag")){
            return  ErrorMsg("2000","参数错误");
        }
        try{
            HashMap<String,Object> map = DbSession.selectOne("eam_asset.getAssetAddressInfoByAssetTag",pJson);
            if(map!=null){
                return  SuccessMsg("获取成功", JSON.toJSON(map));
            }else{
                return  ErrorMsg("2000","未获取到资产地点信息");
            }
        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    /**
     * 添加资产
     * @return
     */
    @RequestMapping(value = "/CreateAsset", produces = "text/plain;charset=UTF-8")
    public String CreateAsset(@RequestBody JSONObject pJson) throws ParseException {
        if(!pJson.containsKey("asset_tag")){
            return  ErrorMsg("2000","参数错误");
        }
        try{
            DbSession.insert("eam_asset.addAsset", pJson);
            return SuccessMsg("保存成功", pJson.get("id").toString());

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }


}
