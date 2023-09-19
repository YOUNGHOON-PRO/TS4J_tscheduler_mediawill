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
import com.yettiesoft.javarose.SGException;


public class makeMail {
	private static int		VM_FLAG_TEXTMSG				= 0x0001;
	private static int		VM_FLAG_STD_PDF				= 0x0002;
	private static int		VM_FLAG_STD_ZIP				= 0x0004;
	private static int		VM_FLAG_TEXTMSG_TEMPLATE	= 0x0008;
	private static int		VM_FLAG_PDF_TEMPLATE		= 0x0010;
	private static int		VM_FLAG_BIN_TEMPLATE		= 0x0020;
	private static int		VM_FLAG_BIN_MSG_TEMPLATE		= 0x0040;
	private static int		VM_FLAG_MBIN_MSG_TEMPLATE		= 0x0080;
	private static int		VM_FLAG_OFFICE_TEMPLATE		= 0x0100;
	
	public static void main(String[] args) {
		int			aFlag = 0;
		String		aKeyParam = "123456";
		
		//String 		contentFilePath = "./template/ktemail.html";
		String 		contentFilePath = "./template/uplusemail.html";
		String		contentFileEncoding = "utf-8";		
		/************************************************************************************************/
		/* STEP.1 check args */
		if (args.length == 0) {
			aFlag	|= VM_FLAG_TEXTMSG;
			aFlag	|= VM_FLAG_STD_PDF;
			aFlag	|= VM_FLAG_STD_ZIP;
			aFlag	|= VM_FLAG_TEXTMSG_TEMPLATE;
			aFlag	|= VM_FLAG_PDF_TEMPLATE;
			aFlag	|= VM_FLAG_BIN_TEMPLATE;
			aFlag	|= VM_FLAG_BIN_MSG_TEMPLATE;
			aFlag	|= VM_FLAG_MBIN_MSG_TEMPLATE;
			aFlag	|= VM_FLAG_OFFICE_TEMPLATE;
			
		}
		else {
			int	i = 0;
			while (i < args.length) {
				switch (Integer.parseInt(args[i])) {
				case 1:
					aFlag	|= VM_FLAG_TEXTMSG;
					break;
				case 2:
					aFlag	|= VM_FLAG_STD_PDF;
					break;
				case 3:
					aFlag	|= VM_FLAG_STD_ZIP;
					break;
				case 4:
					aFlag	|= VM_FLAG_TEXTMSG_TEMPLATE;
					break;
				case 5:
					aFlag	|= VM_FLAG_PDF_TEMPLATE;
					break;
				case 6:
					aFlag	|= VM_FLAG_BIN_TEMPLATE;
					break;
				case 7:
					aFlag	|= VM_FLAG_BIN_MSG_TEMPLATE;
					break;
				case 8:
					aFlag	|= VM_FLAG_MBIN_MSG_TEMPLATE;
					break;
				case 9:
					aFlag	|= VM_FLAG_OFFICE_TEMPLATE;
					break;
					
				}
				i++;
			}
		}
				
		
		
		VMCipherImpl aCipherInterface = null;
		
		// �������� ������ ����ϴ� ���
		/*
		String configPath = "./properties/vestmail.properties";
		aCipherInterface = new VMCipherImpl(configPath);
		// ���� ���� �������� �����ϴ� ���
		aCipherInterface.setConfig(configPath);
		*/
		try {
			aCipherInterface = new VMCipherImpl();

			/**
			 * STEP.1 �Ϲ� HTML ��ȣȭ.
			 * �Ϲ� HTML ��ȣȭ�� ������ �̸� ���ǵ� ���ø��� ���ڵ����� �̿��ϴ� �������
			 * ������ ��ġ�� ���� �Է��ϰų� ���� ��Ʈ���� �Ѱ��ִ� ����� �����Ѵ�.
			 */
			if ( (aFlag & VM_FLAG_TEXTMSG) != 0) {
				/* ������ ��ġ�� ���ڷ� �Ѱ��ִ� ��� */
				
				String encMail = aCipherInterface.makeMailFile(aKeyParam, contentFilePath);
				try {
					saveFile("./sample/output/htmlfile_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/* ���� ��Ʈ���� �̸� �а� �Ѱ��ִ� ��� */
				String plainMail;
				try {
					plainMail = VMUtil.readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContent (aKeyParam, plainMail);
					saveFile("./sample/output/htmlctx_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			/**
			 * STEP.2 ���ø��� �̿��� HTML ��ȣȭ
			 * �Ϲ� HTML ��ȣȭ�� ������ ����� �����ϸ� ���ø��� �������� �Է��� �� �ִ�.
			 * ���ø� ���� ������ ��ġ �� ��Ʈ���� ���� �Է��� �� �ִ�.
			 */
			if ( (aFlag & VM_FLAG_TEXTMSG_TEMPLATE) != 0) {
				/* �ϳ��� ���ø����� �Է¹޴� ��� */
				String encMail = aCipherInterface.makeMailFileWithTemplate (aKeyParam,
																			contentFilePath,
																			"./template/htmlcipher/template.html");
				try {
					saveFile("./sample/output/template_file_type_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/* ���ø��� head, body, css�� �и��ؼ� �ܺηκ��� �Է¹޴� ���. */
				/*
				encMail = aCipherInterface.makeMailFileWithTemplate (aKeyParam,
																	 contentFilePath,
																	 null, 
																	 "./template/htmlcipher/template_head.html",
																	 "./template/htmlcipher/template_body.html",
																	 null);
				try {
					saveFile("./sample/output/template_file_type2_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
				*/
				/* ���� ��Ʈ������ �Է¹޴� ��� */
				String plainMail, templateType1, templateType2Head, templateType2Body;
				try {
					plainMail			= readFile (contentFilePath, contentFileEncoding);
					templateType1		= readFile ("./template/htmlcipher/template.html", contentFileEncoding);
					templateType2Head	= readFile ("./template/htmlcipher/template_head.html", contentFileEncoding);
					templateType2Body	= readFile ("./template/htmlcipher/template_body.html", contentFileEncoding);

					encMail = aCipherInterface.makeMailContentWithTemplate (aKeyParam, plainMail, templateType1);
					saveFile ("./sample/output/template_ctx_type_enc.html", encMail.getBytes(), contentFileEncoding);
					/*
					encMail = aCipherInterface.makeMailContentWithTemplate (aKeyParam, plainMail, null, templateType2Head, templateType2Body, null);
					saveFile ("./sample/output/template_ctx_type2_enc.html", encMail.getBytes(), contentFileEncoding);
					*/
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			/**
			 * STEP.3 ǥ�� PDF ��ȣȭ
			 * ǥ�� PDF ��ȣȭ�� �̿��� PDF ǥ�� ��ȣȭ ����� �����Ѵ�.
			 * PDF ������ �����Ҷ� ����� �� �ִ�.
			 * ������ ���԰��ݿ� ����� �� �ִ�.
		 	 * @param aKeyParam: ��ȣȭ PDF ������ ���� ��й�ȣ
			 * @param aPath: ��ȣȭ PDF ������ ���

			 */
//			if ( (aFlag & VM_FLAG_STD_PDF) != 0) {
//				//long beforeTime = System.currentTimeMillis();
//				byte[] encPDF = aCipherInterface.makeEncryptedPDF(aKeyParam, "./template/test.pdf");
//				//long afterTime = System.currentTimeMillis(); 
//				//long secDiffTime = (afterTime - beforeTime);
//				//System.out.println("makeEncryptedPDF �ð�����(ms) : "+secDiffTime);
//				try {
//					saveFile("./sample/output/standard_pdfenc.pdf", encPDF);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				//beforeTime = System.currentTimeMillis(); 
//				encPDF = aCipherInterface.makeEncryptedPDFAES(aKeyParam, "./template/test.pdf");
//				//afterTime = System.currentTimeMillis(); 
//				//secDiffTime = (afterTime - beforeTime);
//				//System.out.println("makeEncryptedPDFAES �ð�����(ms) : "+secDiffTime);
//				
//				try {
//					saveFile("./sample/output/standard_pdfenc_aes.pdf", encPDF);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				
//			}

			/**
			 * STEP.4 ǥ�� ZIP ��ȣȭ
			 * ǥ�� ZIP ��ȣȭ ����� �����Ѵ�.
			 * PDF�� ������ Ÿ ���̳ʸ�(doc, xls, hwp�� ����) ������ ��ȣȭ�Ҷ� ����� �� �ִ�.
			 * ������ ���԰��ݿ� ����� �� �ִ�.
			 */
			if ( (aFlag & VM_FLAG_STD_ZIP) != 0) {
				byte[] encZIP = aCipherInterface.makeEncryptedZip(aKeyParam, "./template/policy.doc");
				try {
					saveFile("./sample/output/standard_zipenc.zip", encZIP);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			/**
			* STEP.5 ��ȣȭ pdf ������ �����Ѵ�.
			* STEP.3�� �ٸ� ���� pdf.js�� �̿��� ��ȣȭ ����� �����Ѵ�.
			*/
			/**
			* STEP.5-1 ���� ��θ� �Է¹޴� ���
			* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			* @param aFilePath: ������ �ʿ��� pdf ������ ���
			* @param aTemplatePath: ���� ������ �����ϴµ� �ʿ��� ���ø� ������ ���
			* @param file_encoding: char file_encoding
			*/

			if ( (aFlag & VM_FLAG_PDF_TEMPLATE) != 0) {
				String encBin = aCipherInterface.makeAttachFileWithTemplate (aKeyParam,
																			 "./template/policy.pdf",
																			 "./template/bincipher_pdfjs/template.html",
																			 "utf-8");
				try {
					saveFile("./sample/output/pdfjs_enc.html", encBin.getBytes("utf-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String	aFileName = "plain.pdf";
				byte[]	aBin;
				String	aTemplate;
				
				try {
					aTemplate = readFile ("./template/bincipher_pdfjs/template.html", "utf-8");
					aBin = readBinaryFile ("./template/policy.pdf");
					encBin = aCipherInterface.makeAttachFileWithTemplate(aKeyParam, aFileName, aBin, aTemplate);
					saveFile("./sample/output/pdfjs_ctx_enc.html", encBin.getBytes("utf-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
//			/**
//			* STEP.6 ��ȣȭ ���� HTML�� �����Ѵ�.
//			*/
//			/**
//			* STEP.6-1 ���� ��θ� �Է¹޴� ���
//			* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
//			* @param aFilePath: ������ �ʿ��� ������ ���
//			* @param aTemplatePath: ���� ������ �����ϴµ� �ʿ��� ���ø� ������ ���
//			*/
//			if ( (aFlag & VM_FLAG_BIN_TEMPLATE) != 0) {
//				String encBin = aCipherInterface.makeAttachFileWithTemplate (aKeyParam,
//																			 "./template/policy.pdf",
//																			 "./template/htmlcipher/template.html");
//				try {
//					saveFile("./sample/output/bin_enc.html", encBin.getBytes(), contentFileEncoding);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				/**
//				* STEP.6-2 ���� ��Ʈ���� �Է¹޴� ���
//				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
//				* @param aFileName: ��ȣȭ �� ������ ���ϸ�
//				* @param aBin: ��ȣȭ�� ����
//				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø�
//				*/
//
//				String	aFileName = "plain.pdf";
//				byte[]	aBin;
//				String	aTemplate;
//				
//				try {
//					aTemplate = readFile ("./template/htmlcipher/template.html", contentFileEncoding);
//					aBin = readBinaryFile ("./template/policy.pdf");
//
//					encBin = aCipherInterface.makeAttachFileWithTemplate(aKeyParam, aFileName, aBin, aTemplate);
//					saveFile("./sample/output/bin_ctx_enc.html", encBin.getBytes(), contentFileEncoding);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}

			/**
			 * STEP.7 ���ø��� �̿��� HTML + ÷������ ��ȣȭ
			 * �Ϲ� HTML ��ȣȭ�� ������ ����� �����ϸ� ���ø��� �������� �Է��� �� �ִ�.
			 * ���ø� ���� ������ ��ġ �� ��Ʈ���� ���� �Է��� �� �ִ�.
			 * ÷�������� ��ġ �� ���ϸ� �� �Է��� �����ϴ�.
			 * @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 * @param plainMail: ������ �ʿ��� html ���� ����
			 * @param aFilePath: ��ȣȭ �� ������ ���
			 * @param file_encoding: char file_encoding
			 * @Param fileName : ÷������ �̸�, ""�� aFilePath �� ���ϸ��� ����
			 * @Param aBin : ÷������ binary, aFilePath�� �Բ� ��� �Ұ�
			 */
			if ( (aFlag & VM_FLAG_BIN_MSG_TEMPLATE) != 0) {
				
				String	aFileName = "./template/policy.pdf";
				byte[]	aBin;
				String	aTemplate = "./template/htmlcipher/template.html";
				String  encMail;
								
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContentAttachFile(aKeyParam, plainMail, aFileName );
					saveFile("./sample/output/attach_ctx_enc1.html", encMail.getBytes(), contentFileEncoding);
				}catch (IOException e) {
					e.printStackTrace();
				}
				

				/**
				 * STEP.7-1 ���ø��� �̿��� HTML + ÷������ ��ȣȭ
				 * �Ϲ� HTML ��ȣȭ�� ������ ����� �����ϸ� ���ø��� �������� �Է��� �� �ִ�.
				 * ���ø� ���� ������ ��ġ �� ��Ʈ���� ���� �Է��� �� �ִ�.
				 * ÷�������� ��ġ �� ���ϸ� �� �Է��� �����ϴ�.
				 * @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
				 * @param plainMail: ������ �ʿ��� html ���� ����
				 * @param aFilePath: ��ȣȭ �� ������ ���
				 * @param file_encoding: char file_encoding
				 **/
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContentAttachFile(aKeyParam, plainMail, aFileName , contentFileEncoding);
					saveFile("./sample/output/attach_ctx_enc2.html", encMail.getBytes(), contentFileEncoding);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				/**
				* STEP.7-2 ���ø��� �̿��� HTML + ÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� 
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ���
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					aTemplate = "./template/htmlcipher/template.html";
					
					encMail = aCipherInterface.makeMailContentAttachFileWithTemplate(aKeyParam, aFileName, plainMail, aTemplate);
					saveFile("./sample/output/attach_ctx_enc3.html", encMail.getBytes(), contentFileEncoding);

				}catch (IOException e) {
					e.printStackTrace();
				}
				
				/**
				* STEP.7-3 ���ø��� �̿��� HTML + ÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� 
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ��Ʈ��
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					aTemplate = readFile ("./template/htmlcipher/template.html", contentFileEncoding);
					
					encMail = aCipherInterface.makeMailContentAttachFileWithTemplateS(aKeyParam, aFileName, plainMail, aTemplate);
					saveFile("./sample/output/attach_ctx_enc4.html", encMail.getBytes(), contentFileEncoding);

				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			/**
			 * STEP.8 �̿��� HTML + (��Ƽ)÷������ ��ȣȭ
			 * �Ϲ� HTML ��ȣȭ�� ������ ����� �����ϸ� ���ø��� �������� �Է��� �� �ִ�.
			 * ���ø� ���� ������ ��ġ �� ��Ʈ���� ���� �Է��� �� �ִ�.
			 * ÷�������� ��ġ �� ���ϸ� �� �Է��� �����ϴ�.
			 * @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 * @param plainMail: ������ �ʿ��� html ���� ����
			 * @param aFileNames: ��ȣȭ �� ������ ��� ( String array )
			 * @param file_encoding: char file_encoding
			 */
			if ( (aFlag & VM_FLAG_MBIN_MSG_TEMPLATE) != 0) {
				
				
				String[]	aFileName = {"./template/policy.pdf", "./template/policy.doc", "./template/png.png", "./template/jpg.jpg", "./template/gif.gif"};
				String[]	aFileName2 = {"policy2.pdf", "policy2.doc"};
				byte[][]	aBin;
				String	aTemplate;
				String  encMail;
								
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContentMultiAttachFile(aKeyParam, plainMail, aFileName );
					saveFile("./sample/output/attach_ctx_enc_multi1.html", encMail.getBytes(), contentFileEncoding);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContentMultiAttachFile(aKeyParam, plainMail, aFileName, contentFileEncoding);
					saveFile("./sample/output/attach_ctx_enc_multi2.html", encMail.getBytes(), contentFileEncoding);

				}catch (IOException e) {
					e.printStackTrace();
				}
				
				/**
				* STEP.8-2 ���ø��� �̿��� HTML + (��Ƽ)÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� ( String array )
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ���
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					aTemplate = "./template/htmlcipher/template.html";
					
					encMail = aCipherInterface.makeMailContentMultiAttachFileWithTemplate(aKeyParam, aFileName, plainMail, aTemplate);
					saveFile("./sample/output/attach_ctx_enc_multi3.html", encMail.getBytes(), contentFileEncoding);

				}catch (IOException e) {
					e.printStackTrace();
				}
				
				/**
				* STEP.8-3 ���ø��� �̿��� HTML + (��Ƽ)÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� ( String array )
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ��Ʈ��
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					aTemplate = readFile ("./template/htmlcipher/template.html", contentFileEncoding);
					
					encMail = aCipherInterface.makeMailContentMultiAttachFileWithTemplateS(aKeyParam, aFileName, plainMail, aTemplate);
					saveFile("./sample/output/attach_ctx_enc_multi4.html", encMail.getBytes(), contentFileEncoding);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			/**
			* STEP.9 OFFICE ������ ��ȣȭ �Ѵ�
			* office 97 �̻��� ������ ��ȣȭ �Ѵ�
			* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			* @param aFileName: ��ȣȭ �� ������ ���ϸ�
			* @param aBin: ��ȣȭ�� ����
			* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø�
			*/
//			if((aFlag & VM_FLAG_OFFICE_TEMPLATE)!= 0) {
//				
//				byte[]	aBin;
//				try {
//					aBin = readBinaryFile ("./template/policy.docx");
//
//					byte[] encBin = aCipherInterface.makeEncryptedOffice(aKeyParam, aBin );
//					saveFile("./sample/output/office_enc.docx", encBin);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				/*
//				 * office 97 ���� ������ ��ȣȭ �Ѵ�
//				 * type 1 : doc ����
//				 * type 2 : xls ����
//				 * type 3 : ppt ���� 
//				/*
//				try {
//					aBin = readBinaryFile ("./template/policy.doc");
//
//					byte[] encBin = aCipherInterface.makeEncryptedOffice(aKeyParam, aBin, 1);
//					saveFile("./sample/output/office_enc.doc", encBin);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				try {
//					aBin = readBinaryFile ("./template/test.xls");
//
//					byte[] encBin = aCipherInterface.makeEncryptedOffice(aKeyParam, aBin, 2);
//					saveFile("./sample/output/office_enc.xls", encBin);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				try {
//					aBin = readBinaryFile ("./template/test.ppt");
//
//					byte[] encBin = aCipherInterface.makeEncryptedOffice(aKeyParam, aBin, 3);
//					saveFile("./sample/output/office_enc.ppt", encBin);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				*/
//			}
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

