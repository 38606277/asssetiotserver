package root.report.org;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.report.common.RO;
import root.report.db.DbFactory;
import root.report.util.Node;
import root.report.util.TreeBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/reportServer/org")
public class OrgController extends RO {

	/**
	 * 通过org_pid获取组织信息
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/listByOrgPid", produces = "text/plain;charset=UTF-8")
	public String listByOrgPid(@RequestBody JSONObject pJson)  {
		List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("fndOrg.listByOrgPid", pJson);
		return SuccessMsg("查询成功",gatewayList);
	}


	/**
	 * 通过org_pid获取树结构数据
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/listOrgTreeByOrgPid", produces = "text/plain;charset=UTF-8")
	public String listOrgTreeByOrgPid(@RequestBody JSONObject pJson)  {
		List<Map<String, Object>> gatewayList = DbFactory.Open(DbFactory.FORM).selectList("fndOrg.listOrgTreeByOrgPid", pJson);
		return SuccessMsg("查询成功",gatewayList);
	}


	/**
	 * 添加组织信息
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/addOrg", produces = "text/plain;charset=UTF-8")
	public String addOrg(@RequestBody JSONObject pJson)  {
		DbFactory.Open(DbFactory.FORM).insert("fndOrg.addOrg", pJson);
		return SuccessMsg("保存成功","");
	}

	/**
	 * 添加组织信息
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/createOrg", produces = "text/plain;charset=UTF-8")
	public String createOrg(@RequestBody JSONObject pJson)  {
		//pJson中放pid,org_name:"未命名"
		DbFactory.Open(DbFactory.FORM).insert("fndOrg.createOrg", pJson);
		String org_id=pJson.getString("org_id");
		return SuccessMsg("保存成功",org_id);
	}


	/**
	 * 通过org_id更新组织信息
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/updateOrgByOrgId", produces = "text/plain;charset=UTF-8")
	public String updateOrgByOrgId(@RequestBody JSONObject pJson)  {
		DbFactory.Open(DbFactory.FORM).update("fndOrg.updateOrgByOrgId", pJson);
		return SuccessMsg("保存成功","");
	}


	/**
	 * 通过org_id删除组织信息
	 * @param pJson
	 * @return
	 */
	@RequestMapping(value = "/deleteByOrgId", produces = "text/plain;charset=UTF-8")
	public String deleteByOrgId(@RequestBody JSONObject pJson)  {
		DbFactory.Open(DbFactory.FORM).delete("fndOrg.deleteByOrgId", pJson);
		return SuccessMsg("删除成功","");
	}

	@RequestMapping(value = "/getOrgTree", produces = "text/plain;charset=UTF-8")
	public String getOrgTree(@RequestBody JSONObject pJson)  {
		JSONObject jsonTree=new JSONObject();
		List<Node> nodes= DbFactory.Open(DbFactory.FORM).selectList("fndOrg.getAll", pJson);
		// 拼装树形json字符串
		List<Node>  result= new TreeBuilder().buildTree(nodes);
		return SuccessMsg("",result);
	}

	@RequestMapping(value = "/getOrgByID", produces = "text/plain;charset=UTF-8")
	public String getOrgByID(@RequestBody JSONObject pJson)  {
		Map<String,Object> result= DbFactory.Open(DbFactory.FORM).selectOne("fndOrg.getByOrgID", pJson);
		return SuccessMsg("",result);
	}

}
