package com.tscheduler.util;
/*
*설명: 유저 개인의 정보 단위 workdb의 발송그룹저장테이블이나 혹은
*		legacyDB에서 값을 뽑아온 한 유저의 그룹단위
*  1) 들어가는 값의 필드는 MID(메시지ID), TID(발송타입),RID(수신자ID), RNAME(수신자 이름), RMAIL(수신자 이메일)
*/
import java.util.*;

/**
 * 예약정보나 수신자정보와 같은 정보를 넣는 단위 클래스
 * @version 1.0
 * @author ymkim
 */
public class DataUnitInfo extends Hashtable
{
	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	public DataUnitInfo()
	{
		super();
	}

	/**
	 * 문자열을 입력한다.
	 * @version 1.0
	 * @author ymkim
	 * @param key key값
	 * @param value value 값
	 */
	public void setString(String key, String value)
	{
		if( value == null ) {
			this.put(key, "");
		}
		else {
			this.put(key, value);
		}
	}

	/**
	 * Integer를 입력한다.
	 * @version 1.0
	 * @author ymkim
	 * @param k key값
	 * @param v value 값
	 */
	public void setInt(String k, int v)
	{
		this.put(k, new Integer(v));
	}

	/**
	 * 문자열을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @param key key값
	 * @return String key에 대해 얻는 문자열값
	 */
	public String getString(String key)
	{
		if( this.isExist(key) ) {
			return (String) this.get(key);
		}
		else {
			return "";
		}
	}

	/**
	 * Integer을 얻는다.
	 * @version 1.0
	 * @author ymkim
	 * @param key key값
	 * @return int key에 대해 얻는 Integer값
	 */
	public int getInt(String key)
	{
		if( this.isExist(key) ) {
			return ((Integer)this.get(key)).intValue();
		}
		else {
			return -1;
		}
	}

	/**
	 * key 값에 대한 value값이 있는지 체크
	 * @version 1.0
	 * @author ymkim
	 * @param key key값
	 * @return boolean true - value가 존재, false - value가 없음
	 */
	public boolean isExist(String key)
	{
		return this.containsKey(key);
	}

	/**
	 * 이 단위 객체 안에 들어있는 내용들의 갯수
	 * @version 1.0
	 * @author ymkim
	 * @return int 내용들의 갯수를 얻는다.
	 */
	public int getSize()
	{
		return this.size();
	}
}