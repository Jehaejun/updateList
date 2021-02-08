package updateList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnLogCaster implements Runnable {
	SVNRepository svnRepo;
	private String strDt;
	private String endDt;
	private String id;
	private UpdateListFrame updateListFrame;
	
	static class Builder {
		SVNRepository svnRepo;
		private String strDt;
		private String endDt;
		private String id;
		private UpdateListFrame updateListFrame;
		
		public Builder svnRepository(SVNRepository svnRepo) {
			this.svnRepo = svnRepo;
			return this;
		}
		public Builder searchStartDate(String strDt) {
			this.strDt = strDt;
			return this;
		}
		public Builder searchEndDate(String endDt) {
			this.endDt = endDt;
			return this;
		}
		public Builder svnUserId(String id) {
			this.id = id;
			return this;
		}
		public Builder callBackInstance(UpdateListFrame updateListFrame) {
			this.updateListFrame = updateListFrame;
			return this;
		}
		public SvnLogCaster bulid() {
			return new SvnLogCaster(svnRepo, strDt, endDt, id, updateListFrame);
		}
	}
	
	public SvnLogCaster(SVNRepository svnRepo, String strDt, String endDt, String id, UpdateListFrame updateListFrame) {
		this.svnRepo = svnRepo;
		this.strDt = strDt;
		this.endDt = endDt;
		this.id = id;
		this.updateListFrame = updateListFrame;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		try {
			getSVNLogList(strDt, endDt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getSVNLogList(String startDt, String endDt) throws Exception {
		List<LogDTO> svnLogList = new ArrayList<LogDTO>();
			
		Date startDate = dateConvert(startDt, 0);
		long startRevision = svnRepo.getDatedRevision(startDate);
		
		Date endDate =  dateConvert(endDt, 1);
		long endRevision = svnRepo.getDatedRevision(endDate);

		Collection<SVNLogEntry> logEntries = null;
		logEntries = svnRepo.log(new String[] {""}, null, startRevision, endRevision, true, true);
		Iterator<SVNLogEntry> entries = logEntries.iterator();
		while (entries.hasNext()) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			if (logEntry == null) {
				continue;
			}

			/*System.out.println("---------------------------------------------");
			System.out.println("revision: " + logEntry.getRevision());
			System.out.println("author: " + logEntry.getAuthor());
			System.out.println("date: " + logEntry.getDate());
			System.out.println("log message: " + logEntry.getMessage());
*/
			if (logEntry.getChangedPaths() == null || logEntry.getChangedPaths().size() == 0) {
				continue;
			}

			//if(true) {
			if(id.equals(logEntry.getAuthor()) || logEntry.getMessage().indexOf(id) > -1) {
			//	System.out.println();
			//	System.out.println("changed paths:");
				Set<String> changedPathsSet = logEntry.getChangedPaths().keySet();
				Iterator<String> changedPaths = changedPathsSet.iterator();
				while (changedPaths.hasNext()) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());		
					
					if (entryPath.getCopyPath() != null) {
					/*	System.out.println(" " + entryPath.getType() + " " + entryPath.getPath() + "(from "
								+ entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")");*/
					} else {
						//strBuilder.append("\n" + logEntry.getAuthor() + entryPath.getPath());
						LogDTO logDTO = new LogDTO();
						logDTO.setAuthor(logEntry.getAuthor());
						logDTO.setDate(logEntry.getDate().toString());
						logDTO.setMessage(logEntry.getMessage());
						logDTO.setRevision(logEntry.getRevision());
						logDTO.setPath(entryPath.getPath());
						logDTO.setType(entryPath.getType());
						
						svnLogList.add(logDTO);
						//SVNList.add("[" + logEntry.getDate() + "]"+entryPath.getPath() + ('D' != entryPath.getType() ? "" : " - 확인필요(삭제된 파일)"));
						//System.out.println(entryPath.getType());
						//System.out.println(" " + entryPath.getType() + " " + entryPath.getPath());
					}
				}
			}
		}
		updateListFrame.callBackSVN(svnLogList);
	}
	
	private Date dateConvert(String strDate, int gb) {
		//String nowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		
		// 조회일
		Calendar calSearch = Calendar.getInstance();
		String[] tempStartDt = strDate.split("-");
		calSearch.set(Integer.parseInt(tempStartDt[0]), Integer.parseInt(tempStartDt[1]), Integer.parseInt(tempStartDt[2]));

		// 현재일자
/*		Calendar calNow = Calendar.getInstance();
		String[] date = nowDate.split("-");
		calNow.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
		
		long diffStartDay = ((calNow.getTimeInMillis() - calSearch.getTimeInMillis()) / 1000) / (60*60*24);*/
		
       // System.out.println("두 날짜의 날짜 차이: " + diffStartDay);

   /*     Date now = new Date();
        if(gb == 0) {
        	now.setHours(0);
            now.setMinutes(0);
        }else {
        	now.setHours(23);
            now.setMinutes(59);
        }*/
        
/*        Calendar nowData = Calendar.getInstance();
        if(gb == 0) {
        	nowData.set(Calendar.HOUR_OF_DAY, 0);
            nowData.set(Calendar.MINUTE, 0);
            nowData.set(Calendar.SECOND, 0);
        }else {
        	nowData.set(Calendar.HOUR_OF_DAY, 23);
            nowData.set(Calendar.MINUTE, 59);
            nowData.set(Calendar.SECOND, 59);
        }*/
        
      //  System.out.println(now.getTime());
      //  System.out.println(nowData.getTimeInMillis() - (diffStartDay) * 24 * 3600000);
        
        Calendar nowData = Calendar.getInstance();
        nowData.set(Calendar.YEAR, Integer.parseInt(tempStartDt[0]));
        nowData.set(Calendar.MONTH, Integer.parseInt(tempStartDt[1]) - 1);
        nowData.set(Calendar.DATE, Integer.parseInt(tempStartDt[2]));

        if(gb == 0) {
        	nowData.set(Calendar.HOUR_OF_DAY, 0);
            nowData.set(Calendar.MINUTE, 0);
            nowData.set(Calendar.SECOND, 0);
        }else {
        	nowData.set(Calendar.HOUR_OF_DAY, 23);
            nowData.set(Calendar.MINUTE, 59);
            nowData.set(Calendar.SECOND, 59);
        }
        //System.out.println(nowData.getTimeInMillis());
		//Date searchDate = new Date(nowData.getTimeInMillis() - (diffStartDay) * 24 * 3600000);
        Date searchDate = new Date(nowData.getTimeInMillis());
        
		return searchDate;
	}
}
