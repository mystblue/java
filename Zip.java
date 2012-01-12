import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;


/**
* 
* 複数のファイル及び、サブディレクトリを含めたディレクトリを
* zip圧縮・解凍する為のクラスです。
* 
* @author HarukaV49
* 
*/
public class FileZip {

	private static final int EOF = -1;
	private static final int ZIP_BUFF_SIZE = 1024;

	/**
	* デフォルト(ファイル名解析)エンコーダを使用してファイル及びディレクトリをZIP圧縮します。
	* @param zipFilename 作成されるZIPファイル名
	* @param targetFiles 圧縮対象のファイル及びディレクトリ名列
	* @return 作成されたZIPファイル
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File zip(String zipFilename, String... targetFiles) throws ZipException, FileNotFoundException, IOException {
		return zip(zipFilename, targetFiles, null);
	}
	/**
	* ファイル及びディレクトリをZIP圧縮します。
	* @param zipFilename 作成されるZIPファイル名
	* @param targetFiles targetFiles 圧縮対象のファイル及びディレクトリ名配列
	* @param encoding ファイル名解析エンコーダ名（<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">一覧</A>）
	* @return 作成されたZIPファイル
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File zip(String zipFilename, String[] targetFiles, String encoding) throws ZipException, FileNotFoundException, IOException {
		int n = targetFiles.length;
		File[] files = new File[n];
		for(int i=0; i<n; i++) {
			files[i] = new File( targetFiles[i] );
		}
		return zip( new File(zipFilename), files, encoding);
	}
	/**
	* ファイル及びディレクトリをZIP圧縮します。
	* @param zipFile 作成されるZIPファイル
	* @param targetFiles targetFiles 圧縮対象のファイル及びディレクトリ配列
	* @param encoding ファイル名解析エンコーダ名（<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">一覧</A>）
	* @return 作成されたZIPファイル
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File zip(File zipFile, File[] targetFiles, String encoding) throws ZipException, FileNotFoundException, IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		out.setEncoding(encoding);
		for(int i=0; i<targetFiles.length; i++) {
			int deleteLength = targetFiles[i].getPath().length() - targetFiles[i].getName().length();
			zip(out, targetFiles[i], deleteLength);
		}
		out.close();
		return zipFile;
	}
	private static void zip(ZipOutputStream out, File targetFile, int deleteLength) throws IOException {
		if( targetFile.isDirectory() ) {
			File[] files = targetFile.listFiles();
			for(int i=0; i<files.length; i++) {
				zip(out, files[i], deleteLength);
			}
		} else {
			ZipEntry target = new ZipEntry( targetFile.getPath().substring(deleteLength) );
			out.putNextEntry(target);
			byte buf[] = new byte[ZIP_BUFF_SIZE];
			int count;
			BufferedInputStream in = new BufferedInputStream( new FileInputStream(targetFile) );
			while( (count = in.read(buf, 0, ZIP_BUFF_SIZE)) != EOF ) {
				out.write(buf, 0, count);
			}
			in.close();
			out.closeEntry();
		}
	}

	/**
	* デフォルト(ファイル名解析)エンコーダを使用してZIPファイルを解凍します。
	* @param filename ZIPファイル名
	* @param outDir 解凍先ディレクトリ名
	* @return 解凍されたファイルまたはディレクトリ
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File unzip(String filename, String outDir) throws ZipException, FileNotFoundException, IOException {
		return unzip(filename, outDir, null);
	}
	/**
	* ZIPファイルを解凍します。
	* @param filename ZIPファイル名
	* @param outDir 解凍先ディレクトリ名
	* @param encoding ファイル名解析エンコーダ名（<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">一覧</A>）
	* @return 解凍されたファイルまたはディレクトリ
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File unzip(String filename, String outDir, String encoding) throws ZipException, FileNotFoundException, IOException {
		return unzip( new File(filename), new File(outDir), encoding );
	}
	/**
	* ZIPファイルを解凍します。
	* @param filename ZIPファイル
	* @param outDir 解凍先ディレクトリ
	* @param encoding ファイル名解析エンコーダ名（<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">一覧</A>）
	* @return 解凍されたファイルまたはディレクトリ
	* @throws ZipException ZIP例外
	* @throws FileNotFoundException ファイル例外
	* @throws IOException IO例外
	*/
	public static File unzip(File filename, File outDir, String encoding) throws ZipException, FileNotFoundException, IOException {
		ZipFile zipFile = new ZipFile(filename, encoding);
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> enumeration = zipFile.getEntries();
		while( enumeration.hasMoreElements() ) {
			ZipEntry entry = enumeration.nextElement();
			if( entry.isDirectory() ) {
				new File(entry.getName()).mkdirs();
			} else {
				File file = new File(outDir, entry.getName());
				file.getParentFile().mkdirs();
				FileOutputStream out = new FileOutputStream(file);
				InputStream in = zipFile.getInputStream(entry);
				byte[] buf = new byte[ZIP_BUFF_SIZE];
				int size = 0;
				while( (size = in.read(buf)) != EOF ) {
					out.write(buf, 0, size);
				}
				out.close();
				in.close();
			}
		}
		zipFile.close();
		return outDir;
	}

}
