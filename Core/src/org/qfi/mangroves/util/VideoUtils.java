package org.qfi.mangroves.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import org.qfi.mangroves.ImageManager;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Matt McHugh
 * Date: 10/5/12
 */
public class VideoUtils {
	private static final String CLASS_TAG = VideoUtils.class.getCanonicalName();

	// folder to save pending photos.
	public static final String PENDING = "/pendingvideos";

	public static File[] getPendingVideos(Context context) {
		File path = pendingVideosPath(context);

		if (path != null && path.exists()) {
			return path.listFiles();
		}
		return null;
	}

	public static File pendingVideosPath(Context context) {
		return new File(Environment.getExternalStorageDirectory(), context.getPackageName() + PENDING);
	}

	public static Uri getVideoUri(String filename, Activity activity) {
		File path = new File(Environment.getExternalStorageDirectory(), activity.getPackageName() + PENDING);
		if (!path.exists() && path.mkdir()) {
			return Uri.fromFile(new File(path, filename));
		}
		return Uri.fromFile(new File(path, filename));
	}

	public static String getVideoPath(Activity activity) {
		File path = new File(Environment.getExternalStorageDirectory(), activity.getPackageName() + PENDING);
		if (!path.exists()) {
			path.mkdir();
		}

		return path.exists() ? path.getAbsolutePath() : null;
	}

	public static File getGalleryVideo(Activity activity, Uri uri) {
		if (uri != null) {
			String[] columns = { MediaStore.Video.Media.DATA };
			Cursor cursor = MediaStore.Video.query(activity.getContentResolver(), uri, null);
			if (cursor != null) {
				cursor.moveToFirst();
				String filePath = cursor.getString(cursor.getColumnIndex(columns[0]));

				File videoFile = new File(filePath);
				if (!videoFile.isFile()) {
					new Util().log("Video file does not exist: %s", filePath);
					return null;
				}

				return videoFile;
			}
		}
		return null;
	}

	public static File getCameraVideo(Uri uri) {
		if (uri != null) {
			String filePath = uri.getPath();

			File videoFile = new File(filePath);
			if (!videoFile.isFile()) {
				new Util().log("Video file does not exist: %s", filePath);
				return null;
			}

			return videoFile;
		}
		return null;
	}

	public static Bitmap getVideoThumbnail(Activity activity, Uri uri) {
		if (uri != null) {
			String[] columns = { BaseColumns._ID };
			Cursor cursor = activity.getContentResolver().query(uri, columns, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex(columns[0]));
				return MediaStore.Video.Thumbnails.getThumbnail(activity.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);
			}
		}
		return null;
	}

	public static boolean saveVideo(Activity activity, File video, String fileName) {
		try {
			byte[] byteArray = new byte[(int)video.length()];
			FileInputStream fileStream = new FileInputStream(video);
			fileStream.read(byteArray);

			ImageManager.writeImage(byteArray, fileName, getVideoPath(activity));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
