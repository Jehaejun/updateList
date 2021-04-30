package updateList.fileWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SvnLogFileWriter {
	private String logData;
	private String filePath;

	public SvnLogFileWriter(String logData, String filePath) {
		this.logData = logData;
		this.filePath = filePath;
	}

	public void write() {
		// 경로 없이 파일명만 작성할 경우 현재 디렉토리에 파일 생성함.
		File file = new File(filePath);
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			// FileOutputStream 클래스가 파일에 바이트를 내보내는 역할을 하는 클래스이므로
			// 내보낼 내용을 바이트로 변환을 하는 작업이 필요합니다.

			String line = System.getProperty("line.separator");
			logData = logData.replace("\n", line);

			byte[] content = logData.getBytes();

			fos.write(content);
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
