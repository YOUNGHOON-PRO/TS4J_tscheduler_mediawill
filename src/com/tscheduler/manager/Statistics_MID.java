/*
* 클래스명: Statistics_MID.java
* 버전정보: JDK 1.4.1
* 요약설명: MID 별 통계 처리
* 작성일자: 2003-04-04 하광범
 */

package com.tscheduler.manager;

public class Statistics_MID
{
	public String MID;
	public int TID;
	public int Ecode[];
	public int Scount;
	public int Ccount;

	public Statistics_MID()
	{
		MID = "";
		TID = 0;
		Scount = 0;
		Ccount = 0;
		Ecode = new int[24];
		for( int i = 0; i < 24;i++ ) {
			Ecode[i] = 0;
		}
	}
}
