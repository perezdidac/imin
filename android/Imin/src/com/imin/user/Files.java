package com.imin.user;

import android.annotation.SuppressLint;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Files {

	public static String readStringFile(FileInputStream fis) throws java.io.IOException {
		StringBuffer fileContent = new StringBuffer("");

		byte[] buffer = new byte[1024];

		while (fis.read(buffer) != -1) {
			fileContent.append(new String(buffer));
		}

		return fileContent.toString();
	}

	public static void writeStringFile(FileOutputStream fos, String text) throws java.io.IOException {
		fos.write(text.getBytes());
	}

	@SuppressLint("DefaultLocale")
	public static String hashFile(byte[] byteArray) {
		String hash = "";

		// Compute hash
		try {
			// Compute md5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] md5 = digest.digest(byteArray);

			// Convert to String
			for (int i = 0; i < md5.length; i++) {
				hash += Integer.toString((md5[i] & 0xff) + 0x100, 16).substring(1);
			}
		} catch (NoSuchAlgorithmException e) {
		}

		return hash.toLowerCase();
	}
}
