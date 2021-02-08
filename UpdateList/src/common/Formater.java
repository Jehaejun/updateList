package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formater {
	public static String dateFormat(String str) {
		String strDate = null;

		SimpleDateFormat recvSimpleFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat tranSimpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

		try {
			Date data = recvSimpleFormat.parse(str);
			strDate = tranSimpleFormat.format(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return strDate;
	}
	
	public static String typeFormat(char str) {
		String strType = "";
		
		switch (str) {
		case 'A':
			strType = "추가";
			break;

		case 'D':
			strType = "삭제";
			break;
		
		case 'M':
			strType = "수정";
			break;
		
		}
		return strType;
	}
}
