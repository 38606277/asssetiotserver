package root.report.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.dom4j.Document;
import org.dom4j.Element;
import root.report.db.DbFactory;
import root.report.sys.SysContext;

import java.util.List;
import java.util.Map;

public class RO {
	

	public static SerializerFeature[] features = { SerializerFeature.WriteNullNumberAsZero,
			SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteMapNullValue,
			SerializerFeature.PrettyFormat, SerializerFeature.UseISO8601DateFormat,
			SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteNullListAsEmpty };
	private Object List;

	public  String SuccessMsg(String message,Object data)
	{
		//根据列权限过滤返回的列
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("resultCode", "1000");
		jsonObject.put("message", message);
		jsonObject.put("data", data);
		return JSON.toJSONString(jsonObject,features);
	}
	public  String ErrorMsg(String resultCode,String message)
	{
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("resultCode", resultCode);
		jsonObject.put("message", message);
		return JSON.toJSONString(jsonObject,features);
	}
	public  String ExceptionMsg(String message)
	{
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("resultCode", "3000");
		jsonObject.put("message", message);
		return JSON.toJSONString(jsonObject,features);
	}

	//移除某个节点
	protected void moveSqlId(Document userDoc, String sqlId)
	{
		List<Element> list = userDoc.selectNodes("//select[@id='"+sqlId+"']");
		for (int i = 0; i < list.size(); i++)
		{
			list.get(i).getParent().remove(list.get(i));
		}
	}
	//判断doc是否存在某个节点
	protected boolean checkIsContainsSqlId(Document userDoc,String sqlId)
	{
		List<Element> list = userDoc.selectNodes("//select[@id='"+sqlId+"']");
		if(list==null||list.size()==0)
		{
			return false;
		}
		return true;
	}
	//查询列表
	protected   <E> List<E> selectList(String mapperId, Map param)
	{
		//注入行权限过滤	param.put("org_ids",dataAuth);
		////		param.put("dept_ids",dataAuth);
		String userCode = SysContext.getUserCode();
		Map authData= getDataAuthByUsercode(userCode);
		param.putAll(authData);
		authData.forEach((key, value) -> {
			param.put(key,value);
		});
		List result= DbFactory.Open(DbFactory.FORM).selectList(mapperId, param);
//		列权限过滤
		return  result;

	}

	private Map<String,String> getDataAuthByUsercode(String userCode) {
//		org:"12,23,23" dept:"4,43,34"

return null;
	}

	//查询map
	<T> T selectOne(String var1, Object var2){
		return null;
	}
	//

}
