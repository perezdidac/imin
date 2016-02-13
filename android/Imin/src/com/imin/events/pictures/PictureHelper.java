package com.imin.events.pictures;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class PictureHelper {

	public static Bitmap prepare(Bitmap bitmap, int maxSize) {
		// Crop the picture
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap preparedBitmap;

		try {
			// First crop the image
			if (height < width) {
				// Horizontal image
				float factor = (float) maxSize / (float) height;
				int newWidth = (int) (factor * (float) width);
				preparedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, maxSize, false);
				int x = (preparedBitmap.getWidth() - preparedBitmap.getHeight()) / 2;
				preparedBitmap = Bitmap.createBitmap(preparedBitmap, x, 0, maxSize, maxSize);
			} else {
				// Vertical image
				float factor = (float) maxSize / (float) width;
				int newHeight = (int) (factor * (float) height);
				preparedBitmap = Bitmap.createScaledBitmap(bitmap, maxSize, newHeight, false);
				int y = (preparedBitmap.getHeight() - preparedBitmap.getWidth()) / 2;
				preparedBitmap = Bitmap.createBitmap(preparedBitmap, 0, y, maxSize, maxSize);
			}
		} catch (OutOfMemoryError error) {
			return null;
		} catch (IllegalArgumentException exception) {
			return null;
		}

		// Return
		return preparedBitmap;
	}

	public static String encode(Bitmap bitmap) {
		if (bitmap == null) {
			return "";
		}

		try {
			// Encode the bitmap
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
			byte[] byteArray = stream.toByteArray();
			String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
			return base64;
		} catch (Exception e) {
			return null;
		}
	}

	public static Bitmap decode(String base64) {
		try {
			byte[] byteArray = Base64.decode(base64, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}
}
