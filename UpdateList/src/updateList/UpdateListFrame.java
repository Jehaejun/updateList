package updateList;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tmatesoft.svn.core.io.SVNRepository;

import common.Formater;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import updateList.fileWriter.SvnLogFileWriter;

public class UpdateListFrame {
	SVNRepository svnRepo; 
	GridBagLayout gbl;
	GridBagConstraints gbc;
	private JFrame mainFrame;
	//private JTextField txtFieldSvnUrl;
	private JTextField txtFieldId;
	private JPasswordField txtFieldPwd;
	private JButton btnSearch;
	private JButton btnConvert;
	private JButton btnCopy;
	private JButton btnFileOut;
	private JButton btnJoin;
	private JButton btnNewVersionDownlaod;
	JTextArea txtAreaClient;
	private JTextArea txtAreaServer;
	private SvnModule svnModule;
	private JDatePickerImpl datePicker;
	private JDatePickerImpl datePicker2;
	UtilDateModel model;
	UtilDateModel model2;
	private JComboBox<String> comboSvnUrl;
	private ImageIcon imgIndicator;
	private ImageIcon imgHide;
	private JLabel imgLabel;
	static UpdateListFrame updateListFrame;
	private String versionData;
	private JLabel lableUpdateDate;
	private int nowVersion = 161;
	private String nowVersionTitle = "v1.6.1";
	Exception tempException;
	
	JMenuBar menuBar; //메뉴바 선언
	JMenu menu; //메뉴 선언
	JMenuItem menuItem; //메뉴 항목 선언
	
	public UpdateListFrame() {
		//imgIndicator = new ImageIcon("C:/Users/제해준/Desktop/loading.gif");
		imgIndicator = new ImageIcon(getClass().getResource("/image/loading.gif"));
		imgHide = new ImageIcon(getClass().getResource("/image/hide.PNG"));
		
		mainFrame = new JFrame("반영목록 " + nowVersionTitle);
		mainFrame.setSize(1000, 1000);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		prepareGUI();
		mainFrame.setVisible(true);
		mainFrame.setLocationRelativeTo(null); // 프레임 실행시 위치 중앙
		txtFieldId.requestFocus();
		setMenuBar();
		buttonAction();

	}
	public static void main(String[] args) {
		updateListFrame = new UpdateListFrame();
		updateListFrame.getNewVersionData();
	}
	
	private void prepareGUI() {
		String[] svnUrlArr = {
				"svn://10.254.241.174:3691/SCLOUD_ERP",
				"svn://10.254.241.174:3691/SERP_CMS",
				"svn://10.254.241.174:3691/SERPCMSHOME",
				"svn://10.254.241.174:3691/SERPCMSADMIN",
				"svn://10.254.241.174:3691/WEBIZ",
				"svn://10.254.241.174:3691/TAXBILL_GW",
				"svn://10.254.241.174:3691/TAXBILL_NEW"/*,
				"svn://10.254.241.173/TCMC"*/
		};
		
		gbl = new GridBagLayout();
		mainFrame.setLayout(gbl); // GridBagLayout을 설정
		gbc = new GridBagConstraints(); // GridBagLayout에 배치할 컴포넌트 위치 정보 등을 담을 객체 준비

		gbc.fill = GridBagConstraints.BOTH; // GridBagConstraints.fill: 컴포넌트의 디스플레이 영역이 컴포넌트가 요청한 크기보다 클 때,

		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		// svn
		//txtFieldSvnUrl = new JTextField();
		
		comboSvnUrl = new JComboBox<String>(svnUrlArr);
		comboSvnUrl.setBackground(new Color(255, 255, 255));
		comboSvnUrl.setEditable(true);
		gbAdd(new JLabel("SVN URL"), 0, 0, 4, 1, 0.5);
		gbAdd(comboSvnUrl, 4, 0, 20, 1, 0.5);
		// id
		txtFieldId = new JTextField();
		gbAdd(new JLabel("ID"), 0, 1, 4, 1, 0.5);
		gbAdd(txtFieldId, 4, 1, 20, 1, 0.5);
		// pwd
		txtFieldPwd = new JPasswordField();
		gbAdd(new JLabel("Password"), 0, 2, 4, 1, 0.5);
		gbAdd(txtFieldPwd, 4, 2, 20, 1, 0.5);
		// 로그인
		btnJoin = new JButton("SVN 연결");
		gbAdd(btnJoin, 0, 3, 24, 1, 0.5);
		// 조회일자, 조회버튼
		model = new UtilDateModel(); 
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		datePicker = new JDatePickerImpl(datePanel); 
	    
		model2 = new UtilDateModel();
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2);
		datePicker2 = new JDatePickerImpl(datePanel2); 

