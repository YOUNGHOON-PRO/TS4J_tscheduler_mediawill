package com.tscheduler.vest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.yettiesoft.vestmail.VMCipherExt;
import com.yettiesoft.vestmail.VMResult;
import com.yettiesoft.vestmail.VMUtil;
import com.yettiesoft.javarose.SGException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class makeMailExt {
	
	private static final Logger LOGGER = LogManager.getLogger(makeMailExt.class.getName());
	
	public static void main(String[] args) {
		int			aFlag = 0;
		String		aKeyParam = "123456";
		
		String 		contentFilePath = "./template/uplusemail.html";
		String		contentFileEncoding = "utf-8";		
		/************************************************************************************************/
		
		VMCipherExt aCipherInterface = null;
		
		// ?��?��?��?�� 별도�? ?��?��?��?�� 경우
		/*
		String configPath = "./properties/vestmail.properties";
		aCipherInterface = new VMCipherExt(configPath);
		// ?��?�� ?��?�� ?��?��?���? �?경하?�� 경우
		aCipherInterface.setConfig(configPath);
		*/
		try {
			aCipherInterface = new VMCipherExt();

			VMResult vmresult;
			String plainMail;
			String tokenIdx;
			try {
				// ?���? token
				tokenIdx = "YTMAIL2020";
				// 메시�? ?���?
				plainMail = VMUtil.readFile (contentFilePath, contentFileEncoding);
				// ?��?��?�� 결과
				vmresult = aCipherInterface.makeMailContent (aKeyParam, plainMail, tokenIdx);
				
				saveFile("./sample/output/htmlctx_enc.html", vmresult.getEncMail().getBytes(), contentFileEncoding);
				LOGGER.info("tokenIdx  string : " + tokenIdx );
				// ?��증키
				LOGGER.info("saved key string : " + vmresult.getEncKey());
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (SGException e) {
			e.printStackTrace();
		}

		/************************************************************************************************/
	}
	
	public static void saveFile(String fname, byte[] content) throws IOException {
    	File aEncFile = new File(fname);
		FileOutputStream os = new FileOutputStream(aEncFile);
		os.write(content);
		os.close();	
	}
	
	public static void saveFile(String fname, byte[] content, String charset) throws IOException {
    	File aEncFile = new File(fname);
    	aEncFile.createNewFile();
    	
    	BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aEncFile.getPath()), charset));
		output.write(new String(content));	
		output.close();
	}
	
	public static String readFile(String fname, String charset) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(fname), charset));
		String sread = null;
		while((sread = br.readLine())!=null) {					
			sb.append(sread).append("\n");
		}
		br.close();
		return sb.toString();
	}
	
	public static byte[] readBinaryFile (String name) throws IOException {
		return readBinaryFile (new File(name));
	}
	
	public static byte[] readBinaryFile (File f) throws IOException {
		int fsize = (int) f.length();
		byte[] tmp = new byte[fsize];
		FileInputStream fi = new FileInputStream(f);
		fi.read(tmp);
		if (fi != null)
			fi.close();
		return tmp;
	}
}
