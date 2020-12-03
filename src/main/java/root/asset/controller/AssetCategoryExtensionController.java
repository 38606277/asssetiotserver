package root.asset.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.x.json.JsonArray;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.util.StringUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产分类扩展
 */
@RestController
@RequestMapping(value = "/reportServer/assetCategoryExtension")
public class AssetCategoryExtensionController extends RO {

    /**
     * 添加分类扩展信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/add", produces = "text/plain;charset=UTF-8")
    public String add(@RequestBody JSONObject pJson){
        JSONArray parentCodeList =  pJson.getJSONArray("parent_code_list");
        if(parentCodeList == null  || parentCodeList.isEmpty()){
            return ErrorMsg("2000","请选择应用范围(分类)");
        }
        if(StringUtil.isBlank(StringUtil.formatNull(pJson.get("attribute_name")))){
            return ErrorMsg("2000","资产扩展信息名称不能为空");
        }

        if(parentCodeList.contains("0")){
            Map<String,Object> params = new HashMap<>();
            params.put("attribute_name",pJson.get("attribute_name"));
            params.put("is_required",pJson.get("is_required"));
            params.put("category_id","-1");
            params.put("scope","1");
            DbSession.insert("eam_asset_category_extension.add",params);
        }else{
            Map<String,Object> params = new HashMap<>();
            params.put("attribute_name",pJson.get("attribute_name"));
            params.put("is_required",pJson.get("is_required"));
            params.put("scope","0");

            for(int i = 0; i < parentCodeList.size(); i++ ){
                String parentCoed = parentCodeList.getString(i);
                params.put("category_id",parentCoed);
                DbSession.insert("eam_asset_category_extension.add",params);
            }
        }

        return SuccessMsg("新增成功", "");
    }


    /**
     * 删除分类扩展信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/deleteById", produces = "text/plain;charset=UTF-8")
    public String deleteById(@RequestBody JSONObject pJson){
        int id = DbSession.delete("eam_asset_category_extension.deleteById",pJson);
        return SuccessMsg("删除成功", id);
    }



    /**
     * 删除分类扩展信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/deleteByIds", produces = "text/plain;charset=UTF-8")
    public String deleteByIds(@RequestBody JSONObject pJson){
        JSONArray dataList =  pJson.getJSONArray("dataList");
        if(dataList==null|| dataList.isEmpty()){
            return ErrorMsg("2000","删除项不能为空");
        }
        for(int i = 0 ; i < dataList.size(); i ++){
            DbSession.delete("eam_asset_category_extension.deleteById",dataList.getJSONObject(i));
        }
        return SuccessMsg("删除成功", "");
    }

    /**
     * 更新分类扩展信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/updateById", produces = "text/plain;charset=UTF-8")
    public String updateById(@RequestBody JSONObject pJson){
        int id = DbSession.update("eam_asset_category_extension.updateById",pJson);
        return SuccessMsg("编辑成功", id);
    }

    /**
     * 获取分类扩展信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getById", produces = "text/plain;charset=UTF-8")
    public String getById(@RequestBody JSONObject pJson){
        Map<String,Object> categoryExtension = DbSession.selectOne("eam_asset_category_extension.getById",pJson);
        return SuccessMsg("", categoryExtension);
    }



    /**
     * 获取分类扩展信息 通过关键字
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByKeyword", produces = "text/plain;charset=UTF-8")
    public String getByKeyword(@RequestBody JSONObject pJson){
        List<Map<String, Object>> categoryExtensionList = DbSession.selectList("eam_asset_category_extension.getByKeyword", pJson);
        return SuccessMsg("", categoryExtensionList);

    }

}
