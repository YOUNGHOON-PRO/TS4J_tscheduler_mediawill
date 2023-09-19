package com.tscheduler.util;
/*
* 설명: 수신자의 정보가 들어간 ReceiveUserInfo의 리스트
*
*/
import java.util.*;

/**
 * 예약정보나 수신자정보와 같은 정보의 리스트를 넣는 단위 클래스
 * @version 1.0
 * @author ymkim
 */
public class DataUnitInfoList extends ArrayList
{
	/**DataUnitInfo배열*/
	DataUnitInfo[] uInfo = null;

	/**DataUnitInfoList에서의 순서*/
	private int seqIndex = 0;

	/**
	 * 생성자
	 * @version 1.0
	 * @author ymkim
	 */
	public DataUnitInfoList()
	{
		super();
	}


	/**
	 * 인덱스에 해당하는 값을 가져온다.
	 * @version 1.0
	 * @author ymkim
	 * @param index 위치값
	 * @return 해당 위치의 DataUnitInfo객체를 리턴
	 */
	public DataUnitInfo getDataUnitInfo(int index)
	{
		return (DataUnitInfo)this.get(index);
	}


	/**
	 * 하나의 DataUnitInfo를 추가한다.
	 * @version 1.0
	 * @author ymkim
	 * @param uInfo 추가할 DataUnitInfo객체
	 */
	public void addDataUnitInfo(DataUnitInfo uInfo)
	{
		this.add(uInfo);
	}


	/**
	 * 하나의 DataUnitInfo를 가져온후에 그 것을 지워준다.
	 * @version 1.0
	 * @author ymkim
	 * @return DataUnitInfo Pop Up시킬 DataUnitInfo 객체
	 */
	public DataUnitInfo popDataUnitInfo()
	{
		return (DataUnitInfo)this.remove(0);
	}


	/**
	 * 순서대로 하나씩 가져오기
	 * @version 1.0
	 * @author ymkim
	 * @return DataUnitInfo 순서대로 가져온 DataUnitInfo 객체
	 */
	public DataUnitInfo getSequenceDataUnitInfo()
	{
		int seq = seqIndex++;
		if( seq < this.getDataUnitInfoListSize() ) {
			return this.getDataUnitInfo(seq);
		}
		else
		{
			seqIndex = 0;
			return null;
		}
	}

	/**
	 * 갯수를 리턴한다.
	 * @version 1.0
	 * @author ymkim
	 * @return int DataUnitInfoList의 갯수
	 */
	public int getDataUnitInfoListSize(){
		return this.size();
	}

	/**
	 * DataUnitInfo의 하나의 Key값에 해당하는 DataUnitInfoList에서 String배열을 가져온다.
	 * @version 1.0
	 * @author ymkim
	 * @param key 특정 위치의 key값
	 * @return String[] 특정 key 값에 대한 문자 배열값
	 */
	public String[] getStringArray(String key)
	{
		int arraySize = this.getDataUnitInfoListSize();
		String[] strArray = new String[arraySize];
		uInfo = new DataUnitInfo[arraySize];

		for( int i = 0; i < arraySize ; i++ )
		{
			uInfo[i]= (DataUnitInfo)(this.get(i));
			strArray[i]= uInfo[i].getString(key);
		}

		return strArray;
	}

	/**
	 * DataUnitInfo의 하나의 Key값에 해당하는 DataUnitInfoList에서 int배열을 가져온다.
	 * @version 1.0
	 * @author ymkim
	 * @param key 특정 위치의 key값
	 * @return int[] 특정 key 값에 대한 Integer 배열값
	 */
	public int[] getIntArray(String key)
	{
		int arrSize = this.getDataUnitInfoListSize();
		int[] intArray = new int[arrSize];

		for( int i = 0; i < arrSize; i++ )
		{
			uInfo[i] = (DataUnitInfo)this.get(i);
			intArray[i] = uInfo[i].getInt(key);
		}
		return intArray;
	}

	/**
	 * 특정 필드에 특정위치에 문자열을 넣기 위한 메소드
	 * @version 1.0
	 * @author ymkim
	 * @param index 특정 위치
	 * @param key 특정 위치의 key값
	 * @param value 특정 위치의 value값
	 */
	public void addStringIndex(int index, String key,String value)
	{
		DataUnitInfo tempUnitInfo = this.getDataUnitInfo(index);
		tempUnitInfo.setString(key,value);
		this.add(index,tempUnitInfo);
	}

	/**
	 * 특정 필드에 특정위치에 숫자를 넣기 위한 메소드
	 * @version 1.0
	 * @author ymkim
	 * @param index 특정 위치
	 * @param key 특정 위치의 key값
	 * @param value 특정 위치의 value값
	 */
	public void addIntIndex(int index, String key,int value)
	{
		DataUnitInfo tempUnitInfo = this.getDataUnitInfo(index);
		tempUnitInfo.setInt(key,value);
		this.add(index,tempUnitInfo);
	}

	/**
	 * 특정 필드의 위치에 있는 문자열을 얻기 위한 메소드
	 * @version 1.0
	 * @author ymkim
	 * @param index 특정 위치
	 * @param key 특정 위치의 key값
	 * @return String
	 */
	public String getStringIndex(int index, String key)
	{
		DataUnitInfo tempDataUnit = (DataUnitInfo)(this.get(index));
		return tempDataUnit.getString(key);
	}

	//특정 필드의 위치에 있는 숫자를 얻기 위한 메소드
	public int getIntIndex(int index, String key)
	{
		DataUnitInfo tempDataUnit = (DataUnitInfo)(this.get(index));
		return tempDataUnit.getInt(key);
	}
}