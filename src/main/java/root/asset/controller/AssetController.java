package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.RO;
import root.report.db.DbFactory;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产控制器
 */
@RestController
@RequestMapping(value = "/reportServer/asset")
public class AssetController extends RO {

    /**
     * 添加资产
     * @return
     */
    @RequestMapping(value="/addAsset",produces = "text/plain;charset=UTF-8")
    public String addAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int gatewayId = pJson.getIntValue("asset_id");
        int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.queryCountByGatewayId",gatewayId);
        if(0 < count){
            return ExceptionMsg("资产已添加,请勿重复添加");
        }else{
            DbFactory.Open(DbFactory.FORM).insert("eam_asset.addAsset",pJson);
            return SuccessMsg("保存成功", "");
        }
    }
    /**
     * 删除资产
     * @return
     */
    @RequestMapping(value="/deleteAsset",produces = "text/plain;charset=UTF-8")
    public String deleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
        int gatewayId = pJson.getIntValue("asset_id");
        int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.queryCountByGatewayId",gatewayId);
        if(0 < count){
            DbFactory.Open(DbFactory.FORM).insert("eam_asset.rmEamAsset",gatewayId);
        }
        return SuccessMsg("删除成功", "");
    }

    /**
     * 绑定物联网标签
     * @return
     */
    @RequestMapping(value="/bindEamTag",produces = "text/plain;charset=UTF-8")
    public String bindTag(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
        DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.bindEamTag",pJson);
        return SuccessMsg("绑定成功", "");
    }

    /**
     * 获取资产列表 by GatewayId
     * @return
     */
    @RequestMapping(value="/listEamAssetByGatewayId",produces = "text/plain;charset=UTF-8")
    public String listEamAssetByGatewayId(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
        List<Map<String,Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway_asset.queryAssetListByGatewayId",pJson);
        return SuccessMsg("查询成功",assetList);
    }

    /**
     * 获取资产列表
     * @return
     */
    @RequestMapping(value="/listEamAsset",produces = "text/plain;charset=UTF-8")
    public String listEamAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
        int currentPage=Integer.valueOf(pJson.getString("pageNum"));
        int perPage=Integer.valueOf(pJson.getString("perPage"));
        if(1==currentPage|| 0==currentPage){
            currentPage=0;
        }else{
            currentPage=(currentPage-1)*perPage;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("startIndex", currentPage);
        map.put("perPage",perPage);
        List<Map<String,Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.listEamAssetByPage",map);
        int total=DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.countEamAsset", map);
        Map<String,Object> map2 =new HashMap<String,Object>();
        Map<String,Object> map3 =new HashMap<String,Object>();
        map3.put("list",assetList);
        map3.put("total",total);
        map2.put("msg","查询成功");
        map2.put("data",map3);
        map2.put("status",0);
        return JSON.toJSONString(map2);
    }


    /**
     * 获取资产列表 并携带网关编号
     * @return
     */
    @RequestMapping(value="/listBindingEamAsset",produces = "text/plain;charset=UTF-8")
    public String listBindingEamAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException{
        int currentPage=Integer.valueOf(pJson.getString("pageNum"));
        int perPage=Integer.valueOf(pJson.getString("perPage"));
        if(1==currentPage|| 0==currentPage){
            currentPage=0;
        }else{
            currentPage=(currentPage-1)*perPage;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("startIndex", currentPage);
        map.put("perPage",perPage);
        List<Map<String,Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.listBindingEamAssetByPage",map);
        int total=DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.countEamAsset", map);
        Map<String,Object> map2 =new HashMap<String,Object>();
        Map<String,Object> map3 =new HashMap<String,Object>();
        map3.put("list",assetList);
        map3.put("total",total);
        map2.put("msg","查询成功");
        map2.put("data",map3);
        map2.put("status",0);
        return JSON.toJSONString(map2);
    }
}
