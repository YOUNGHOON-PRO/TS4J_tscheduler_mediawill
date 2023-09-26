/**
 * 작성자 : 김상진
 * 작성일시 : 2021.07.06
 * 설명 : 문자열을 암호화 처리
 */
package com.tscheduler.util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

//import org.apache.poi.util.SystemOutLogger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.StringFixedSaltGenerator;
//import com.util.EncryptUtil;
//import messager.center.creator.FetchException;
//import messager.center.creator.FetchException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EncryptUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(EncryptUtil.class.getName());
	
	//public  Logger logger = Logger.getLogger(EncryptUtil.class);
	
	/**
	 * 문자열을 SHA256으로 암호화(해싱)한다.
	 * @param str
	 * @return
	 */
	public  String getEncryptedSHA256(String str) {
		String result = "";
		if(str == null) {
			return "";
		} else {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				digest.reset();
				digest.update(str.getBytes());
				byte[] hash = digest.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < hash.length; i++) {
					sb.append(Integer.toString((hash[i]&0xff) + 0x100, 16).substring(1));
				}
				result = sb.toString();
			} catch (NoSuchAlgorithmException nsae) {
				LOGGER.error(nsae);
				result = str;
			}
			return result;
		}
	}
	
	/**
	 * 문자열을 Jasypt library로 암호화한다.(가변)
	 * @param algorithm
	 * @param password
	 * @param str
	 * @return 
	 * @return
	 */
	public static  String getJasyptEncryptedString(String algorithm, String password, String str) {
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setAlgorithm(algorithm);
			encryptor.setPassword(password);
			return encryptor.encrypt(str);
		} catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
			//logger.error("getJasyptEncryptedString error = " + e);
			return "";
		}
	}

	/**
	 * 문자열을 Jasypt library로 복호화한다.(가변)
	 * @param algorithm
	 * @param password
	 * @param str
	 * @return
	 * @throws FetchException 
	 */
	public static  String getJasyptDecryptedString(String algorithm, String password, String str) throws Exception {
		try {
			
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setAlgorithm(algorithm);
			encryptor.setPassword(password);
			return encryptor.decrypt(str);
		} catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
			return "error";
		}
		
	}
	
	/**
	 * 문자열을 Jasypt library로 암호화한다.(고정)
	 * @param algorithm
	 * @param password
	 * @param str
	 * @return
	 */
	public static String getJasyptEncryptedFixString(String algorithm, String password, String str) {
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setAlgorithm(algorithm);
			encryptor.setPassword(password);
			encryptor.setSaltGenerator(new StringFixedSaltGenerator(password));
			return encryptor.encrypt(str);
		} catch(Exception e) {
			LOGGER.error(e);
			//logger.error("getJasyptEncryptedUnFixString error = " + e);
			return str;
		}
	}
	
	/**
	 * 문자열을 Jasypt library로 복호화한다.(고정)
	 * @param algorithm
	 * @param password
	 * @param str
	 * @return
	 * @throws FetchException 
	 */
	public static String getJasyptDecryptedFixString(String algorithm, String password, String str) throws Exception {
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setAlgorithm(algorithm);
			encryptor.setPassword(password);
			encryptor.setSaltGenerator(new StringFixedSaltGenerator(password));
			return encryptor.decrypt(str);
		} catch(Exception e) {
			LOGGER.error(e);
			//e.printStackTrace();
			
			return str;
		}
	}
	
	/**
	 * 문자열을 Base64로 인코딩한다.
	 * @param str
	 * @return
	 */
	public  String getBase64EncodedString(String str) {
		try {
			Encoder encoder = Base64.getEncoder();
			return new String(encoder.encode(str.getBytes()));
		} catch(Exception e) {
			LOGGER.error(e);
			//logger.error("getBase64EncodedString Error = " + e.getMessage());
			return str;
		}
	}
	
	/**
	 * 문자열을 Base64로 디코딩한다.
	 * @param str
	 * @return
	 */
	public  String getBase64DecodedString(String str) {
		try {
			Decoder decoder = Base64.getDecoder();
			return new String(decoder.decode(str.getBytes()));
		} catch(Exception e) {
			LOGGER.error(e);
			//logger.error("getBase64DecodedString Error = " + e.getMessage());
			return str;			
		}
	}
	
	
	public static void main(String args[]) throws Exception  {
		
		String ALGORITHM = "PBEWithMD5AndDES";
		String KEYSTRING = "ENDERSUMS";
		//String KEYSTRING = "NOT_RNNO";
		
		EncryptUtil enc =  new EncryptUtil();
		String enc_data1 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "jdbc:oracle:thin:@127.0.0.1:1521:xe");
		String enc_data2 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "ums");
		String enc_data3 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "enders1!@");
		
		String enc_data4 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "hun1010616@naver.com");
		String enc_data5 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "hun1110@hanmail.net");
		String enc_data6 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "hun1010616@gmail.com");
		String enc_data7 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "hun1110@enders.co.kr");
		String enc_data8 = enc.getJasyptEncryptedFixString(ALGORITHM, KEYSTRING, "hun1010616@nate.com");
		
		LOGGER.info("enc_data1 : " +enc_data1);
		LOGGER.info("enc_data2 : " +enc_data2);
		LOGGER.info("enc_data3 : " +enc_data3);
		LOGGER.info("enc_data4 : " +enc_data4);
		LOGGER.info("enc_data5 : " +enc_data5);
		LOGGER.info("enc_data6 : " +enc_data6);
		LOGGER.info("enc_data7 : " +enc_data7);
		LOGGER.info("enc_data8 : " +enc_data8);
		LOGGER.info("");
		
		String dec_data1 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data1);
		String dec_data2 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data2);
		String dec_data3 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data3);
		String dec_data4 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data4);
		String dec_data5 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data5);
		String dec_data6 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data6);
		String dec_data7 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data7);
		String dec_data8 = enc.getJasyptDecryptedFixString(ALGORITHM, KEYSTRING, enc_data8);

		LOGGER.info("dec_data1 : " +dec_data1);
		LOGGER.info("dec_data2 : " +dec_data2);
		LOGGER.info("dec_data3 : " +dec_data3);
		LOGGER.info("dec_data4 : " +dec_data4);
		LOGGER.info("dec_data5 : " +dec_data5);
		LOGGER.info("dec_data6 : " +dec_data6);
		LOGGER.info("dec_data7 : " +dec_data7);
		LOGGER.info("dec_data8 : " +dec_data8);
		
	}

}
