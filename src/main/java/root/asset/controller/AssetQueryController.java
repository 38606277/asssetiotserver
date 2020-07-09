package root.asset.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.db.DbFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 资产控制器
 */
@RestController
@RequestMapping(value = "/reportServer/assetquery")
public class AssetQueryController extends RO {

    @RequestMapping(value = "/getAssetNum", produces = "text/plain;charset=UTF-8")
    public String getAssetNum(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object>  resutlMap = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset_query.getAssetNum", pJson);
        return SuccessMsg("", resutlMap);
    }

    @RequestMapping(value = "/getAssetNumByCity", produces = "text/plain;charset=UTF-8")
    public String getAssetNumByCity(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {

        List<Map<String, Object>> resutlList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset_query.getAssetNumByCity", pJson);
        return SuccessMsg("", resutlList);
    }

    @RequestMapping(value = "/getAssetCube", produces = "text/plain;charset=UTF-8")
    public String getAssetCube(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>>  resutlList =new DbSession().selectList("eam_asset_query.getAssetCube", pJson);
        return SuccessMsg("", resutlList);
    }

    @RequestMapping(value = "/getAsset", produces = "text/plain;charset=UTF-8")
    public String getAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> result = new DbSession().selectList("eam_asset_query.getAssetCube",pJson);
        return SuccessMsg("", result);
    }

    @RequestMapping(value = "/getAssetAlarm", produces = "text/plain;charset=UTF-8")
    public String getAssetAlarm(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> resutlList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset_query.getAssetAlarm", pJson);
        return SuccessMsg("", resutlList);
    }



}
