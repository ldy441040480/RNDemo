package com.ldy.werty.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.ldy.werty.BaseApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by lidongyang on 2016/10/19.
 */
public class FileUtil {

	private static final String TAG = FileUtil.class.getSimpleName();
	/**assets 文件下babytree.properties */
	public static final String ASSETS_PROPERTY = "babytree.properties";

	/**
	 * 获取手机内存 缓存目录，末尾带\
	 *
	 * @return
	 */
	public static String getCachePath() {
		return BaseApplication.context.getCacheDir().getPath() + File.separator;
	}

	/**
	 * 获取SD卡的根目录，末尾带\
	 *
	 * @return
	 */
	public static String getSDRoot() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}

	/**
	 * 获取 babytree SD缓存目录，末尾带\
	 *
	 * @return
	 */
	public static String getSDBabytree() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "babytree" + File.separator;
	}

	/**
	 * 判断手机是否存在sd卡,并且有读写权限
	 *
	 * @return
	 */
	public static boolean isExistSdcard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建文件夹
	 *
	 * @param dirPath
	 * @return
	 */
	public static boolean makeFolders(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			return false;
		}
		File f = new File(dirPath);
		return (f.exists() && f.isDirectory()) || f.mkdirs();
	}

	public static boolean makeFolders(File file) {
		if (file == null) {
			return false;
		}
		return (file.exists() && file.isDirectory()) || file.mkdirs();
	}

	/**
	 * 检查文件夹是否存在
	 *
	 * @param dirPath
	 * @return
	 */
	public static boolean isFolderExist(String dirPath) {
		if (TextUtils.isEmpty(dirPath)) {
			return false;
		}
		File f = new File(dirPath);
		return f.exists() && f.isDirectory();
	}

	public static boolean isFolderExist(File file) {
		return file != null && file.exists() && file.isDirectory();
	}

	/**
	 * 检查文件是否存在
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		File f = new File(filePath);
		return f.exists() && f.isFile();
	}

	public static boolean isFileExist(File file) {
		return file != null && file.exists() && file.isFile();
	}

	/**
	 * 创建文件
	 *
	 * @param filePath
	 * @return
	 */
	public static boolean createFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		return createFile(new File(filePath));
	}

	public static boolean createFile(File file) {
		if (file == null) {
			return false;
		}
		if (file.exists() && file.isFile()) {
			return true;
		}
		try {
			return file.createNewFile();
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 获取文件大小
	 *
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		File file = new File(filePath);
		return file.exists() && file.isFile() ? file.length() : 0;
	}

	/**
	 * 获取目录文件大小
	 *
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		long dirSize = 0;
		if (isFolderExist(dir)) {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					dirSize += file.length();
				} else if (file.isDirectory()) {
					dirSize += getDirSize(file);
				}
			}
		}
		return dirSize;
	}

	/**
	 * 重命名
	 *
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public static boolean reNamePath(String oldName, String newName) {
		File f = new File(oldName);
		return f.renameTo(new File(newName));
	}

	/**
	 * 根据文件绝对路径获取文件名
	 *
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		}
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 获取文件扩展名
	 *
	 * @param filePath
	 * @return
	 */
	public static String getFileFormat(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		}
		return filePath.substring(filePath.lastIndexOf('.') + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 *
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return "";
		}
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf('.'));
	}

	/**
	 * 计算SD卡的剩余空间
	 * @return            KB
	 */
	public static long getSDSpace() {
		if (isExistSdcard()) {
			try {
				StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
				return stat.getAvailableBlocks() * stat.getBlockSize() / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * 读取Assets文件
	 *
	 * @param fileName
	 * @return 	InputStream
	 */
	public static InputStream openAssets(String fileName) {
		try {
			return BaseApplication.context.getAssets().open(fileName);
		} catch (Exception e) {
			return null;
		}
	}

	public static void writeBytes(byte[] bytes, String dirPath, String fileName) throws Exception {
		if (bytes == null || TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName))
			return;
		makeFolders(dirPath);
		FileOutputStream outStream = new FileOutputStream(dirPath + fileName, false);
		outStream.write(bytes);
		outStream.flush();
		outStream.close();
	}

	/**
	 * 从assets目录中复制整个文件夹内容
	 *
	 * @param oldPath
	 * @param newPath
     */
	public static void copyAssetsFiles(String oldPath, String newPath) {
		try {
			String fileNames[] = BaseApplication.context.getAssets().list(oldPath);
			if (fileNames.length > 0) {
				makeFolders(newPath);
				newPath = newPath.endsWith(File.separator) ? newPath : newPath + File.separator;
				oldPath = oldPath.endsWith(File.separator) ? oldPath : oldPath + File.separator;
				for (String fileName : fileNames) {
					copyAssetsFiles(oldPath + fileName, newPath + fileName);
				}
			} else {
				InputStream is = BaseApplication.context.getAssets().open(oldPath);
				FileOutputStream fos = new FileOutputStream(new File(newPath));
				byte[] buffer = new byte[1024];
				int byteCount = 0;
				while((byteCount=is.read(buffer))!=-1) {
					fos.write(buffer, 0, byteCount);
				}
				fos.flush();
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 写入流
	 *
	 * @param inputStream
	 * @param dirPath
	 * @param fileName
	 * @throws Exception
	 */
	public static void writeInputStream(InputStream inputStream, String dirPath, String fileName) throws Exception {
		if (inputStream == null || TextUtils.isEmpty(dirPath) || TextUtils.isEmpty(fileName))
			return;
		makeFolders(dirPath);
		FileOutputStream outStream = new FileOutputStream(dirPath + fileName, false);
		byte buffer[] = new byte[1024 * 1024];
		int count;
		while ((count = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, count);
		}
		inputStream.close();
		outStream.flush();
		outStream.close();
	}

	/**
	 * 解压Assets中的文件
	 *
	 * @param assetName 压缩包文件名
	 * @param outputDirectory 输出目录
	 * @throws IOException
	 */
	public static void unZipAssets(String assetName, String outputDirectory) throws Throwable {
		if (TextUtils.isEmpty(assetName) || TextUtils.isEmpty(outputDirectory))
			return;
		InputStream inputStream = openAssets(assetName);
		if (inputStream != null) {
			unZip(new ZipInputStream(inputStream), outputDirectory);
		}
	}

	/**
	 * 解压zip
	 *
	 * @param zipFilePath
	 * @param outputDir
	 * @throws IOException
     */
	public static void unZip(String zipFilePath, String outputDir) throws Throwable {
		if (TextUtils.isEmpty(zipFilePath) || TextUtils.isEmpty(outputDir))
			return;
		unZip(new FileInputStream(zipFilePath), outputDir);
	}

	public static void unZip(InputStream inputStream, String outputDir) throws Throwable {
		if (inputStream == null || TextUtils.isEmpty(outputDir))
			return;

		unZip(new ZipInputStream(new BufferedInputStream(inputStream)), outputDir);
	}

	public static void unZip(ZipInputStream zipInputStream, String outputDir) throws Throwable {
		if (zipInputStream == null || TextUtils.isEmpty(outputDir))
			return;
		makeFolders(outputDir);
		ZipEntry zipEntry = null;
		BufferedOutputStream bufferedOutputStream = null;
		byte[] buffer = new byte[1024 * 4];
		int count = 0;
		while ((zipEntry = zipInputStream.getNextEntry()) != null) {
			File file = new File(outputDir + File.separator + zipEntry.getName());
			makeFolders(file.getParent());
			if (zipEntry.isDirectory()) {
				makeFolders(file);
			} else {
				createFile(file);
				FileOutputStream outputStream = new FileOutputStream(file, false);
				bufferedOutputStream = new BufferedOutputStream(outputStream, 4096);
				while ((count = zipInputStream.read(buffer)) != -1) {
					bufferedOutputStream.write(buffer, 0, count);
				}
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}
		}
		zipInputStream.close();
	}

	/**
	 * 保存Properties文件
	 *
	 * @param properties
	 * @param dirPath
	 * @param fileName
	 */
	public static boolean saveProperties(Properties properties, String dirPath, String fileName) {
		try {
			makeFolders(dirPath);
			FileOutputStream outStream = new FileOutputStream(dirPath + fileName, false);
			properties.store(outStream, null);
			outStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 写入 list
	 *
	 * @param dirPath   文件目录
	 * @param name		文件名
	 * @param list
	 * @param append
	 * @param <T>
	 * @return
	 */
	public static <T> boolean writeList(String dirPath, String name, List<T> list, boolean append) {
		try {
			return writeFileOOS(dirPath, name,  list, append);
		} catch (Exception e) {
			return false;
		}
	}

	public static <T> boolean writeList(String filePath, List<T> list, boolean append) {
		try {
			return writeFileOOS(filePath,  list, append);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 读取list
	 *
	 * @param filePath
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> readerList(String filePath) {
		try {
			return (List<T>) readFileOIS(filePath);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> readerList(File file) {
		try {
			return (List<T>) readFileOIS(file);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 写 object 类型数据到文件缓存
	 *
	 * @param dirPath       文件所在目录（不存在则创建）
	 * @param name      	文件名
	 * @param o 			vector map以及实现序列化的对象
	 * @param append
	 */
	public static boolean writeFileOOS(String dirPath, String name, Object o, boolean append) {
		try {
			makeFolders(dirPath);
			return writeFileOOS(dirPath + name, o, append);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 写 object 类型数据到文件缓存
	 *
	 * @param filePath       文件路径（需确保目录存在）
	 * @param o 			vector map以及实现序列化的对象
	 * @param append
	 */
	public static boolean writeFileOOS(String filePath, Object o, boolean append) {
		try {
			return writeFileOOS(new File(filePath), o, append);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean writeFileOOS(File file, Object o, boolean append) {
		ObjectOutputStream oos = null;
		FileOutputStream fos = null;
		if (file != null && createFile(file) && o != null) {
			try {
				fos = new FileOutputStream(file, append);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(o);
				oos.flush();
			} catch (IOException e) {
				return false;
			} finally {
				try {
					if (oos != null)
						oos.close();
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * 读取文件缓存
	 *
	 * @param filePath  文件路径
	 * @return    Object vector map以及实现序列化的对象
	 */
	public static Object readFileOIS(String filePath) {
		try {
			return readFileOIS(new File(filePath));
		} catch (Exception e) {
			return null;
		}
	}

	public static Object readFileOIS(File file) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		Object obj = null;
		if (file != null && file.exists()) {
			try {
				fis = new FileInputStream(file);
				ois = new ObjectInputStream(fis);
				obj = ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (ois != null)
						ois.close();
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * 保存图片到本地
	 *
	 * @param dirPath
	 * @param name
	 * @param bitmap
	 * @return            文件如果已经存在 返回true
	 */
	public static boolean saveImage(String dirPath, String name, Bitmap bitmap) {
		try {
			makeFolders(dirPath);
			return saveImage(dirPath + name, bitmap, 100);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保存图片到本地
	 *
	 * @param filePath
	 * @param bitmap
	 * @param quality
	 * @return            文件如果已经存在 返回true
	 */
	public static boolean saveImage(String filePath, Bitmap bitmap, int quality) {
		if (bitmap == null)
			return false;
		if (isFileExist(filePath))
			return true;
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if(!bitmap.isRecycled()) {
					bitmap.recycle();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static boolean saveImage(String filePath, byte[] data, boolean override) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		if (data == null)
			return false;
		if (!override && isFileExist(filePath))
			return true;
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(data);
			fos.flush();
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 刷新系统相册
	 *
	 * @param context
	 * @param fileName
	 */
	public static void refreshMedia(Context context, File fileName) {
		try {
			if (isFileExist(fileName)) {
				Uri uri = Uri.fromFile(fileName);
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void refreshMedia(Context context, String filePath) {
		try {
			refreshMedia(context, new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除指定目录、文件   以及里面的文件
	 *
	 * @param dirPath
	 * @return
	 */
	public static void deleteFile(String dirPath) {
		deleteFile(dirPath, true);
	}

	/**
	 * 删除指定目录、文件
	 *
	 * @param dirPath
	 * @param isDelDir 是否删除根目录
	 * @return
	 */
	public static void deleteFile(String dirPath, boolean isDelDir) {
		if (TextUtils.isEmpty(dirPath)) return;

		File file = new File(dirPath);
		try {
			if (!file.exists())
				return;

			if (file.isFile()) {
				file.delete();
				return;
			}

			for (File f : file.listFiles()) {
				if (f.isFile()) {
					f.delete();
				} else if (f.isDirectory()) {
					deleteFile(f.getAbsolutePath());
				}
			}

			if (isDelDir)
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拷贝文件
	 *
	 * @param fromPath
	 * @param toDirPath
	 * @param toName
	 */
	public static boolean copyFile(String fromPath, String toDirPath, String toName) {
		if (isFileExist(fromPath)) {
			try {
				makeFolders(toDirPath);
				FileInputStream inStream = new FileInputStream(fromPath);
				FileOutputStream outStream = new FileOutputStream(toDirPath + toName, false);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, length);
				}
				outStream.flush();
				outStream.close();
				inStream.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 复制文件夹
	 *
	 * @param oldPath
	 * @param newPath
     */
	public static void copyFolder(String oldPath, String newPath) throws Throwable {
		if (TextUtils.isEmpty(oldPath) || TextUtils.isEmpty(newPath))
			return;

		makeFolders(newPath);

		newPath = newPath.endsWith(File.separator) ? newPath : newPath + File.separator;

		String[] fileArr = new File(oldPath).list();
		String tempPath = null;
		for (String fileStr : fileArr) {
			if (oldPath.endsWith(File.separator)) {
				tempPath = oldPath + fileStr;
			} else {
				tempPath = oldPath + File.separator + fileStr;
			}

			File tempFile = new File(tempPath);

			if (tempFile.isFile()) {
				FileInputStream inStream = new FileInputStream(tempFile);
				FileOutputStream outStream = new FileOutputStream(newPath + fileStr, false);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, length);
				}
				outStream.flush();
				outStream.close();
				inStream.close();
			}

			if (tempFile.isDirectory()) {
				copyFolder(tempPath, newPath + fileStr);
			}
		}
	}
	/**
	 * 写文本内容
	 *
	 * @param dirPath   xxx/xxx/
	 * @param fileName
	 * @param content
	 * @return
	 */
	public static void writerString(String dirPath, String fileName, String content) {
		makeFolders(dirPath);
		writerString(dirPath + fileName, content);
	}

	public static void writerString(String filePath, String content) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				writerString(new File(filePath), content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void writerString(File file, String content) {
		if (file != null && content != null && createFile(file)) {
			try {
				FileWriter fW = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fW);
				bw.write(content);
				bw.flush();
				bw.close();
				fW.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读纯文本内容
	 *
	 * @param filePath
	 * @return
	 */
	public static String readerString(String filePath) {
		try {
			return readerString(new File(filePath));
		} catch (Exception e) {
			return null;
		}
	}

	public static String readerString(File file) {
		if (isFileExist(file)) {
			try {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				StringBuilder builder = new StringBuilder();
				String b = null;
				while ((b = br.readLine()) != null) {
					builder.append(b);
				}
				fr.close();
				br.close();
				return builder.toString();
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String readerString(InputStream inStream) {
		if (inStream == null)
			return null;
		try {
			InputStreamReader isr = new InputStreamReader(inStream);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder builder = new StringBuilder();
			String b = null;
			while ((b = br.readLine()) != null) {
				builder.append(b);
			}
			isr.close();
			br.close();
			return builder.toString();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取文本文件
	 *
	 * @param filePath
	 * @return
	 */
	public static byte[] readerStreamByte(String filePath) {
		try {
			FileInputStream in = new FileInputStream(new File(filePath));
			return readerStreamByte(in);
		} catch (Exception e) {
            Log.e(TAG, "readInStream e[" + e + "]");
		}
		return null;
	}

	public static String readerStreamString(String filePath) {
		try {
			FileInputStream inStream = new FileInputStream(new File(filePath));
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (Exception e) {
			Log.e(TAG, "readInStream e[" + e + "]");
		}
		return null;
	}

	private static byte[] readerStreamByte(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			outStream.close();
			inStream.close();
			return outStream.toByteArray();
		} catch (Exception e) {
			Log.e(TAG, "readInStream e[" + e + "]");
		}
		return null;
	}


	public static void writerStreamString(String filePath, String content) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
			FileOutputStream outStream = new FileOutputStream(new File(filePath));
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			inputStream.close();
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			Log.e(TAG, "readInStream e[" + e + "]");
		}
	}

	public static void writerStreamByte(String filePath, byte[] bytes) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			FileOutputStream outStream = new FileOutputStream(new File(filePath));
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			inputStream.close();
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			Log.e(TAG, "readInStream e[" + e + "]");
		}
	}

	/**
	 * 获取文件大小
	 *
	 * @param size 字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 *
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			out.write(ch);
		}
		byte buffer[] = out.toByteArray();
		out.close();
		return buffer;
	}

	/**
	 * 列出root目录下所有子目录
	 *
	 * @param root
	 * @return 绝对路径
	 */
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					allDir.add(f.getAbsolutePath());
				}
			}
		}
		return allDir;
	}

	/**
	 * 截取路径名
	 *
	 * @return
	 */
	public static String getPathName(String absolutePath) {
		if (TextUtils.isEmpty(absolutePath)) {
			return "";
		}
		int start = absolutePath.lastIndexOf(File.separator) + 1;
		int end = absolutePath.length();
		return absolutePath.substring(start, end);
	}

	/**
	 * 使用当前时间戳拼接一个唯一的文件名
	 *
	 * @return
	 */
	public static String getTempFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS", Locale.getDefault());
		return format.format(new Timestamp(System.currentTimeMillis()));
	}

	public static String getNewFileName(String folder, String suffix) {
		String result = "";
		String newSuffix = suffix.startsWith(".") ? suffix : ("." + suffix);
		makeFolders(folder);
		do {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SS", Locale.getDefault());
			result = format.format(new Timestamp(System.currentTimeMillis())) + newSuffix;
		} while (isFileExist(folder + result));
		LogUtil.i(TAG, "getNewFileName fileName[" + result + "]");
		return result;
	}

	/**
	 * 拷贝文件
	 *
	 * @param fromFile
	 * @param toFile
	 * @param rewrite
	 * @author wangbingqi
	 */
	public static boolean copyFile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return false;
		}
		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}

		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	/**
	 * 获取目录内文件个数
	 *
	 * @return
	 */
	public static long getFileList(File dir) {
		long count = 0;
		try {
			if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles();
				count = files.length;
				for (File file : files) {
					if (file.isDirectory()) {
						count = count + getFileList(file);// 递归
						count--;
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 判断是否存在SD卡并且剩余容量大于sizeMb
	 *
	 * @param sizeMb
	 * @return
	 */
	public static boolean isValidableSpace(int sizeMb) {
		boolean isHasSpace = false;
		try {
			if (isExistSdcard()) {
				String sdcard = Environment.getExternalStorageDirectory().getPath();
				StatFs statFs = new StatFs(sdcard);
				long blockSize = statFs.getBlockSize();
				long blocks = statFs.getAvailableBlocks();
				long availableSpare = (blocks * blockSize) / (1024 * 1024);
				LogUtil.d(TAG, "availableSpare = " + availableSpare);
				if (availableSpare > sizeMb) {
					isHasSpace = true;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return isHasSpace;
	}
}