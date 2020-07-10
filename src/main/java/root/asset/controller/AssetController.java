package root.asset.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.db.DbFactory;
import root.report.service.AssetService;
import root.report.util.ReadExcel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产控制器
 */
@RestController
@RequestMapping("/reportServer/asset")
public class AssetController extends RO {

    SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    AssetService assetService;
    /**
     * 添加资产
     *
     * @return
     */
    @RequestMapping(value = "/CreateAsset", produces = "text/plain;charset=UTF-8")
    public String CreateAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbSession.insert("eam_asset.addAsset", pJson);
            return SuccessMsg("保存成功", pJson.get("id").toString());

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }

    }

    @RequestMapping(value = "/UpdateAsset", produces = "text/plain;charset=UTF-8")
    public String UpdateAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbSession.update("eam_asset.updateAsset", pJson);
            return SuccessMsg("保存成功", "");

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    @RequestMapping(value = "/DeleteAsset", produces = "text/plain;charset=UTF-8")
    public String DeleteAsset(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        try{
            DbSession.delete("eam_asset.deleteAsset", pJson);
            return SuccessMsg("删除成功", "");

        }catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

    @RequestMapping(value = "/getAssetById", produces = "text/plain;charset=UTF-8")
    public String getAssetById(@RequestBody JSONObject pJson){
        try{
            HashMap<String,Object> map = DbSession.selectOne("eam_asset.getAssetById",pJson);
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
        DbSession.selectOne("eam_asset.bindEamTag", pJson);
        return SuccessMsg("绑定成功", "");
    }

    /**
     * 获取资产列表 by GatewayId
     * @return
     */
    @RequestMapping(value = "/listEamAssetByGatewayId", produces = "text/plain;charset=UTF-8")
    public String listEamAssetByGatewayId(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
        List<Map<String, Object>> assetList = DbSession.selectList("eam_gateway_asset.queryAssetListByGatewayId", pJson);
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
        List<Map<String, Object>> gatewayList = DbSession.selectList("eam_gateway_asset.queryAssetListPageByGatewayId", map);
        int total = DbSession.selectOne("eam_gateway_asset.countAssetByGatewayId", map);
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
        List<Map<String, Object>> assetList = DbSession.selectList("eam_asset.listEamAssetByPage", map);
        int total = DbSession.selectOne("eam_asset.countEamAssetByPage", map);
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
        List<Map<String, Object>> assetList = DbSession.selectList("eam_asset.listBindingEamAssetByPage", map);
        int total = DbSession.selectOne("eam_asset.countBindingEamAssetByPage", map);
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
            aResult =new DbSession().selectList("eam_asset.getAssetInventory", map,bounds);
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
//        List<Map<String, Object>> assetList = DbSession.selectList("eam_asset.getAssetInventory", map);
//        int total = DbSession.selectOne("eam_asset.countAssetInventory", map);
//        Map<String, Object> map2 = new HashMap<String, Object>();
//        Map<String, Object> map3 = new HashMap<String, Object>();
//        map3.put("list", assetList);
//        map3.put("total", total);
//        map2.put("msg", "查询成功");
//        map2.put("data", map3);
//        map2.put("status", 0);
        return  SuccessMsg("",maps);
    }

    @RequestMapping(value = "/execqueryToExcel")
    @ResponseBody
    public String execqueryToExcel(HttpServletRequest request, HttpServletResponse response,@RequestBody JSONObject pJson) {

        Map map = new HashMap();
        Map maps=new HashMap<>();
        try{
            map.put("cityCode",pJson.getString("cityCode"));
            map.put("receiveTime",pJson.getString("receiveTime"));
            List<Map> aResult = DbSession.selectList("eam_asset.getAssetInventory", map);
//            String fileName="盘点名称.xls";
//            String[] titles={"资产标签号", "资产名称", "物联网编号", "网关编号", "最后接收时间", "综资基站编号", "综资基站名称", "地址"};
//            String[] column={"asset_tag","asset_name","iot_num","gateway_id","receive_time","base_station_code","base_station_name","address"};
//            HSSFWorkbook wb= ExportExcel.workbooks("数据查询", titles, column,aResult);
            maps.put("list",aResult);
        }catch (Exception e){
            e.printStackTrace();
            return ExceptionMsg(e.getCause().getMessage());
        }
        return  SuccessMsg("",maps);

    }

    @RequestMapping(value = "/importExcel", method={RequestMethod.POST})
    public String importExcel(@RequestParam("file") MultipartFile file) throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        Date date=new Date();
        String result = "";
        //创建处理EXCEL的类
        ReadExcel readExcel = new ReadExcel();
        //解析excel，获取上传的事件单
        List<Map<String, Object>> userList = readExcel.getExcelInfo(file);
        SqlSession sqlSession =  DbFactory.Open(DbFactory.FORM);
        try{
            sqlSession.getConnection().setAutoCommit(false);
            //至此已经将excel中的数据转换到list里面了,接下来就可以操作list,可以进行保存到数据库,或者其他操作,
            //员工编码+岗位+指标名称 判断是否存在
            String  column1=null, column2=null, column3=null, column4=null, column5=null, column6=null, column7=null, column8=null,column9=null, column10=null,
                    column11=null,column12=null,column13=null,column14=null,column15=null, column16=null, column18=null,column19=null, column20=null,
                    column21=null,column22=null, column23=null,column24=null,column25=null, column26=null,column27=null, column28=null,column29=null, column30=null,
                    column31=null,column32=null, column33=null,column34=null,column35=null, column36=null,column37=null, column38=null,column39=null, column40=null,
                    column41=null,column42=null, column44=null,column45=null,  column48=null,column49=null, column50=null,
                    column52=null, column53=null,column54=null,column55=null, column56=null,column57=null, column58=null,column59=null, column60=null,
                    column61=null,column62=null, column63=null,column64=null,column65=null, column66=null,column67=null, column68=null,column69=null, column70=null,
                    column71=null, column72=null, column73=null, column74=null, column75=null, column76=null;
            Date column17=null,column43=null,column46=null,column47=null,column51=null;
            for(Map<String, Object> obj:userList){
                column1=obj.get("资产编号").toString();
                column2=obj.get("物联网编号").toString();
                column3=obj.get("资产标签号").toString();
                column4=obj.get("资产名称").toString();
                column5=obj.get("资产种类").toString();
                column6=obj.get("类别编码").toString();
                column7=obj.get("资产类别").toString();
                column8=obj.get("生产厂商").toString();
                column9=obj.get("供应商").toString();
                column10=obj.get("规格型号").toString();
                column11=obj.get("序列号")==null? null:obj.get("序列号").toString();
                column12=obj.get("应用领域编码")==null? null:obj.get("应用领域编码").toString();
                column13=obj.get("应用领域名称").toString();
                column14=obj.get("折旧年限").toString();
                column15=obj.get("是否重要资产").toString();
                column16=obj.get("备注").toString();
                column17=dateFormat.parse(obj.get("erp创建时间").equals("")? dateFormat.format(date):obj.get("erp创建时间").toString());
                column18=obj.get("数量").toString();
                column19=obj.get("计量单位").toString();
                column20=obj.get("传输线路资源量").toString();
                column21=obj.get("传输线路资源量单位").toString();
                column22=obj.get("资产状态").toString();
                column23=obj.get("资产子状态").toString();
                column24=obj.get("责任人编码").toString();
                column25=obj.get("责任人名称").toString();
                column26=obj.get("使用人编码").toString();
                column27=obj.get("使用人名称").toString();
                column28=obj.get("责任部门编码").toString();
                column29=obj.get("责任部门").toString();
                column30=obj.get("实物管理部门编码").toString();
                column31=obj.get("实物管理部门").toString();
                column32=obj.get("资产地点编码").toString();
                column33=obj.get("资产地点").toString();
                column34=obj.get("综资站点编码").toString();
                column35=obj.get("综资站点名称").toString();
                column36=obj.get("对应综资站点状态").toString();
                column37=obj.get("基站/营业厅/仓库编码").toString();
                column38=obj.get("基站/营业厅/仓库").toString();
                column39=obj.get("责任人确认").toString();
                column40=obj.get("使用人确认").toString();
                column41=obj.get("是否共享设备").toString();
                column42=obj.get("是否共建设备").toString();
                column43=dateFormat.parse(obj.get("启用日期").equals("")? dateFormat.format(date):obj.get("启用日期").toString());
                column44=obj.get("合同编号").toString();
                column45=obj.get("租金").toString();
                column46=dateFormat.parse(obj.get("起租日期").equals("")? dateFormat.format(date):obj.get("起租日期").toString());
                column47=dateFormat.parse(obj.get("止租日期/报废日期").equals("")? dateFormat.format(date):obj.get("止租日期/报废日期").toString());
                column48=obj.get("老资产标签号").toString();
                column49=obj.get("资产使用状态").toString();
                column50=obj.get("是否机密").toString();
                column51=dateFormat.parse(obj.get("购置日期").equals("")? dateFormat.format(date):obj.get("购置日期").toString());
                column52=obj.get("原值").toString();
                column53=obj.get("净值").toString();
                column54=obj.get("净额").toString();
                column55=obj.get("残值").toString();
                column56=obj.get("本期折旧额").toString();
                column57=obj.get("本年折旧额").toString();
                column58=obj.get("累计折旧额").toString();
                column59=obj.get("本期减值准备").toString();
                column60=obj.get("本年减值准备").toString();
                column61=obj.get("累计减值准备").toString();
                column62=obj.get("网络元素编码").toString();
                column63=obj.get("网络元素名称").toString();
                column64=obj.get("投资分类编码").toString();
                column65=obj.get("投资分类名称").toString();
                column66=obj.get("业务平台编码").toString();
                column67=obj.get("业务平台名称").toString();
                column68=obj.get("网络层次编码").toString();
                column69=obj.get("网络层次名称").toString();
                column70=obj.get("支撑网设备类型编码").toString();
                column71=obj.get("支撑网设备类型名称").toString();
                column72=obj.get("项目编号").toString();
                column73=obj.get("项目名称").toString();
                column74=obj.get("项目小类").toString();
                column75=obj.get("是否抵扣").toString();
                column76=obj.get("原规格型号").toString();

               Map mapss=new HashMap();
                mapss.put("column1",column1);
                mapss.put("column2",column2);
                mapss.put("column3",column3);
                mapss.put("column4",column4);
                mapss.put("column5",column5);
                mapss.put("column6",column6);
                mapss.put("column7",column7);
                mapss.put("column8",column8);
                mapss.put("column9",column9);
                mapss.put("column10",column10);
                mapss.put("column11",column11);
                mapss.put("column12",column12);
                mapss.put("column13",column13);
                mapss.put("column14",column14);
                mapss.put("column15",column15);
                mapss.put("column16",column16);
                mapss.put("column17",column17);
                mapss.put("column18",column18);
                mapss.put("column19",column19);
                mapss.put("column20",column20);
                mapss.put("column21",column21);
                mapss.put("column22",column22);
                mapss.put("column23",column23);
                mapss.put("column24",column24);
                mapss.put("column25",column25);
                mapss.put("column26",column26);
                mapss.put("column27",column27);
                mapss.put("column28",column28);
                mapss.put("column29",column29);
                mapss.put("column30",column30);
                mapss.put("column31",column31);
                mapss.put("column32",column32);
                mapss.put("column33",column33);
                mapss.put("column34",column34);
                mapss.put("column35",column35);
                mapss.put("column36",column36);
                mapss.put("column37",column37);
                mapss.put("column38",column38);
                mapss.put("column39",column39);
                mapss.put("column40",column40);
                mapss.put("column41",column41);
                mapss.put("column42",column42);
                mapss.put("column43",column43);
                mapss.put("column44",column44);
                mapss.put("column45",column45);
                mapss.put("column46",column46);
                mapss.put("column47",column47);
                mapss.put("column48",column48);
                mapss.put("column49",column49);
                mapss.put("column50",column50);
                mapss.put("column51",column51);
                mapss.put("column52",column52);
                mapss.put("column53",column53);
                mapss.put("column54",column54);
                mapss.put("column55",column55);
                mapss.put("column56",column56);
                mapss.put("column57",column57);
                mapss.put("column58",column58);
                mapss.put("column59",column59);
                mapss.put("column60",column60);
                mapss.put("column61",column61);
                mapss.put("column62",column62);
                mapss.put("column63",column63);
                mapss.put("column64",column64);
                mapss.put("column65",column65);
                mapss.put("column66",column66);
                mapss.put("column67",column67);
                mapss.put("column68",column68);
                mapss.put("column69",column69);
                mapss.put("column70",column70);
                mapss.put("column71",column71);
                mapss.put("column72",column72);
                mapss.put("column73",column73);
                mapss.put("column74",column74);
                mapss.put("column75",column75);
                mapss.put("column76",column76);
                this.assetService.importExcel(sqlSession,mapss);
                sqlSession.getConnection().commit();


            }
        }catch (Exception ex){
            sqlSession.getConnection().rollback();
            ex.printStackTrace();
        }
        if(userList != null && !userList.isEmpty()){
            result = "上传成功";
        }else{
            result = "上传失败";
        }
        map.put("message", result);
        return SuccessMsg("",result);
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
        List<Map<String, Object>> assetList = DbSession.selectList("eam_asset_status.listTagNoBindGateway", pJson);
        int total = DbSession.selectOne("eam_asset_status.countTagNoBindGateway", pJson);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", assetList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2,features);
    }

    /**
     * 获取未关联网关的资产列表
     * @return
     */
    @RequestMapping(value = "/listAssetNoBindGateway", produces = "text/plain;charset=UTF-8")
    public String listAssetNoBindGateway(@RequestBody JSONObject pJson) throws UnsupportedEncodingException {
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
        List<Map<String, Object>> assetList = DbSession.selectList("eam_asset.listAssetNoBindGateway", map);
        int total = DbSession.selectOne("eam_asset.countAssetNoBindGateway", map);
        Map<String, Object> map2 = new HashMap<String, Object>();
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("list", assetList);
        map3.put("total", total);
        map2.put("msg", "查询成功");
        map2.put("data", map3);
        map2.put("status", 0);
        return JSON.toJSONString(map2);
    }

}
