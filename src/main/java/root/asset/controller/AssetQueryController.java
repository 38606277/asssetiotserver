package root.asset.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
        Map<String, Object>  resultMap = DbSession.selectOne("eam_asset_query.getAssetNum", pJson);

        /**
         * 获取资产原值
         * */
        String assetCost =DbSession.selectOne("eam_asset_query.getAssetCost", null);
        resultMap.put("assetCost",assetCost);
        /**
         * 获取资产条数
         * */
        String assetNumber =DbSession.selectOne("eam_asset_query.getAssetNumber", null);

        resultMap.put("assetNumber",assetNumber);
        return SuccessMsg("", resultMap);
    }

    @RequestMapping(value = "/getAssetNumByCity", produces = "text/plain;charset=UTF-8")
    public String getAssetNumByCity(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {

        List<Map<String, Object>> resutlList = DbSession.selectList("eam_asset_query.getAssetNumByCity", pJson);
        return SuccessMsg("", resutlList);
    }

    @RequestMapping(value = "/getAssetCube", produces = "text/plain;charset=UTF-8")
    public String getAssetCube(@RequestBody JSONObject pJson)  {
        List<Object> r =DbSession.selectList("eam_asset_query.getAssetCube", pJson);
        return SuccessMsg("", r);
    }

    @RequestMapping(value = "/getAsset", produces = "text/plain;charset=UTF-8")
    public String getAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> result = DbSession.selectList("eam_asset_query.getAssetCube",pJson);
        return SuccessMsg("", result);
    }

    @RequestMapping(value = "/getAssetAlarm", produces = "text/plain;charset=UTF-8")
    public String getAssetAlarm(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> resutlList =DbSession.selectList("eam_asset_query.getAssetAlarm", pJson);
        return SuccessMsg("", resutlList);
    }

    /**
     * 获取资产异常统计
     * */
    @RequestMapping(value = "/getAssetAlarmNum", produces = "text/plain;charset=UTF-8")
    public String getAssetAlarmNum(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object> map=new HashMap<>();
        String gatewayNumber =DbSession.selectOne("eam_asset_query.getGatewayNumber", null);
        String assetAlarmNumber =DbSession.selectOne("eam_asset_query.getAssetAlarmNumber", null);
        String pendAssetAlarmNumber =DbSession.selectOne("eam_asset_query.getPendAssetAlarmNumber", null);
        map.put("gatewayNumber",gatewayNumber);
        map.put("assetAlarmNumber",assetAlarmNumber);
        map.put("pendAssetAlarmNumber",pendAssetAlarmNumber);
        return SuccessMsg("", map);
    }


    /**
     * 获取资产类别统计
     * */
    @RequestMapping(value = "/getAssetTypeNum", produces = "text/plain;charset=UTF-8")
    public String getAssetTypeNum(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object> map=new HashMap<>();
        List<Map<String, Object>> list =DbSession.selectList("eam_asset_query.getAssetTypeNum", null);
        String typeName="",typeNum="";
        for(int i=0;i<list.size();i++){
            typeName = typeName +"," + list.get(i).get("assetclass");
            typeNum = typeNum +"," + list.get(i).get("total");
        }
        if(!"".equals(typeName)){
            typeName = typeName.substring(1);
        }
        if(!"".equals(typeNum)){
            typeNum = typeNum.substring(1);
        }
        map.put("typeName",typeName);
        map.put("typeNum",typeNum);
        return SuccessMsg("", map);
    }
    /**
     * 获取资产类别统计
     * */
    @RequestMapping(value = "/getAssetJZType", produces = "text/plain;charset=UTF-8")
    public String getAssetJZType(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object> map=new HashMap<>();
        List<Map<String, Object>> list =DbSession.selectList("eam_asset_query.getAssetJZType", null);
//        String typeName="",typeNum="";
//        for(int i=0;i<list.size();i++){
//            typeNum = list.get(i).get("total");
//        }
//        map.put("typeNum",list);
        return SuccessMsg("", list);
    }

}
