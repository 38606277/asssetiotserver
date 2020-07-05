package root.asset.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
     *
     * @return
     */
    @RequestMapping(value = "/getArea", produces = "text/plain;charset=UTF-8")
    public String getArea(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        String parentCode = pJson.getString("parentCode");
        String maxLevel = pJson.getString("maxLevel");
        List<Map<String, Object>> areaList = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getTaskIdByParentCode", parentCode);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map areaData : areaList) {
            Map data = new HashMap();
            data.put("label", areaData.get("name").toString());
            data.put("value", areaData.get("code").toString());
            data.put("merger_name", areaData.get("merger_name").toString());
            data.put("level", areaData.get("level_type").toString());
            data.put("isLeaf", areaData.get("level_type").toString().equals(maxLevel));
            dataList.add(data);
        }
        return SuccessMsg("", dataList);
    }


    /**
     * 获取区域信息并判断是否有网关信息
     *
     * @return
     */
    @RequestMapping(value = "/getGatewayArea", produces = "text/plain;charset=UTF-8")
    public String getGatewayArea(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        String parentCode = pJson.getString("parentCode");
        String maxLevel = pJson.getString("maxLevel");
        List<Map<String, Object>> areaList = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getTaskIdByParentCode", parentCode);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map areaData : areaList) {
            Map data = new HashMap();
            data.put("label", areaData.get("name").toString());
            data.put("value", areaData.get("code").toString());
            data.put("level", areaData.get("level_type").toString());
            if (areaData.get("level_type").toString().equals(maxLevel)) {
                String addressId = areaData.get("code").toString();
                int count = DbFactory.Open(DbFactory.FORM).selectOne("eam_gateway.queryCountByAddressId", addressId);
                data.put("isLeaf", count == 0);
            } else {
                data.put("isLeaf", false);
            }

            dataList.add(data);
        }
        return SuccessMsg("", dataList);
    }


    /**
     * 获取含有网关的市
     * @param pJson
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/getCityContainingGateway", produces = "text/plain;charset=UTF-8")
    public String getCityContainingGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> areaList = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getCityContainingGateway", pJson);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map areaData : areaList) {
            Map data = new HashMap();
            data.put("label", areaData.get("name").toString());
            data.put("value", areaData.get("code").toString());
            data.put("level", areaData.get("level_type").toString());
            data.put("isLeaf", false);
            dataList.add(data);
        }


        return SuccessMsg("", dataList);
    }

    /**
     * 获取含有网关的区
     * @param pJson
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/getDistrictContainingGateway", produces = "text/plain;charset=UTF-8")
    public String getDistrictContainingGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> areaList = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getDistrictContainingGateway", pJson);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Map areaData : areaList) {
            Map data = new HashMap();
            data.put("label", areaData.get("name").toString());
            data.put("value", areaData.get("code").toString());
            data.put("level", areaData.get("level_type").toString());
            data.put("isLeaf", false);
            dataList.add(data);
        }
        return SuccessMsg("", dataList);
    }
    @RequestMapping(value = "/getCityByProvince", produces = "text/plain;charset=UTF-8")
    public String getCityByProvince(@RequestHeader("credentials") String credentials, @RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>>  resut = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getCityByProvince", pJson);
        return SuccessMsg("", resut);
    }

    @RequestMapping(value = "/getPostionByCityName", produces = "text/plain;charset=UTF-8")
    public String getPostionByCityName(@RequestHeader("credentials") String credentials, @RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Map<String, Object>>  resut = DbFactory.Open(DbFactory.FORM).selectList("sys_area.getPostionByCityName", pJson);
        return SuccessMsg("", resut);
    }



}