	//	imgIndicator = new ImageIcon("C:/Users/제해준/Desktop/loading.gif"); 
	//	imgIndicator = new ImageIcon("C:/Users/제해준/Desktop/hide.PNG"); 
		imgLabel = new JLabel(imgHide);
	//	imgLabel.setVisible(false);
		
		btnSearch = new JButton("조회");
		btnSearch.setEnabled(false);
		gbAdd(new JLabel("조회일자"), 0, 4, 4, 1, 0.5);
		gbAdd(datePicker, 4, 4, 5, 1, 0.5);
		gbAdd(datePicker2, 9, 4, 5, 1, 0.5);
		gbAdd(imgLabel, 14, 4, 6, 1, 0.5);
		gbAdd(btnSearch,20, 4, 4, 1, 0.5);
		// clientPath label
		gbAdd(new JLabel("Client Path"), 0, 5, 24, 1, 0.5);
		// clientPath textAear
		txtAreaClient = new JTextArea(10, 20);
		txtAreaClient.setEditable(false);
		JScrollPane scrollPane  = new JScrollPane(txtAreaClient);
		
		gbAdd(scrollPane, 0, 6, 24, 1, 15);
		// convert button
		btnConvert = new JButton("변환");
		btnConvert.setEnabled(false);
		
		gbAdd(btnConvert, 0, 7, 24, 1, 0.5);
		// serverPath label, copy button
		btnCopy = new JButton("복사");
		btnCopy.setEnabled(false);
		
		gbAdd(new JLabel("Server Path"), 0, 8, 20, 1, 0.5);
		gbAdd(btnCopy, 16, 8, 4, 1, 0.5);
		
		btnFileOut = new JButton("파일생성");
		btnFileOut.setEnabled(false);
		gbAdd(btnFileOut, 20, 8, 4, 1, 0.5);
		
		// serverPath textAear
		txtAreaServer = new JTextArea(10, 20);
		txtAreaServer.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(txtAreaServer);
		gbAdd(scrollPane2, 0, 9, 24, 1, 15);

		lableUpdateDate = new JLabel("");
		gbAdd(lableUpdateDate, 0, 10, 20, 1, 0.5);
		
