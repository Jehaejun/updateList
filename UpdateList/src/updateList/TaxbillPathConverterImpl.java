package updateList;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TaxbillPathConverterImpl implements PathConvertible{
	private String path;
	private String rootPath;
	
	public TaxbillPathConverterImpl(String path, String rootPath) {
		this.path = path;
		this.rootPath = rootPath;
	}
	
	@Override
	public String convert() {
		// TODO Auto-generated method stub
		
		StringBuilder stringBuilder = new StringBuilder();
		List<String> notConverlogList = new ArrayList<String>();
		List<String> dupLogList = new ArrayList<String>();
		List<String> logList = new ArrayList<String>();

		path = path.replaceAll(rootPath + "_PT/web"		         , "./src")
		           .replaceAll(rootPath + "_PT/src"		         , "./src")
		           .replaceAll(rootPath + "_DOMAIN"		         , "./src/WEB-INF")
		           .replaceAll(rootPath + "_PT_STATIC/web"       , "./src")
		           .replaceAll(rootPath + "_ETC/xml/web_service/", "./src/WEB-INF/jex/xml/TCMC/xml/web_service")
		           .replaceAll("[\\[](.*?)[\\]]", "")
		           .replaceAll(" ", "");
		StringTokenizer st = new StringTokenizer(path, "\n");

		while(st.hasMoreTokens()) {
			String stPath = st.nextToken();
			
			if(stPath.indexOf(rootPath) > -1 || stPath.indexOf("확인필요") > -1) {
				notConverlogList.add(stPath);
			}else {
				dupLogList.add(stPath);
			}
			
			if(stPath.indexOf(".java") > -1) {
				String javaPath = stPath.replace("./src/WEB-INF/src", "./src/WEB-INF/classes").replace(".java", ".class");
				dupLogList.add(javaPath);
			}
		}

		if(notConverlogList.size() != 0) {
			dupLogList.addAll(notConverlogList);
		}

		if(dupLogList.size() == 0) {
			return "";
		}
		
		for(String path : dupLogList) {
			if(!logList.contains(path)) {
				logList.add(path);
			}
		}
		
		for(String path : logList) {
			stringBuilder.append("\n" + path);
		}
		
		return stringBuilder.toString().substring(1);
	}

}
