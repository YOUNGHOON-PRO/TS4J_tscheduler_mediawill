package com.tscheduler.vest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.yettiesoft.vestmail.VMCipherImpl;
import com.yettiesoft.vestmail.VMUtil;
import com.yettiesoft.vestmail.pdf.CreateSignature;
import com.yettiesoft.vestmail.pdf.VMPdfImpl;
import com.yettiesoft.javarose.SGException;


public class makePDFSign {
	
	
	public static void main(String[] args) {
		
		// ?ù∏Ï¶ùÏÑú ?ú†?ö®?Ñ± Í≤??Ç¨Î•? ?ïòÏß? ?ïä?ùÑ Í≤ΩÏö∞ false
		CreateSignature.validCheck = false;
		
		String pfxFile = "./template/koscom-test.pfx";
		String pin = "12qwaszx23!";
		String pdfFile = "./template/policy.pdf";
		String outFile = "./sample/output/policy_signed.pdf";

		String name = "?òà?ã∞?Üå?îÑ?ä∏"; 
		String reaeson = "?†Ñ?ûê?ÑúÎ™? ?Öå?ä§?ä∏ ?ö©";
		
		VMPdfImpl pdfSign = null;
		try{
			pdfSign = new VMPdfImpl();
			
			// sample 1
	//		KeyStore keystore = KeyStore.getInstance("PKCS12");
	//		keystore.load(new FileInputStream(pfxFile), pin.toCharArray());
	//		File inFile = new File(pdfFile);
	//      PDDocument doc = PDDocument.load(inFile);
	//		byte[] out = pdfSign.makePDFSignature(keystore, pin, doc, "", "");
			
			// sample 2
			byte[] out = pdfSign.makePDFSignature(pfxFile, pin, pdfFile, name, reaeson);
			
			// sample 3
//			byte[] bPfx = VMUtil.readBinaryFile(pfxFile);
//			byte[] bPdf = VMUtil.readBinaryFile(pdfFile);
//			byte[] out = pdfSign.makePDFSignature(bPfx, pin, bPdf);
	
			VMUtil.saveFile(outFile, out);
		} catch(SGException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
