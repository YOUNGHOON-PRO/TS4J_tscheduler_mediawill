package com.tscheduler.util;
/*
* 문서 규칙, 이메일규칙과 같은 유효성검사를 해준다.
*/
import java.util.*;

/**
 * 이메일의 포맷이 맞는지 체크하는 클래스
 * @version 1.0
 * @author ymkim
 */
public class CheckFormat
{
	/**이메일_ID의 최대 길이*/
	private static int ID_MAX_LENGTH = 50;

	/**
	 * 이메일이 이메일 규칙에 맞는지 체크한다.
	 * @version 1.0
	 * @author ymkim
	 * @param email 이메일
	 * @return boolean true - 이메일 규칙에 맞다, false - 이메일 규칙에 틀리다.
	 */
	public static boolean checkEmail(String email)
	{
		//이메일 안에 공백이있다면 에러로 처리한다.
		StringTokenizer testSt = new StringTokenizer(email);
		if( testSt.countTokens() != 1 )	{
			return false;
		}

		StringTokenizer st = new StringTokenizer(email, "@");

		//@가 없거나 혹은 아이디나 도메인이 없는경우나 혹은 @가 하나 이상일 경우
		if( st.countTokens() != 2 ) {
			return false;
		}

		while( st.hasMoreTokens() )
		{
			String id = st.nextToken();//아이디

			//id 길이가 50자를 넘으면 에러
			if( id.length() > ID_MAX_LENGTH ) {
				return false;
			}

			String domain = st.nextToken();//도메인
			StringTokenizer st2 = new StringTokenizer(domain, ".");

			if( st2.countTokens() == 1 ) {
				return false;
			}

			//요즘은 한글 아이디가 있다.
			//			//아이디가 한글인지 검사한다.
			//			byte[] idToByte  = id.getBytes();
			//
			//			//첫번째 바이트가 ascii값을 넘어서는(128이상) 값이면 한글이다.
			//			if((int)(idToByte[0]&0xff) > 127)
			//			{
			//				return false;
			//			}
		}

		return pathConfirm(email);
	}

	public static boolean pathConfirm(String str)
	{
		String id, domain, temp;
		int pos = 0, domainlen = 0;

		str = str.trim();
		if( str.length() < 3 ) return false;
		if( str.indexOf("|") >= 0 ) return false;
		if( str.indexOf(">") >= 0 ) return false;
		if( str.indexOf("<") >= 0 ) return false;

		if( (pos = str.indexOf("@")) == -1 ) {
			return false;
		}

		id = str.substring(0,pos);
		if( id.length()  < 1  ) {
			return false; // id
		}

		if( id.indexOf("|") >= 0 ) return false;
		if( id.indexOf("<") >= 0 ) return false;
		if( id.indexOf(">") >= 0 ) return false;
		if( id.indexOf("#") >= 0 ) return false;
		if( id.indexOf("\\") >= 0 ) return false;
		if( id.indexOf("\"") >= 0 ) return false;
		if( id.indexOf("'") >= 0 ) return false;
		if( id.indexOf("`") >= 0 ) return false;
		if( id.indexOf("!") >= 0 ) return false;
		if( id.indexOf("$") >= 0 ) return false;
		if( id.indexOf("%") >= 0 ) return false;
		if( id.indexOf("^") >= 0 ) return false;
		if( id.indexOf("&") >= 0 ) return false;
		if( id.indexOf("*") >= 0 ) return false;
		if( id.indexOf("~") >= 0 ) return false;
		if( id.indexOf("?") >= 0 ) return false;
		if( id.indexOf("(") >= 0 ) return false;
		if( id.indexOf(")") >= 0 ) return false;
		if( id.indexOf(",") >= 0 ) return false;
		if( id.indexOf(";") >= 0 ) return false;
		if( id.indexOf(":") >= 0 ) return false;
		if( id.indexOf("/") >= 0 ) return false;
		if( id.indexOf("[") >= 0 ) return false;
		if( id.indexOf("]") >= 0 ) return false;
		if( id.indexOf("}") >= 0 ) return false;
		if( id.indexOf("{") >= 0 ) return false;
		if( id.indexOf(" ") >= 0 ) return false;

		domain = str.substring(pos+1, str.length());
		temp = domain.trim();
		domainlen = domain.length();

		if( temp.length() == 0 ) return false;
		if( domainlen == 0 ) return false; // domain

		if( domain.indexOf("@") >= 0 ) return false;
		if( domain.indexOf("|") >= 0 ) return false;
		if( domain.indexOf("<") >= 0 ) return false;
		if( domain.indexOf(">") >= 0 ) return false;
		if( domain.indexOf("#") >= 0 ) return false;
		if( domain.indexOf("\\") >= 0 ) return false;
		if( domain.indexOf("\"") >= 0 ) return false;
		if( domain.indexOf("'") >= 0 ) return false;
		if( domain.indexOf("`") >= 0 ) return false;
		if( domain.indexOf("!") >= 0 ) return false;
		if( domain.indexOf("$") >= 0 ) return false;
		if( domain.indexOf("%") >= 0 ) return false;
		if( domain.indexOf("^") >= 0 ) return false;
		if( domain.indexOf("&") >= 0 ) return false;
		if( domain.indexOf("*") >= 0 ) return false;
		if( domain.indexOf("~") >= 0 ) return false;
		if( domain.indexOf("?") >= 0 ) return false;
		if( domain.indexOf("(") >= 0 ) return false;
		if( domain.indexOf(")") >= 0 ) return false;
		if( domain.indexOf(",") >= 0 ) return false;
		if( domain.indexOf(";") >= 0 ) return false;
		if( domain.indexOf(":") >= 0 ) return false;
		if( domain.indexOf("/") >= 0 ) return false;
		if( domain.indexOf("[") >= 0 ) return false;
		if( domain.indexOf("]") >= 0 ) return false;
		if( domain.indexOf("}") >= 0 ) return false;
		if( domain.indexOf("{") >= 0 ) return false;
		if( domain.indexOf(" ") >= 0 ) return false;
		if( domain.indexOf("..") >= 0 ) return false;

		if( domain.charAt(domainlen-1) == '.' ||
			domain.charAt(domainlen-1) == '-' ||
			domain.charAt(domainlen-1) == '_' ||
			domain.charAt(0) == '.' ||
			domain.charAt(0) == '-' ||
			domain.charAt(0) == '_' )
			return false;

		if( domain.indexOf('.') < 0 )
			return false;

		return true;
	}
}