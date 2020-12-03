package root.asset.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产分类
 */
@RestController
@RequestMapping(value = "/reportServer/assetCategory")
public class AssetCategoryController extends RO {

    /**
     * 添加分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/add", produces = "text/plain;charset=UTF-8")
    public String add(@RequestBody JSONObject pJson){

        if(hasCode(pJson)){//判断编号是否存在
            return ErrorMsg("2000","分类编码已存在，请重新输入!");
        }

        String parentCode = pJson.getString("parent_code");

        //表示根节点
        if("0".equals(parentCode)){
            pJson.put("path","-");
        }else{
            Map<String,Object> map = new HashMap<>();
            map.put("code",pJson.getString("parent_code"));
            Map<String,Object> parentCategory =  getCategoryByCode(map);
            if(parentCategory==null || parentCategory.isEmpty()){
                return ErrorMsg("2000","上级分类不存在，请重新选择！");
            }
            pJson.put("path",parentCategory.get("path") + pJson.getString("parent_code") + "-");
        }

        int id = DbSession.insert("eam_asset_category.add", pJson);
        return SuccessMsg("新增分类成功", id);
    }


    /**
     * 删除分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/deleteByCode", produces = "text/plain;charset=UTF-8")
    public String deleteByCode(@RequestBody JSONObject pJson){
        Map<String, Object> currentCategory =  getCategoryByCode(pJson);
        if(currentCategory == null || currentCategory.isEmpty()){
            return ErrorMsg("2000","数据不存在或已删除");
        }
        DbSession.delete("eam_asset_category.deleteByCode", currentCategory);
        return SuccessMsg("删除成功", "");
    }

    /**
     * 更新分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/updateById", produces = "text/plain;charset=UTF-8")
    public String updateById(@RequestBody JSONObject pJson){

        String code = pJson.getString("code");
        if(StringUtil.isBlank(code)){
            return ErrorMsg("2000","分类编码不能为空");
        }

        if(!code.equals(pJson.getString("old_code"))){
            if(hasCode(pJson)){//判断编号是否存在
                return ErrorMsg("2000","分类编码已存在，请重新输入");
            }
        }


        Map<String, Object> currentCategoryMap = new HashMap<>();
        currentCategoryMap.put("code",pJson.getString("old_code"));
        currentCategoryMap = getCategoryByCode(currentCategoryMap);


        String newParentCode = pJson.getString("parent_code");

        //表示根节点
        if("0".equals(newParentCode)){
            pJson.put("path","-");
        }else{
            Map<String,Object> map = new HashMap<>();
            map.put("code",newParentCode);
            Map<String,Object> parentCategory =  getCategoryByCode(map);
            if(parentCategory==null || parentCategory.isEmpty()){
                return ErrorMsg("2000","上级分类不存在，请重新选择");
            }
            pJson.put("path",parentCategory.get("path") + pJson.getString("parent_code") + "-");
        }

        int id = DbSession.update("eam_asset_category.updateById", pJson);

        //更新路径
        Map<String,Object> updatePathMap = new HashMap<>();
        updatePathMap.put("old_path",String.valueOf(currentCategoryMap.get("path")) + String.valueOf(currentCategoryMap.get("code")) + "-");
        updatePathMap.put("new_path",String.valueOf(pJson.get("path")) + String.valueOf(pJson.get("code")) + "-");
        DbSession.update("eam_asset_category.updatePath",updatePathMap);

        //更新字节点的parent_code
        Map<String,Object> updateByParentCode = new HashMap<>();
        updateByParentCode.put("parent_code",String.valueOf(currentCategoryMap.get("code")));
        updateByParentCode.put("new_parent_code",String.valueOf(pJson.get("code")));
        DbSession.update("eam_asset_category.updateByParentCode",updateByParentCode);

        return SuccessMsg("编辑分类成功", id);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByCode", produces = "text/plain;charset=UTF-8")
    public String getByCode(@RequestBody JSONObject pJson){
        Map<String, Object> Category = getCategoryByCode(pJson);
        return SuccessMsg("", Category);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByParentCode", produces = "text/plain;charset=UTF-8")
    public String getByParentCode(@RequestBody JSONObject pJson){
        List<Map<String, Object>> CategoryList = DbSession.selectList("eam_asset_category.getByParentCode", pJson);
        return SuccessMsg("", CategoryList);
    }

    /**
     * 获取分类信息 通过关键字
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByKeyword", produces = "text/plain;charset=UTF-8")
    public String getByKeyword(@RequestBody JSONObject pJson){
        List<Map<String, Object>> CategoryList = DbSession.selectList("eam_asset_category.getByKeyword", pJson);
        return SuccessMsg("", CategoryList);

    }


    /**
     * 获取分类下的所有子分类 递归所有 以列表形式返回
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getAllChildrenListByCode", produces = "text/plain;charset=UTF-8")
    public String getAllChildrenListByCode(@RequestBody JSONObject pJson)  {
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String, Object> currentCategory =  getCategoryByCode(pJson);
        if(currentCategory!=null &&  0 < currentCategory.size()){
            dataList.add(currentCategory);
            List<Map<String, Object>> CategoryList = DbSession.selectList("eam_asset_category.getByPath", currentCategory);
            if(CategoryList != null && 0 < CategoryList.size()){
                dataList.addAll(CategoryList);
            }
        }

//        List<Map<String,Object>> dataList = new ArrayList<>();
//        Map<String, Object> currentCategory =  getCategoryByCode(pJson);
//        if(currentCategory!=null &&  0 < currentCategory.size()){
//            dataList.add(currentCategory);
//        }
//        recursionList(dataList,pJson);
        return SuccessMsg("", dataList);
    }

    /**
     * 获取分类下的所有子分类 递归所有 以递归层级形式返回
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getAllChildrenRecursionByCode", produces = "text/plain;charset=UTF-8")
    public String getAllChildrenRecursionByCode(@RequestBody JSONObject pJson)  {
        List<Map<String, Object>> CategoryList = getCategoryByParentCode(pJson);
        if(CategoryList!=null && 0 <CategoryList.size()){
            for(Map<String,Object> child : CategoryList){
                recursion(child);
            }
        }
        return SuccessMsg("", CategoryList);
    }

    /**
     * 检查code是否存在
     * @param pJson
     * @return
     */
    private boolean hasCode(JSONObject pJson){
        Map<String, Object> map = getCategoryByCode(pJson);
        return map!=null && !map.isEmpty();
    }

