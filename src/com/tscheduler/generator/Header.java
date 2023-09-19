package com.tscheduler.generator;
/*
*	설명:헤더에 들어갈 필드의 내용들
*/

/**
 * 헤더 인터페이스
 * @version 1.0
 * @author ymkim
 */
interface Header
{
	/**Carriage Return*/
	public static final String NEW_LINE = "\r\n";
	/**Tab*/
	public static final char CONTINUE_CHAR = '\t';
	/**colon*/
	public static final String COLON = ": ";
	/**semi colon*/
	public static final String SEMICOLON = ";";

	/**본문이 text*/
	public static final int PLAIN_TYPE = 0;
	/**본문이 html*/
	public static final int HTML_TYPE = 1;

	/**컨덴츠 타입이 text/plain*/
	public static final String CONTENT_TYPE_PLAIN = "text/plain";
	/**컨덴츠 타입이 text/html*/
	public static final String CONTENT_TYPE_HTML = "text/html";
	/**컨덴츠 타입이 multipart/mixed*/
	public static final String CONTENT_TYPE_MIXED = "multipart/mixed";

	/**Date 필드*/
	public final String DATE = "Date";
	/**Subejct 필드*/
	public final String SUBJECT = "Subject";
	/**From 필드*/
	public final String FROM = "From";
	/**To 필드*/
	public final String TO = "To";
	/**CC 필드*/
	public final String CC = "Cc";
	/**Bcc 필드*/
	public final String BCC = "Bcc";
	/**Status 필드*/
	public final String STATUS = "Status";
	/**Reply_to 필드*/
	public final String REPLY_TO = "Reply-To";

	/**X-Mailer 필드*/
	public final String X_MAILER = "X-Mailer";
	/**Mime Version 필드*/
	public final String MIME_VERSION =  "MIME-VERSION";
	/**Default Mime Version*/
	public final String DEFAULT_MIME_VERSION = "1.0";

	/**컨덴츠 타입 필드*/
	public final String CONTENT_TYPE = "Content-Type";
	/**Boundary 필드*/
	public final String BOUNDARY = "boundary";
	/**Charset 필드*/
	public final String CHRACTER_SET = "charset";
	/**이름 필드*/
	public final String NAME = "name";
	/**파일명*/
	public final String FILENAME = "filename";
	/**content-transfer-encoding 필드*/
	public final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	/**content-id 필드*/
	public final String CONTENT_ID = "Content-ID";
	/**content-disposition 필드*/
	public final String CONTENT_DISPOSITION = "Content-Disposition";
	/**attachment 필드*/
	public final String ATTACHMENT = "attachment";
}
