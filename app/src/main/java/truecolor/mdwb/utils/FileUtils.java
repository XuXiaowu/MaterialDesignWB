package truecolor.mdwb.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.lidroid.xutils.bitmap.core.BitmapDecoder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import truecolor.mdwb.global.Constant;

public class FileUtils {

	private static final String TAG = FileUtils.class.getSimpleName();

	public static void writeFile(InputStream in, File file) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		try {
			FileOutputStream out = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 128];
			int len = -1;
			while ((len = in.read(buffer)) != -1)
				out.write(buffer, 0, len);
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readAssetsFile(String file, Context context) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = context.getResources().getAssets().open(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static String readFileToString(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			InputStream is = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				sb.append(readLine);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String content = sb.toString();
		Log.d(TAG, String.format("read file's content = %s", content.length() >= 150 ? content.substring(0, 150) : content));
		return sb.toString();
	}

	public static byte[] readFileToBytes(File file) {
		try {
			return readStreamToBytes(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readStreamToBytes(InputStream in) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 8];
		int length = -1;
		while ((length = in.read(buffer)) != -1) {
			out.write(buffer, 0, length);
		}
		out.flush();
		byte[] result = out.toByteArray();
		in.close();
		out.close();
		return result;
	}

	public static boolean writeFile(File file, String content) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(content.getBytes());
			out.flush();

			Log.d(TAG, String.format("write file's content = %s", content.length() >= 150 ? content.substring(0, 150) : content));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e2) {
				}
		}

		return true;
	}
	
	public static boolean writeFile(File file, byte[] bytes) {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileOutputStream out = null;
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		try {
			out = new FileOutputStream(file);

			byte[] buffer = new byte[1024 * 8];
			int length = -1;
			
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (Exception e2) {
				}
		}

		return true;
	}

	public static boolean copyFile(File sourceFile, File targetFile) {
		try {
			FileInputStream in = new FileInputStream(sourceFile);
			byte[] buffer = new byte[128 * 1024];
			int len = -1;
			FileOutputStream out = new FileOutputStream(targetFile);
			while ((len = in.read(buffer)) != -1)
				out.write(buffer, 0, len);
			out.flush();
			out.close();
			in.close();
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T readObject(File file, Class<T> clazz) throws Exception {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			
			return (T) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}
		
		return null;
	}
	
	public static void writeObject(File file, Serializable object) throws Exception {
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			
			out.writeObject(object);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

	public static File getUploadFile(File source, Context context) {
		Log.w(TAG,"原图图片大小" + (source.length() / 1024) + "KB");

		if (source.getName().toLowerCase().endsWith(".gif")) {
			Log.w(TAG, "上传图片是GIF图片，上传原图");
			return source;
		}

		File file = null;

		String imagePath = Constant.UPLOAD_IMAGE_TEMP_PATH;

		int sample = 1;
		int maxSize = 0;

		int type ;
		if (SystemUtils.getNetworkType(context) == SystemUtils.NetWorkType.wifi)
			type = 1;
		else
			type = 2;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(source.getAbsolutePath(), opts);
		switch (type) {
			// 原图
			case 1:
				Log.w(TAG, "原图上传");
				file = source;
				break;
			// 高
			case 2:
				sample = BitmapDecoder.calculateInSampleSize(opts, 1920, 1080);
				Log.w(TAG, "高质量上传");
				maxSize = 700 * 1024;
				imagePath = imagePath + "高" + File.separator + source.getName();
				file = new File(imagePath);
				break;
			// 中
			case 3:
				Log.w(TAG, "中质量上传");
				sample = BitmapDecoder.calculateInSampleSize(opts, 1280, 720);
				maxSize = 300 * 1024;
				imagePath = imagePath + "中" + File.separator + source.getName();
				file = new File(imagePath);
				break;
			// 低
			case 4:
				Log.w(TAG,"低质量上传");
				sample = BitmapDecoder.calculateInSampleSize(opts, 1280, 720);
				maxSize = 100 * 1024;
				imagePath = imagePath + "低" + File.separator + source.getName();
				file = new File(imagePath);
				break;
			default:
				break;
		}

		// 压缩图片
		if (type != 1 && !file.exists()) {
			Log.w(TAG,String.format("压缩图片，原图片 path = %s", source.getAbsolutePath()));
			byte[] imageBytes = FileUtils.readFileToBytes(source);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				out.write(imageBytes);
			} catch (Exception e) {
			}

			Log.w(TAG,String.format("原图片大小%sK", String.valueOf(imageBytes.length / 1024)));
			if (imageBytes.length > maxSize) {
				// 尺寸做压缩
				BitmapFactory.Options options = new BitmapFactory.Options();

				if (sample > 1) {
					options.inSampleSize = sample;
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
					Log.w(TAG,String.format("压缩图片至大小：%d*%d", bitmap.getWidth(), bitmap.getHeight()));
					out.reset();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					imageBytes = out.toByteArray();
				}

				options.inSampleSize = 1;
				if (imageBytes.length > maxSize) {
					BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

					int quality = 90;
					out.reset();
					Log.w(TAG,String.format("压缩图片至原来的百分之%d大小", quality));
					bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
					while (out.toByteArray().length > maxSize) {
						out.reset();
						quality -= 10;
						Log.w(TAG,String.format("压缩图片至原来的百分之%d大小", quality));
						bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
					}
				}

			}

			try {
				if (!file.getParentFile().exists())
					file.getParentFile().mkdirs();

				Log.w(TAG,String.format("最终图片大小%sK", String.valueOf(out.toByteArray().length / 1024)));
				FileOutputStream fo = new FileOutputStream(file);
				fo.write(out.toByteArray());
				fo.flush();
				fo.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * 保存Bitmap图片为本地文件
	 * @param path 保存文件路径
	 * @param photo Bitmap类型的图片
	 */
	public static boolean saveImageFile(Bitmap photo, String path) {
		try {
			File targetFile = new File(path);
			targetFile.delete();
			if (!targetFile.exists()){
				File dir = targetFile.getParentFile();
				if (dir.exists() || dir.mkdirs()) {
					targetFile.createNewFile();
				}else {
					dir.createNewFile();
				}
			}
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean createFile(String path){
		try {
			File targetFile = new File(path);
			targetFile.delete();
			if (!targetFile.exists()){
				File dir = targetFile.getParentFile();
				if (dir.exists() || dir.mkdirs()) {
					targetFile.createNewFile();
				}else {
					dir.createNewFile();
					targetFile.createNewFile();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;

	}

}
