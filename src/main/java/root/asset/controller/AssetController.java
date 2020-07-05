package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import org.apache.ibatis.session.RowBounds;
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
@RequestMapping("/reportServer/asset")
public class AssetController extends RO {

    /**
     * 添加资产
     *
     * @return
     */
    @RequestMapping(value = "/CreateAsset", produces = "text/plain;charset=UTF-8")
    public String CreateAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbFactory.Open(DbFactory.FORM).insert("eam_asset.addAsset", pJson);
            return SuccessMsg("保存成功", pJson.get("id").toString());

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }

    }

    @RequestMapping(value = "/UpdateAsset", produces = "text/plain;charset=UTF-8")
    public String UpdateAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbFactory.Open(DbFactory.FORM).update("eam_asset.updateAsset", pJson);
            return SuccessMsg("保存成功", "");

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    @RequestMapping(value = "/DeleteAsset", produces = "text/plain;charset=UTF-8")
    public String DeleteAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbFactory.Open(DbFactory.FORM).delete("eam_asset.deleteAsset", pJson);
            return SuccessMsg("删除成功", "");

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    /**
     * 删除资产
     *
     * @return
     */
    @RequestMapping(value = "/deleteAsset", produces = "text/plain;charset=UTF-8")
    public String deleteGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int gatewayId = pJson.getIntValue("asset_id");
        int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.queryCountByGatewayId", gatewayId);
        if (0 < count) {
            DbFactory.Open(DbFactory.FORM).insert("eam_asset.rmEamAsset", gatewayId);
        }
        return SuccessMsg("删除成功", "");
    }

    @RequestMapping(value = "/getAssetById", produces = "text/plain;charset=UTF-8")
    public String getAssetById(@RequestBody JSONObject pJson){
        try{
            HashMap<String,Object> map = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.getAssetById",pJson);
            JSONObject jsonObject =(JSONObject) JSON.toJSON(map);
            return  SuccessMsg("",jsonObject);
        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    /**
     * 绑定物联网标签
     *
     * @return
     */
    @RequestMapping(value = "/bindEamTag", produces = "text/plain;charset=UTF-8")
    public String bindTag(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.bindEamTag", pJson);
        return SuccessMsg("绑定成功", "");
    }

    /**
     * 获取资产列表 by GatewayId
     * @return
     */
    @RequestMapping(value = "/listEamAssetByGatewayId", produces = "text/plain;charset=UTF-8")
    public String listEamAssetByGatewayId(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway_asset.queryAssetListByGatewayId", pJson);
        return SuccessMsg("查询成功", assetList);
    }


    /**
     * 获取资产列表 by GatewayId （分页）
     *
     * @return
     */
    @RequestMapping(value = "/listEamAssetPageByGatewayId", produces = "text/plain;charset=UTF-8")
    public String listEamAssetPageByGatewayId(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));
        String gatewayId = pJson.getString("gateway_id");
        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startIndex", currentPage);
        map.put("perPage", perPage);
        map.put("gateway_id", gatewayId);
        List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway_asset.queryAssetListPageByGatewayId", map);
        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway_asset.countAssetByGatewayId", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", gatewayList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }



    /**
     * 获取资产列表
     *
     * @return
     */
    @RequestMapping(value = "/listEamAsset", produces = "text/plain;charset=UTF-8")
    public String listEamAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));
        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startIndex", currentPage);
        map.put("perPage", perPage);
        map.put("keyword",pJson.getString("keyword"));
        List<Map<String, Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.listEamAssetByPage", map);
        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.countEamAssetByPage", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", assetList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }


    /**
     * 获取资产列表 并携带网关编号
     *
     * @return
     */
    @RequestMapping(value = "/listBindingEamAsset", produces = "text/plain;charset=UTF-8")
    public String listBindingEamAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));
        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startIndex", currentPage);
        map.put("perPage", perPage);
        map.put("keyword",pJson.getString("keyword"));
        List<Map<String, Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.listBindingEamAssetByPage", map);
        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.countBindingEamAssetByPage", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", assetList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }



    /**
     * 获取未关联网关的资产列表
     * @return
     */
    @RequestMapping(value = "/getAssetInventory", produces = "text/plain;charset=UTF-8")
    public String getAssetInventory(@RequestBody JSONObject pJson) {

        List<Map> aResult = null;
        Long totalSize = 0L;
        try {
            Map map = new HashMap();
            RowBounds bounds = null;
            if(pJson==null){
                bounds = RowBounds.DEFAULT;
            }else{
                int startIndex = Integer.valueOf(pJson.getString("pageNum"));
                int perPage = Integer.valueOf(pJson.getString("perPage"));
                if(startIndex==1 || startIndex==0){
                    startIndex=0;
                }else{
                    startIndex=(startIndex-1)*perPage;
                }
                bounds = new PageRowBounds(startIndex, perPage);
//                map.put("startIndex",startIndex);
//                map.put("perPage",perPage);
            }

            map.put("cityCode",pJson.getString("cityCode"));
            map.put("receiveTime",pJson.getString("receiveTime"));
            aResult = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.getAssetInventory", map,bounds);
            if(pJson!=null){
                totalSize = ((PageRowBounds)bounds).getTotal();
            }else{
                totalSize = Long.valueOf(aResult.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map maps=new HashMap<>();
        maps.put("list",aResult);
        maps.put("total",totalSize);

//        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
//        int perPage = Integer.valueOf(pJson.getString("perPage"));
//        if (1 == currentPage || 0 == currentPage) {
//            currentPage = 0;
//        } else {
//            currentPage = (currentPage - 1) * perPage;
//        }
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("startIndex", currentPage);
//        map.put("perPage", perPage);
//        List<Map<String, Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset.getAssetInventory", map);
//        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset.countAssetInventory", map);
//        Map<String, Object> map2 = new HashMap<String, Object>();
//        Map<String, Object> map3 = new HashMap<String, Object>();
//        map3.put("list", assetList);
//        map3.put("total", total);
//        map2.put("msg", "查询成功");
//        map2.put("data", map3);
//        map2.put("status", 0);
        return  SuccessMsg("",maps);
    }


    /**
     * 获取未关联网关的资产列表 来自标签号
     * @return
     */
    @RequestMapping(value = "/listTagNoBindGateway", produces = "text/plain;charset=UTF-8")
    public String listTagNoBindGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        int currentPage = Integer.valueOf(pJson.getString("pageNum"));
        int perPage = Integer.valueOf(pJson.getString("perPage"));
        if (1 == currentPage || 0 == currentPage) {
            currentPage = 0;
        } else {
            currentPage = (currentPage - 1) * perPage;
        }
        pJson.put("startIndex", currentPage);
        pJson.put("perPage", perPage);
        List<Map<String, Object>> assetList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset_status.listTagNoBindGateway", pJson);
        int total = DbFactory.Open(DbFactory.FORM).selectOne("eam_asset_status.countTagNoBindGateway", pJson);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", assetList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2,features);
    }


}
