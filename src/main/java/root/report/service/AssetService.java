package root.report.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageRowBounds;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultComment;
import org.dom4j.tree.DefaultElement;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import root.configure.AppConstants;
import root.configure.MybatisCacheConfiguration;
import root.report.db.DbFactory;
import root.report.util.ThreadPoolExecutorUtil;
import root.report.util.XmlUtil;

import javax.websocket.Session;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
public class AssetService {

    private static Logger log = Logger.getLogger(AssetService.class);



    public void importExcel(SqlSession sqlSession, Map mapss) {
        sqlSession.insert("eam_asset.addAssetImport",mapss);
    }
}