		btnNewVersionDownlaod = new JButton("최신버전 다운로드");
	//	btnNewVersionDownlaod.setEnabled(false);
		gbAdd(btnNewVersionDownlaod, 20, 10, 4, 1, 0.5);
		
		
		for(int i = 0; i < 24; i++) {
			gbAdd(new JLabel(), i,  11, 1, 1, 0.1);
		}
	}
	
	private void setMenuBar() {
		menuBar = new JMenuBar(); //메뉴바 초기화
		menu = new JMenu("Help");
		
		menuBar.add(menu);
		
		JMenu subMenu = new JMenu("문의");
		menuItem = new JMenuItem("제해준 주임");
		subMenu.add(menuItem);
		
		menu.add(subMenu);
		
		mainFrame.setJMenuBar(menuBar); //프레임에 메뉴바 설정
	}
	
	private void gbAdd(Component c, int x, int y, int w, int h, double wit) {
		gbc.gridx = x;
		gbc.gridy = y;
		// 가장 왼쪽 위 gridx, gridy값은 0
		gbc.gridwidth = w; // 넓이
		gbc.gridheight = h; // 높이
		gbc.weighty = wit;

		gbl.setConstraints(c, gbc); // 컴포넌트를 컴포넌트 위치+크기 정보에 따라 GridBagLayout에 배치

		mainFrame.add(c);

	}

	public void getNewVersionData() {
		lableUpdateDate.setText("최근 업데이트 일자 : 소켓 통신 대기중..");
		
	try (Socket socket2 = new Socket("10.254.241.154", 9999);
				BufferedReader br = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			) {
			versionData = br.readLine();
			String[] vData = versionData.split("/");
			lableUpdateDate.setText("최근 업데이트 일자 : " + vData[0]);
			//mainFrame.setTitle("적용목록 " + vData[1]);
			
			if(nowVersion < Integer.parseInt(vData[1])) {
				int result = JOptionPane.showConfirmDialog(null, "최신 버전이 존재합니다.\n다운로드 하시겠습니까?", "알림", JOptionPane.YES_NO_OPTION);
				
				if (result == JOptionPane.YES_OPTION) {
					btnNewVersionDownlaod.doClick();
				}
			}
			
			tempException = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			lableUpdateDate.setText("최근 업데이트 일자 : 소켓 서버 에러");
			btnNewVersionDownlaod.setEnabled(false);
			
			tempException = e;
			//showErrorDialog(e);
		}
	}
	
	private void showErrorDialog(Exception e) {
		StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        JOptionPane.showInputDialog(null, "error log : ", "알림", 0, null, null, errors.toString());
	}
	
	public void callBackSVN(List<LogDTO> SVNLogs) {
	//	imgLabel.setVisible(false);
		imgLabel.setIcon(imgHide);
		btnSearch.setEnabled(true);

		btnConvert.setEnabled(true);
		btnFileOut.setEnabled(true);
		btnCopy.setEnabled(true);
		
		StringBuffer stb = new StringBuffer();

		for(LogDTO log : SVNLogs) {
			stb.append("\n" + "[" + Formater.typeFormat(log.getType()) + " " + Formater.dateFormat(log.getDate()) + "] " + log.getPath() + ('D' != log.getType() ? "" : " - 확인필요(삭제된 파일)"));
		}
		if(stb.length() != 0) {
			txtAreaClient.setText(stb.substring(1));
		}else {
			txtAreaClient.setText("조회 내역이 없습니다.");
		}
	}
	
	private void buttonAction() {
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(mainFrame, "ㅎㅇㅎㅇ", "알림", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		btnJoin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				svnModule = new SvnModule.Builder()
						       .svnUrl(comboSvnUrl.getSelectedItem().toString())
						       .id(txtFieldId.getText())
						       .pwd(String.valueOf(txtFieldPwd.getPassword()))
						       .callBackInstance(updateListFrame)
						       .bulid();
				try {
					svnRepo = svnModule.connectSVN();
					btnJoin.setText("SVN 연결성공");
					btnJoin.setEnabled(false);
					btnSearch.setEnabled(true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainFrame, "SVN 접속에 실패하였습니다.", "알림", JOptionPane.ERROR_MESSAGE);
					showErrorDialog(e1);
				}
			}
		});
		
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				//	imgLabel.setVisible(true);
					imgLabel.setIcon(imgIndicator);
					btnSearch.setEnabled(false);
					btnConvert.setEnabled(false);
					btnFileOut.setEnabled(false);
					btnCopy.setEnabled(false);
					 // datePicker.getJFormattedTextField().getText()
				/*	SvnLogCaster logCaster =  new SvnLogCaster.Builder()
							                 .svnRepository(svnRepo)
							                 .searchStartDate(Integer.toString(model.getYear()) + "-" + Integer.toString(model.getMonth() + 1) + "-" + Integer.toString(model.getDay()))
							                 .searchEndDate(Integer.toString(model2.getYear()) + "-" + Integer.toString(model2.getMonth() + 1) + "-" + Integer.toString(model2.getDay()))
							                 .svnUserId(txtFieldId.getText())
							                 .callBackInstance(updateListFrame)
							                 .bulid();
					logCaster.start();*/

					svnModule.setStrDt(Integer.toString(model.getYear()) + "-" + Integer.toString(model.getMonth() + 1) + "-" + Integer.toString(model.getDay()));
					svnModule.setEndDt(Integer.toString(model2.getYear()) + "-" + Integer.toString(model2.getMonth() + 1) + "-" + Integer.toString(model2.getDay()));
					
					Thread thread = new Thread(svnModule);
					thread.start();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					//JOptionPane.showMessageDialog(mainFrame, "처리중 에러가 발생하였습니다.", "알림", JOptionPane.ERROR_MESSAGE);
					
					showErrorDialog(e1);
				}
			}
		});
		

		btnConvert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String SVNUrl = comboSvnUrl.getSelectedItem().toString();
					String rootPath = SVNUrl.substring(SVNUrl.lastIndexOf("/"), SVNUrl.length());
					PathConvertible converter;
					
					converter = new JexThirdPathConverterImpl(txtAreaClient.getText(), rootPath);
					
					/*if(SVNUrl.indexOf("10.254.241.174") > -1) {			// jex 3.0
						converter = new JexThirdPathConverterImpl(txtAreaClient.getText(), rootPath);
						
					}else if(SVNUrl.indexOf("10.254.241.173") > -1) {	// jex 2.0
						converter = new JexSecondPathConverterImpl(txtAreaClient.getText(), rootPath);
						
					}else {												// taxbill
						converter = new TaxbillPathConverterImpl(txtAreaClient.getText(), rootPath);
					}*/
					
					txtAreaServer.setText(converter.convert());

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
					showErrorDialog(e1);
				}
			}
		});
		
		btnCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					StringSelection stringSelection = new StringSelection(txtAreaServer.getText());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
					
					JOptionPane.showMessageDialog(mainFrame, "복사되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					showErrorDialog(e1);
				}
			}
		});
		
		btnFileOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(txtAreaServer.getText().indexOf("삭제") > -1) {
					JOptionPane.showMessageDialog(mainFrame, "목록 중 삭제이력이 있습니다. \n파일생성 후 확인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
				}
				
				// TODO Auto-generated method stub
				try {
					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter fiter = new FileNameExtensionFilter(".txt", "txt");
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.setFileFilter(fiter);
					int result = chooser.showSaveDialog(null);
					
					if (result == JFileChooser.APPROVE_OPTION) {
						SvnLogFileWriter slfw = new SvnLogFileWriter(txtAreaServer.getText(), chooser.getSelectedFile().getPath() + chooser.getFileFilter().getDescription());
						slfw.write();
						
						JOptionPane.showMessageDialog(mainFrame, "파일이 정상적으로 생성되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
					} else if (result == JFileChooser.CANCEL_OPTION) {
						
					}

					
				} catch (Exception e2) {
					// TODO: handle exception
					showErrorDialog(e2);
				}
			}
		});
		
		comboSvnUrl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				btnJoin.setText("SVN 연결");
				btnJoin.setEnabled(true);
				btnSearch.setEnabled(false);
				if(svnRepo != null) {
					svnRepo.closeSession();
					svnRepo = null;
				}
			}
		});
		
		txtFieldPwd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnJoin.doClick();
				}
			}
			
		});
		
		btnNewVersionDownlaod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter fiter = new FileNameExtensionFilter(".jar", "jar");
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(fiter);
				
				int result = chooser.showSaveDialog(null);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					//String fileName = "updateList.jar";
					String serverIP = "10.254.241.154";
					int port = 8888;

				    try (
				      Socket socket = new Socket(serverIP, port);
				      DataInputStream bin = new DataInputStream(socket.getInputStream());
				      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				      BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(new File(chooser.getSelectedFile().getPath() 
				    		  + (chooser.getSelectedFile().getPath().indexOf(".jar") > -1 ? "" : chooser.getFileFilter().getDescription()))));
				    ) {
				      long size = bin.readLong();		// 전송받을 데이터의 총 크기
				      
				      //System.out.println(size);
				      
				      int readed = 0;
				      byte [] b = new byte[10000];
				      while(true) {
				        readed = bin.read(b);
				        bout.write(b, 0, readed);
				        size-=readed;
						if(size==0) {
				          break;
				        }
				      }
				      //System.out.println("파일 전송 완료!!");
				      JOptionPane.showMessageDialog(mainFrame, "다운로드 완료.", "알림", JOptionPane.INFORMATION_MESSAGE);
				      
				      bw.write("success!!");
				      bw.flush();
				    } catch (Exception e3) {
				      e3.printStackTrace();
				    }
				} else if (result == JFileChooser.CANCEL_OPTION) {
					
				}
			}
		});
		
		lableUpdateDate.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(tempException != null) {
					showErrorDialog(tempException);
				}
			}
		});
	}

}