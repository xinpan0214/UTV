/**
 * 
 * Copyright © 2014GreatVision. All rights reserved.
 *
 * @Title: TmArray.java
 * @Prject: BananaTvSetting
 * @Description: TODO
 * @author: luojw 
 * @date: 2014-1-21 上午11:56:49
 */
package com.sz.ead.framework.mul2launcher.settings.network;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TmArray implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return new X509Certificate[] {}; 
	}

}
