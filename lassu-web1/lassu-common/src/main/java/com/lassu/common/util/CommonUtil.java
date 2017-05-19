/**
 * 
 */
package com.lassu.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.sql.Date;
import java.sql.Timestamp;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author abhinab
 *
 */
public class CommonUtil {

	private static final byte[] ENCRYPTION_KEY = "&^^%44$$&*&Gy&%&".getBytes();
	private static final String JWT_API_KEY = "as36e7e872re32ry283ry283";
	private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	private static String HOSTNAME = null;

	public static Claims getClaims(String token) {
		if (token != null) {
			Claims claims = null;
			try {
				claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(JWT_API_KEY))
						.parseClaimsJws(token).getBody();
			} catch(Exception e) {}
			return claims;

		}
		return null;
	}
	
	public static String setClaims(String id, String issuer, String subject, long ttlMillis) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(JWT_API_KEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
				.signWith(signatureAlgorithm, signingKey);
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}
		return builder.compact();
	}

	public static String encrypt(String subject)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		BlowfishEngine engine = new BlowfishEngine();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);
		KeyParameter key = new KeyParameter(ENCRYPTION_KEY);
		cipher.init(true, key);
		byte in[] = subject.getBytes();
		byte out[] = new byte[cipher.getOutputSize(in.length)];
		int len = cipher.processBytes(in, 0, in.length, out, 0);
		cipher.doFinal(out, len);
		return new String(Base64.encode(out));
	}

	public static String decrypt(String subject) throws Exception {
		BlowfishEngine engine = new BlowfishEngine();
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);
		StringBuffer result = new StringBuffer();
		KeyParameter key = new KeyParameter(ENCRYPTION_KEY);
		cipher.init(false, key);
		byte decodedSubject[] = Base64.decode(subject);
		byte generatedSubject[] = new byte[cipher.getOutputSize(decodedSubject.length)];
		int len = cipher.processBytes(decodedSubject, 0, decodedSubject.length, generatedSubject, 0);
		cipher.doFinal(generatedSubject, len);
		String s2 = new String(generatedSubject);
		for (int i = 0; i < s2.length(); i++) {
			char c = s2.charAt(i);
			if (c != 0) {
				result.append(c);
			}
		}

		return result.toString();
	}

	public static String getHostname() {
		if (HOSTNAME != null) {
			return HOSTNAME;
		} else {
			try {
				HOSTNAME = InetAddress.getLocalHost().getHostName();
				return HOSTNAME;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String currentTimeStamp() {
		return (new Timestamp(new java.util.Date().getTime())).toString();
	}
	
}
