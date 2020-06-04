package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.RO;
import root.report.db.DbFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域信息
 */
@RestController
@RequestMapping(value = "/reportServer/area")
public class AreaController extends RO {

    /**
     * 获取区域信息
     * @return
     */
    @RequestMapping(value="/getArea",produces = "text/plain;charset=UTF-8")
    public String getArea(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        String parentCode = pJson.getString("parentCode");
        String maxLevel = pJson.getString("maxLevel");
        List<Map<String,Object>> areaList = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getTaskIdByParentCode",parentCode);

        List<Map<String,Object>> dataList = new ArrayList<>();
        for (Map areaData : areaList) {
            Map data = new HashMap();
            data.put("label", areaData.get("name").toString());
            data.put("value", areaData.get("code").toString());
            data.put("isLeaf", areaData.get("level_type").toString().equals(maxLevel));
            dataList.add(data);
        }
        return JSON.toJSONString(dataList);
    }

}