    /**
     * 检查是否有子元素
     * @param pJson
     * @return
     */
    private boolean hasChildren(JSONObject pJson){
        List<Map<String, Object>> CategoryList =  getCategoryByParentCode(pJson);
        return CategoryList!=null && 0 < CategoryList.size();
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    private Map<String, Object>  getCategoryByCode(Map<String,Object> pJson){
        return DbSession.selectOne("eam_asset_category.getByCode", pJson);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    public List<Map<String, Object>> getCategoryByParentCode(Map<String,Object> pJson){
        return DbSession.selectList("eam_asset_category.getByParentCode", pJson);
    }

    /**
     * 递归 - 层级形式
     * @param parentMap
     */
    private void recursion(Map<String,Object> parentMap){
        HashMap<String,Object> tempMap = new HashMap<>();
        tempMap.put("parent_code",parentMap.get("code"));
        List<Map<String,Object>>  CategoryList =  getCategoryByParentCode(tempMap);
        if(CategoryList != null && 0 < CategoryList.size()){
            parentMap.put("children",CategoryList);
            for(Map<String,Object> child : CategoryList){
                recursion(child);
            }
        }
    }

    /**
     * 递归 - 列表形式
     * @param dataList
     * @param parentMap
     */
    private void recursionList(List<Map<String,Object>> dataList,Map<String,Object> parentMap){
        HashMap<String,Object> tempMap = new HashMap<>();
        tempMap.put("parent_code",parentMap.get("code"));
        List<Map<String,Object>>  CategoryList =  getCategoryByParentCode(tempMap);
        if(CategoryList != null && 0 < CategoryList.size()){
            dataList.addAll(CategoryList);
            for(Map<String,Object> child : CategoryList){
                recursionList(dataList,child);
            }
        }
    }

}
