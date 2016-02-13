package com.imin.events.pictures;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Context;

public class FileCache {

	private Context context;

	public FileCache(Context context) {
		this.context = context;
	}

	public boolean add(String input, byte[] data) {
		// Make the hash
		String hash = computeHash(data);
		
		// Get the file handle
		File file = file(hash);

		// Check if file exists
		if (exists(file)) {
			return true;
		}

		// Check data
		if (data.length > 0) {
			// Put the data
			return save(file, data);
		} else {
			return false;
		}
	}

	public byte[] get(String hash) {
		byte[] data = null;

		// Get the file handle
		File file = file(hash);

		// Check if file exists
		if (exists(file)) {
			// Get the data
			data = load(file);
		}

		return data;
	}
	
	public boolean remove(String hash) {
		// Get the file handle
		File file = file(hash);

		// Check if file exists
		if (exists(file)) {
			// Get the data
			return file.delete();
		} else {
			return false;
		}
	}

	@SuppressLint("DefaultLocale")
	private String computeHash(byte[] byteArray) {
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

	private boolean exists(File file) {
		// Check if the file exists
		return file.exists();
	}

	private boolean save(File file, byte[] data) {
		// Save the data into the file
		try {
			file.createNewFile();
		} catch (IOException e) {
			return false;
		}

		try {
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(data, 0, data.length);
			stream.flush();
			stream.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	private byte[] load(File file) {
		byte[] data = null;

		// Load the file from the disk
		FileInputStream fin;

		try {
			fin = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fin);
			DataInputStream dis = new DataInputStream(bis);
			data = toByteArray(dis);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return data;
	}

	private File file(String hash) {
		// Create the file to be tested
		String filename = hash;
		File directory = context.getCacheDir();

		// Check if the file exists
		return new File(directory, filename);
	}

	// Support methods

	private static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	private static long copy(InputStream from, OutputStream to) throws IOException {
		byte[] buf = new byte[4096];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}
}