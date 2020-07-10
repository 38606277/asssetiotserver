package root.report.menu;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.DbSession;
import root.report.common.RO;
import root.report.db.DbFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/reportServer/menu")
public class MenuController extends RO {


	/**
	 * 获取树形结构的菜单数据
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/getMenuList", produces = "text/plain;charset=UTF-8")
	public String getMenuList(@RequestBody JSONObject pJson)  {
		Map<String,Object> map = new HashMap<>();
		map.put("func_type", pJson.getString("func_type"));
		//默认查询pid为0的数据
		map.put("func_pid", "0");
		List<Map<String,Object>> nodeList = new ArrayList<>();
		List<Map<String,Object>> menuList = DbSession.selectList("fnd_menu.listMenu",map);
		for(Map<String,Object> menu:menuList){
			nodeList.add(menu);
			map.put("func_pid",menu.get("func_id"));
			List<Map<String,Object>> childList = DbSession.selectList("fnd_menu.listMenu",map);
			nodeList.addAll(childList);
		}
		return SuccessMsg("查询成功",nodeList);
	}




	/**
	 * 获取树形结构的菜单数据
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/getMenuTreeList", produces = "text/plain;charset=UTF-8")
	public String getMenuTreeList(@RequestBody JSONObject pJson)  {
		Map<String,String> map = new HashMap<String,String>();
		map.put("func_type", pJson.getString("func_type"));
		//默认查询pid为0的数据
		map.put("func_pid", "0");
		List<Map<String,Object>> rootList = DbSession.selectList("fnd_menu.listMenu",map);
		showExcelRuleTreeNodeReact(map,rootList);
		return SuccessMsg("查询成功",rootList);
	}

	public void showExcelRuleTreeNodeReact(Map<String,String> map, List<Map<String,Object>> nodeList) {
		for (Map<String,Object> auth : nodeList) {
			map.put("func_pid", auth.get("func_id").toString());
			List<Map<String,Object>> childList = DbSession.selectList("fnd_menu.listMenu",map);
			if(childList.size()>0){
				auth.put("children", childList);
				showExcelRuleTreeNodeReact(map,childList);
			}else{
				auth.put("children", new ArrayList<>());
			}
		}

	}
}
