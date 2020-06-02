package root.report.control.report;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.RO;
import root.report.db.DbFactory;
import root.report.service.DictService;
import root.report.service.ReportService;
import root.report.util.ExecuteSqlUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportServer/report")
public class ReportControl extends RO {

    private static Logger log = Logger.getLogger(ReportControl.class);

    @Autowired
    public ReportService reportService;



    //查询所有的数据字典
    @RequestMapping(value = "/getAll", produces = "text/plain;charset=UTF-8")
    public String getAll() {

        List<Map<String, String>> list ;
        try {
            list= reportService.getAll();
            return SuccessMsg("", list);
        } catch (Exception ex){
            return ExceptionMsg(ex.getMessage());
        }
    }

}
