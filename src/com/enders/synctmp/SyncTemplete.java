package com.enders.synctmp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SyncTemplete {
	
	private static final Logger LOGGER = LogManager.getLogger(SyncTemplete.class.getName());
	
	//파일트렌스퍼가 실행되는 곳의 템플릿 위치
//	static String filetransfer_Path =  "addressfile/test02/";
//	static String rootPath = "C:\\test\\";
//	static String tempPath = "C:\\Temp\\";
//	static String backupPath = "C:\\bk\\";

	//파일트렌스퍼에서 파일을 받아온다
	private static  void File_write(String str) {
		ConfigLoader.load();
		 String filetransfer_Path = ConfigLoader.getString("filetransfer.path", "");
		 String tempPath = ConfigLoader.getString("temp.path", "");
		 
		filetransfer_Path = filetransfer_Path+str;
		FileRequester requester = null;
	
        try {
        	requester = new FileRequester();
        	//파일 만들기
			InputStream stream = requester.request(filetransfer_Path);
			FileOutputStream fos = new FileOutputStream(tempPath+str);
		
			
			if(stream!=null) {
				while(true) {
				    int data = stream.read();
				    if(data == -1) {
				        break;
				    }
				    fos.write(data);
				}
			}
			
			if(stream!=null) {
			stream.close();
			}
			if(fos!=null) {
			fos.close();
			}
			
		} catch (IOException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

	
	public static Boolean syncTemplete (String str){
		
		 String rootPath = ConfigLoader.getString("root.path", "");
		 String tempPath = ConfigLoader.getString("temp.path", "");
		 String backupPath = ConfigLoader.getString("backup.path", "");
		
		boolean rst =false;
		String fileNmae = str;
		
		//파일 가져와서 임시폴더에 저장
		File_write(fileNmae);
		
		//기존거 가져와서 비교
		File tempFile = new File(tempPath+fileNmae);
		
		//기존거 가져와서 비교
		File rootFile = new File(rootPath+fileNmae);
		
		//날짜시간 추력
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formatted = current.format(formatter);
        //System.out.println("Current: " + formatted);
        
        
        boolean compare = true;
        
        
        if(tempFile.exists() && rootFile.exists()) { //파일존재하면
        	  BufferedReader reader = null;
      		try {
      			reader = new BufferedReader(new FileReader(tempFile));
      		} catch (FileNotFoundException e1) {
      			LOGGER.error(e1);
      			// TODO Auto-generated catch block
      			//e1.printStackTrace();
      		}
              BufferedReader reader2 = null;
      		try {
      			reader2 = new BufferedReader(new FileReader(rootFile));
      		} catch (FileNotFoundException e1) {
      			LOGGER.error(e1);
      			// TODO Auto-generated catch block
      			//e1.printStackTrace();
      		}

              String data = null;
              String data2 = null;
             
              try {
      			while((data = reader.readLine()) != null) { // 읽을게 없으면 null 리턴
      			    data2 = reader2.readLine();
      			    if (data2 == null) {
      			        break;
      			    }
      			    
      			    if (!data.equals(data2)) {
      			    	compare = false;
      			    	  //System.out.println("불일치" + data);
      			    	  //System.out.println("불일치2" + data2);
      			    }
      			}
      		} catch (IOException e1) {
      			LOGGER.error(e1);
      			// TODO Auto-generated catch block
      			//e1.printStackTrace();
      		}
              try {
      			reader.close();
      		} catch (IOException e1) {
      			LOGGER.error(e1);
      			// TODO Auto-generated catch block
      			//e1.printStackTrace();
      		}
              try {
      			reader2.close();
      		} catch (IOException e1) {
      			LOGGER.error(e1);
      			// TODO Auto-generated catch block
      			//e1.printStackTrace();
      		}
        }
        
      //##################################	
		//기존거랑 같으면 패쓰
      //##################################	
        if ( tempFile.getName().equals(rootFile.getName()) && tempFile.length()== rootFile.length() && compare==true ) {
        	
        	//원격서버에 템플릿이 없을 경우
        	if(tempFile.length()==0 && rootFile.length()==0 ) {
        		System.out.println("원격지 서버에 없는 템플릿이다.");
        	}else {
        		Path filePathToMove2 = Paths.get(tempPath+fileNmae);
            	
    			System.out.println("템플릿이 같다");
//    			System.out.println("tempFile.length() : " +tempFile.length());
//    			System.out.println("rootFile.length() : " +rootFile.length());
//    			System.out.println("tempFile.getName() : " +tempFile.getName());
//    			System.out.println("rootFile.getName() : " +rootFile.getName());

            	try {
    				Files.delete(filePathToMove2);
    			} catch (IOException e) {
    				LOGGER.error(e);
    				// TODO Auto-generated catch block
    				//e.printStackTrace();
    			}
        	}
        
		//##################################	
		//기존거랑 다르면 기존거 백업파일에 복사
        //##################################
		}else {
			System.out.println("템플릿이 다르다");
//			System.out.println("tempFile.length() : " +tempFile.length());
//			System.out.println("rootFile.length() : " +rootFile.length());
//			System.out.println("file.getName() : " +tempFile.getName());
//			System.out.println("rootFile.getName() : " +rootFile.getName());
			
			//기존 파일을 백업 폴더로 복사 
		    Path filePath = Paths.get(rootPath+fileNmae);
		    Path filePathToMove = Paths.get(backupPath+fileNmae+"_"+formatted);
		    
		    
		    if(tempFile.exists() && rootFile.exists()) { //파일존재하면
		    	try {
					Files.copy(filePath, filePathToMove);
					System.out.println("기존파일 백업 완료.");
				} catch (IOException e) {
					LOGGER.error(e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		    }else { //파일 없으면
		    	
		    }
		    
		  //##################################	
		  //신규 파일로 업데이트  
		  //##################################	
		    Path filePath2 = Paths.get(tempPath+fileNmae);
		    Path filePathToMove2 = Paths.get(rootPath+fileNmae);
		    
		  
		    if(rootFile.exists()) { //파일존재하면 
			    try {
			    	Files.delete(filePathToMove2);
			    	Files.move(filePath2, filePathToMove2);
					System.out.println("신규파일 업데이트 완료.");
					rst = true;
				} catch (IOException e) {
					LOGGER.error(e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		    }
		    else { //파일 없으면
			    try {
			    	Files.move(filePath2, filePathToMove2);
					System.out.println("신규파일 업데이트 완료.");
					rst = true;
				} catch (IOException e) {
					LOGGER.error(e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
		    }
		    
		}
		
		return rst;
	}
	
	public void sync (String str) {
		ConfigLoader.load();
		syncTemplete(str);
		
	}
	
	public static void main(String[] args) {
		ConfigLoader.load();
		String fileNmae ="회원가입2.html";
		syncTemplete(fileNmae);
		
		
	
	}




}
