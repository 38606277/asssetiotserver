package root;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import root.mqtt.configure.MyMqttPahoMessageDrivenChannelAdapter;
import root.mqtt.test.configure.MyMqttPahoMessageDrivenChannelTestAdapter;
import root.report.db.DbFactory;
import root.report.service.NLPService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by pangkunkun on 2017/9/3.
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Resource
    private MyMqttPahoMessageDrivenChannelAdapter myMqttPahoMessageDrivenChannelAdapter;

    @Resource
    private MyMqttPahoMessageDrivenChannelTestAdapter myMqttPahoMessageDrivenChannelTestAdapter;

    @Override
    public void run(ApplicationArguments var1) throws Exception {
        System.out.println("reportServer启动成功!");
        //初始化system数据库连接
        System.out.println("开始初始化系统数据库!");
        try {
            DbFactory.initializeDB("form");

            System.out.println("初始化系统数据库成功!");
        } catch (Exception ex) {
            System.out.println("初始化系统数据库失败!");
            ex.printStackTrace();
        }

        //初始化网关主题
        initTopic();

        //初始化测试网关主题
        initTopicTest();
    }

    /**
     * 初始化网关主题
     */
    private void initTopic(){
        List<Map<String,Object>> eamGatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway.listEamGateway");
        if(eamGatewayList!=null && 0 < eamGatewayList.size()){
            for(Map<String,Object> map : eamGatewayList){
                if(map.containsKey("gateway_id")){
                    myMqttPahoMessageDrivenChannelAdapter.addTopicByGateway(String.valueOf(map.get("gateway_id")));
                }
            }
        }
    }

    /**
     * 初始化测试网关主题
     */
    private void initTopicTest(){
        List<Map<String,Object>> eamGatewayList = DbFactory.Open(DbFactory.FORM).selectList("eam_gateway_test.listEamGateway");
        if(eamGatewayList!=null && 0 < eamGatewayList.size()){
            for(Map<String,Object> map : eamGatewayList){
                if(map.containsKey("gateway_id")){
                    myMqttPahoMessageDrivenChannelTestAdapter.addTopicByGateway(String.valueOf(map.get("gateway_id")));
                }
            }
        }
    }




}