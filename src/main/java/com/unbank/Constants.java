package com.unbank;

import org.springframework.stereotype.Component;

@Component
public class Constants {

	public static Integer SERVERPORT;
	public static String SERVERIP;
	public static boolean USE_SSL;

	public static int HEARTBEATRATE;
	public static String HEARTBEATREQUEST = "HEARTBEATREQUEST";
	public static String HEARTBEATRESPONSE = "HEARTBEATRESPONSE";
	public static int HEART_TIMEOUT;
	public static int CLENT_TIMEOUT;

	public static Boolean ISTANCHUANG;

	public void init() {

	}

	public static Integer getSERVERPORT() {
		return SERVERPORT;
	}

	public static void setSERVERPORT(Integer sERVERPORT) {
		SERVERPORT = sERVERPORT;
	}

	public static String getSERVERIP() {
		return SERVERIP;
	}

	public static void setSERVERIP(String sERVERIP) {
		SERVERIP = sERVERIP;
	}

	public static boolean isUSE_SSL() {
		return USE_SSL;
	}

	public static void setUSE_SSL(boolean uSE_SSL) {
		USE_SSL = uSE_SSL;
	}

	public static int getHEARTBEATRATE() {
		return HEARTBEATRATE;
	}

	public static void setHEARTBEATRATE(int hEARTBEATRATE) {
		HEARTBEATRATE = hEARTBEATRATE;
	}

	public static String getHEARTBEATREQUEST() {
		return HEARTBEATREQUEST;
	}

	public static void setHEARTBEATREQUEST(String hEARTBEATREQUEST) {
		HEARTBEATREQUEST = hEARTBEATREQUEST;
	}

	public static String getHEARTBEATRESPONSE() {
		return HEARTBEATRESPONSE;
	}

	public static void setHEARTBEATRESPONSE(String hEARTBEATRESPONSE) {
		HEARTBEATRESPONSE = hEARTBEATRESPONSE;
	}

	public static int getHEART_TIMEOUT() {
		return HEART_TIMEOUT;
	}

	public static void setHEART_TIMEOUT(int hEART_TIMEOUT) {
		HEART_TIMEOUT = hEART_TIMEOUT;
	}

	public static int getCLENT_TIMEOUT() {
		return CLENT_TIMEOUT;
	}

	public static void setCLENT_TIMEOUT(int cLENT_TIMEOUT) {
		CLENT_TIMEOUT = cLENT_TIMEOUT;
	}

	public static Boolean getISTANCHUANG() {
		return ISTANCHUANG;
	}

	public static void setISTANCHUANG(Boolean iSTANCHUANG) {
		ISTANCHUANG = iSTANCHUANG;
	}

}
