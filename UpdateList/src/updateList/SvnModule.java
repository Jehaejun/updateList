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
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnModule implements Runnable{
	SVNRepository svnRepo;
	private String svnUrl;
	private String id;
	private String pwd;
	private UpdateListFrame updateListFrame;
	
	private String strDt;
	private String endDt;
	
	public SvnModule(String svnUrl, String id, String pwd, UpdateListFrame updateListFrame) {
		this.svnUrl = svnUrl;
		this.id = id;
		this.pwd = pwd;
		this.updateListFrame = updateListFrame;
	}
	
	static class Builder {
		private String svnUrl;
		private String id;
		private String pwd;
		private UpdateListFrame updateListFrame;
		
		public Builder svnUrl(String svnUrl) {
			this.svnUrl = svnUrl;
			return this;
		}
		
		public Builder id(String id) {
			this.id = id;
			return this;
		}
		
		public Builder pwd(String pwd) {
			this.pwd = pwd;
			return this;
		}
		
		public Builder callBackInstance(UpdateListFrame updateListFrame) {
			this.updateListFrame = updateListFrame;
			return this;
			
		}
		public SvnModule bulid() {
			return new SvnModule(svnUrl, id, pwd, updateListFrame);
		}
	}

	public void setStrDt(String strDt) {
		this.strDt = strDt;
	}


	public void setEndDt(String endDt) {
		this.endDt = endDt;
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


	public SVNRepository connectSVN() throws Exception {
		String url = this.svnUrl;
		String svnUser = this.id;
		String svnPassword = this.pwd;
		
		svnRepo = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));	
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUser, svnPassword.toCharArray());
		svnRepo.setAuthenticationManager(authManager);
		svnRepo.testConnection();
		
		return svnRepo;
	}
	
	
	@SuppressWarnings("unchecked")
	private void getSVNLogList(String startDt, String endDt) throws Exception {
		List<LogDTO> svnLogList = new ArrayList<LogDTO>();
			
		long startRevision = svnRepo.getDatedRevision(dateConvert(startDt, 0));
		long endRevision = svnRepo.getDatedRevision(dateConvert(endDt, 1));

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
		// 조회일
		Calendar calSearch = Calendar.getInstance();
		String[] tempStartDt = strDate.split("-");
		calSearch.set(Integer.parseInt(tempStartDt[0]), Integer.parseInt(tempStartDt[1]), Integer.parseInt(tempStartDt[2]));

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
        Date searchDate = new Date(nowData.getTimeInMillis());
        
		return searchDate;
	}

}
