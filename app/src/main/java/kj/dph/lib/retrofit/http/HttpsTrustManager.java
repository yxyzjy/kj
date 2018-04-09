package kj.dph.lib.retrofit.http;


import android.annotation.SuppressLint;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by dest1 on 2015/9/8.
 */
public class HttpsTrustManager implements X509TrustManager {

	private static TrustManager[] trustManagers;
	private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
			throws java.security.cert.CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
			throws java.security.cert.CertificateException {

	}

	public boolean isClientTrusted(X509Certificate[] chain) {
		return true;
	}

	public boolean isServerTrusted(X509Certificate[] chain) {
		return true;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return _AcceptedIssuers;
	}

	/* HttpsURLConnection */
	public static void allowAllSSL() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

		});

		SSLContext context = null;
		if (trustManagers == null) {
			trustManagers = new TrustManager[] { new HttpsTrustManager() };
		}

		try {
			context = SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

	}
	/**
	 * 默认信任所有的证书
	 * TODO 最好加上证书认证，主流App都有自己的证书
	 *
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	public static SSLSocketFactory createSSLSocketFactory() {

		SSLSocketFactory sSLSocketFactory = null;

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new HttpsTrustManager()},
					new SecureRandom());
			sSLSocketFactory = sc.getSocketFactory();
		} catch (Exception e) {
		}

		return sSLSocketFactory;
	}
}
