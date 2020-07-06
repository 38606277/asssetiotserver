package root.asset.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import root.report.db.DbFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class AlarmJob {

    //每10分钟执行一次
    @Scheduled(cron = "0 1/15 * * * ?")
    public void testScheduled(){
        System.out.println("springScheduled run:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        check();
    }


    public void check(){
        checkLost();
        checkLowVoltage();
    }

    /**
     * 检查低电压的标签 并生成警告
     * 条件 -- 小于3.2 并且1小时内没有警告
     */
    public void checkLowVoltage(){
        List<Map<String,Object>> dataList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset_status.listLowVoltageTagAlarm", new HashMap<String, Object>());
        for(Map<String,Object> data:dataList){
            addAlarm(String.valueOf(data.get("asset_id")),"低电压");
        }
    }

    /**
     * 检查丢失的标签，并生成警告
     * 条件 -- 两小时未上报 并且1小时内没有警告
     */
    public void checkLost() {

        List<Map<String,Object>> dataList = DbFactory.Open(DbFactory.FORM).selectList("eam_asset_status.listLostTagAlarm", new HashMap<String, Object>());
        for(Map<String,Object> data:dataList){
            addAlarm(String.valueOf(data.get("asset_id")),"丢失");
        }

    }


    public void addAlarm(String assetId,String alarmType){
        HashMap<String,Object> map = new HashMap<>();
        map.put("alarm_num","DYD" + System.currentTimeMillis());
        map.put("asset_id",assetId);
        map.put("alarm_type",alarmType);
        //添加
        DbFactory.Open(DbFactory.FORM).insert("eam_alarm.addEamAlarm",map);
    }



}
