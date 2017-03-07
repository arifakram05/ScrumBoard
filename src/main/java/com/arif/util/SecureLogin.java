package com.arif.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * used to genearate a JWT token for a user<br/>
 * used to validate a user's JWT token
 * 
 * @author arifakrammohammed
 *
 */
public class SecureLogin {

	final static Logger LOGGER = Logger.getLogger(SecureLogin.class);
	private static SecretKey secretKey = null;
	private static String encodedSecretKey = null;

	/**
	 * Construct a JWT token
	 * 
	 * @param associateId
	 *            associate to whom token needs to be served
	 * @param loginDuration
	 *            how long can the user stay logged in
	 * @return JWT token
	 * @throws NoSuchAlgorithmException
	 *             when SecretKey is null or while error during processing
	 */
	public static String createJWT(String associateId, long loginDuration) throws NoSuchAlgorithmException {

		if (secretKey == null) {
			// generate secret key using specified algorithm
			secretKey = KeyGenerator.getInstance("AES").generateKey();
			// get base64 encoded version of the key
			encodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		}

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our encoded secret key
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(encodedSecretKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(associateId).setIssuedAt(now).signWith(signatureAlgorithm,
				signingKey);

		// Set expiration time
		if (loginDuration >= 0) {
			long expMillis = nowMillis + loginDuration;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	/**
	 * Read the JWT token
	 * 
	 * @param jwt
	 *            JWT token
	 */
	public static void parseJWT(String token) {

		// This line will throw an exception if it is not a signed JWS
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(encodedSecretKey))
				.parseClaimsJws(token).getBody();
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
	}

	/**
	 * Verify if the given token and associate ID are related and valid
	 * 
	 * @param token
	 *            JWT token provided by the user to validate
	 * @param associateIdToVerify
	 *            associate trying to perform operation
	 * @return <i>true</i> if given associate ID is valid
	 */
	public static boolean isTokenValid(String token, String associateIdToVerify) {
		String knownAssociateId = Jwts.parser().setSigningKey(encodedSecretKey).parseClaimsJws(token).getBody().getId();
		LOGGER.info("Verifying token for associate " + associateIdToVerify);
		return associateIdToVerify.equals(knownAssociateId);
	}

}
