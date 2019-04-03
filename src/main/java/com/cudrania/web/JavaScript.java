package com.cudrania.web;

import com.nianien.core.util.ArrayUtils;

/**
 * 生成JavaScript脚本的工具类
 * 
 * @author skyfalling
 * 
 */
public class JavaScript {

	/**
	 * 根据函数名和参数列表生成JavaScript函数调用脚本
	 * 
	 * @param functionName
	 * @param args
	 * @return
	 */
	public static String function(String functionName, String... args) {
		StringBuilder sb = new StringBuilder();
		sb.append(functionName);
		if (args.length > 0) {
			sb.append("(").append(ArrayUtils.toString(args, ","))
					.append(")");
		} else if (!functionName.matches("^.+\\(.*\\);*")) {
			sb.append("()");
		}
		sb.append(";");
		return script(sb.toString());
	}

	/**
	 * 根据脚本内容生成JavaScript脚本
	 * 
	 * @param scriptContent
	 * @return
	 */
	public static String script(String scriptContent) {
		return new StringBuilder("<script type=\"text/javascript\">")
				.append(scriptContent).append("</script>").toString();
	}

}
