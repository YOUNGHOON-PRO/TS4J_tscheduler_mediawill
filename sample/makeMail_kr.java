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


public class makeMail_kr {
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
		String 		contentFilePath = "./template/uplusemail_kr.html";
		String 		templateFilePath =  "./template/htmlcipher_kr/template_1.html";
		String		contentFileEncoding = "euc-kr";		
		String		aTemplate;
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
		
		try {
			aCipherInterface = new VMCipherImpl();

			if ( (aFlag & VM_FLAG_TEXTMSG_TEMPLATE) != 0) {
				/* �ϳ��� ���ø����� �Է¹޴� ��� */
				String encMail = aCipherInterface.makeMailFileWithTemplate (aKeyParam,
																			contentFilePath,
																			templateFilePath);
				try {
					saveFile("./sample/output/template_file_type_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/* ���� ��Ʈ������ �Է¹޴� ��� */
				String plainMail, templateType1, templateType2Head, templateType2Body;
				try {
					plainMail			= readFile (contentFilePath, contentFileEncoding);
					templateType1		= readFile (templateFilePath, contentFileEncoding);

					encMail = aCipherInterface.makeMailContentWithTemplate (aKeyParam, plainMail, templateType1);
					saveFile ("./sample/output/template_ctx_type_enc.html", encMail.getBytes(), contentFileEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			
			
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
				String  encMail;
								
				/**
				* STEP.7-2 ���ø��� �̿��� HTML + ÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� 
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ���
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					encMail = aCipherInterface.makeMailContentAttachFileWithTemplate(aKeyParam, aFileName, plainMail, templateFilePath);
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
					aTemplate = readFile (templateFilePath, contentFileEncoding);
					
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
				String  encMail;
				
				/**
				* STEP.8-2 ���ø��� �̿��� HTML + (��Ƽ)÷������ ��ȣȭ
				* @param aKeyParam: ���ȸ��� ������ ���� ��й�ȣ
			 	* @param aFileNames: ��ȣȭ �� ������ ��� ( String array )
			 	* @param plainMail: ������ �ʿ��� html ���� ����
				* @param aTemplate: ���� ������ �����ϴµ� �ʿ��� ���ø� ���� ���
				*/
				
				try {
					String plainMail = readFile (contentFilePath, contentFileEncoding);
					
					encMail = aCipherInterface.makeMailContentMultiAttachFileWithTemplate(aKeyParam, aFileName, plainMail, templateFilePath);
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
					aTemplate = readFile (templateFilePath, contentFileEncoding);
					
					encMail = aCipherInterface.makeMailContentMultiAttachFileWithTemplateS(aKeyParam, aFileName, plainMail, aTemplate);
					saveFile("./sample/output/attach_ctx_enc_multi4.html", encMail.getBytes(), contentFileEncoding);
				}catch (IOException e) {
					e.printStackTrace();
				}
				
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
