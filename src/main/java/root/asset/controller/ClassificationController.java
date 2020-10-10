package root.asset.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产分类
 */
@RestController
@RequestMapping(value = "/reportServer/classification")
public class ClassificationController extends RO {

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
        Map<String,Object> map = new HashMap<>();
        map.put("code",pJson.getString("parent_code"));
        Map<String,Object> parentClassification =  getClassificationByCode(map);
        if(parentClassification==null || parentClassification.isEmpty()){
            return ErrorMsg("2000","上级分类不存在，请重新选择！");
        }
        pJson.put("level",Integer.parseInt(String.valueOf(parentClassification.get("level"))) + 1);
        pJson.put("path",parentClassification.get("path") + pJson.getString("parent_code") + "-");

        int id = DbSession.insert("eam_classification.add", pJson);
        return SuccessMsg("新增分类成功", id);
    }


    /**
     * 删除分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/deleteByCode", produces = "text/plain;charset=UTF-8")
    public String deleteByCode(@RequestBody JSONObject pJson){
        Map<String, Object> currentClassification =  getClassificationByCode(pJson);
        if(currentClassification == null || currentClassification.isEmpty()){
            return ErrorMsg("2000","数据不存在或已删除");
        }
        DbSession.delete("eam_classification.deleteByCode", currentClassification);
        return SuccessMsg("删除成功", "");
    }

    /**
     * 更新分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/updateByCode", produces = "text/plain;charset=UTF-8")
    public String updateByCode(@RequestBody JSONObject pJson){
        int id = DbSession.update("eam_classification.updateByCode", pJson);
        return SuccessMsg("", id);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByCode", produces = "text/plain;charset=UTF-8")
    public String getByCode(@RequestBody JSONObject pJson){
        Map<String, Object> classification = getClassificationByCode(pJson);
        return SuccessMsg("", classification);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByParentCode", produces = "text/plain;charset=UTF-8")
    public String getByParentCode(@RequestBody JSONObject pJson){
        List<Map<String, Object>> classificationList = DbSession.selectList("eam_classification.getByParentCode", pJson);
        return SuccessMsg("", classificationList);
    }

    /**
     * 获取分类信息 通过关键字
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getByKeyword", produces = "text/plain;charset=UTF-8")
    public String getByKeyword(@RequestBody JSONObject pJson){
        List<Map<String, Object>> classificationList = DbSession.selectList("eam_classification.getByKeyword", pJson);
        return SuccessMsg("", classificationList);

    }


    /**
     * 获取分类下的所有子分类 递归所有 以列表形式返回
     * @param pJson
     * @return
     */
    @RequestMapping(value = "/getAllChildrenListByCode", produces = "text/plain;charset=UTF-8")
    public String getAllChildrenListByCode(@RequestBody JSONObject pJson)  {
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String, Object> currentClassification =  getClassificationByCode(pJson);
        if(currentClassification!=null &&  0 < currentClassification.size()){
            dataList.add(currentClassification);
            List<Map<String, Object>> classificationList = DbSession.selectList("eam_classification.getByPath", currentClassification);
            if(classificationList != null && 0 < classificationList.size()){
                dataList.addAll(classificationList);
            }
        }

//        List<Map<String,Object>> dataList = new ArrayList<>();
//        Map<String, Object> currentClassification =  getClassificationByCode(pJson);
//        if(currentClassification!=null &&  0 < currentClassification.size()){
//            dataList.add(currentClassification);
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
        List<Map<String, Object>> classificationList = getClassificationByParentCode(pJson);
        if(classificationList!=null && 0 <classificationList.size()){
            for(Map<String,Object> child : classificationList){
                recursion(child);
            }
        }
        return SuccessMsg("", classificationList);
    }

    /**
     * 检查code是否存在
     * @param pJson
     * @return
     */
    private boolean hasCode(JSONObject pJson){
        Map<String, Object> map = getClassificationByCode(pJson);
        return map!=null && !map.isEmpty();
    }

    /**
     * 检查是否有子元素
     * @param pJson
     * @return
     */
    private boolean hasChildren(JSONObject pJson){
        List<Map<String, Object>> classificationList =  getClassificationByParentCode(pJson);
        return classificationList!=null && 0 < classificationList.size();
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    private Map<String, Object>  getClassificationByCode(Map<String,Object> pJson){
        return DbSession.selectOne("eam_classification.getByCode", pJson);
    }

    /**
     * 获取分类信息
     * @param pJson
     * @return
     */
    public List<Map<String, Object>> getClassificationByParentCode(Map<String,Object> pJson){
        return DbSession.selectList("eam_classification.getByParentCode", pJson);
    }

    /**
     * 递归 - 层级形式
     * @param parentMap
     */
    private void recursion(Map<String,Object> parentMap){
        HashMap<String,Object> tempMap = new HashMap<>();
        tempMap.put("parent_code",parentMap.get("code"));
        List<Map<String,Object>>  classificationList =  getClassificationByParentCode(tempMap);
        if(classificationList != null && 0 < classificationList.size()){
            parentMap.put("children",classificationList);
            for(Map<String,Object> child : classificationList){
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
        List<Map<String,Object>>  classificationList =  getClassificationByParentCode(tempMap);
        if(classificationList != null && 0 < classificationList.size()){
            dataList.addAll(classificationList);
            for(Map<String,Object> child : classificationList){
                recursionList(dataList,child);
            }
        }
    }

}
