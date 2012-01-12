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
* �����̃t�@�C���y�сA�T�u�f�B���N�g�����܂߂��f�B���N�g����
* zip���k�E�𓀂���ׂ̃N���X�ł��B
* 
* @author HarukaV49
* 
*/
public class FileZip {

	private static final int EOF = -1;
	private static final int ZIP_BUFF_SIZE = 1024;

	/**
	* �f�t�H���g(�t�@�C�������)�G���R�[�_���g�p���ăt�@�C���y�уf�B���N�g����ZIP���k���܂��B
	* @param zipFilename �쐬�����ZIP�t�@�C����
	* @param targetFiles ���k�Ώۂ̃t�@�C���y�уf�B���N�g������
	* @return �쐬���ꂽZIP�t�@�C��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
	*/
	public static File zip(String zipFilename, String... targetFiles) throws ZipException, FileNotFoundException, IOException {
		return zip(zipFilename, targetFiles, null);
	}
	/**
	* �t�@�C���y�уf�B���N�g����ZIP���k���܂��B
	* @param zipFilename �쐬�����ZIP�t�@�C����
	* @param targetFiles targetFiles ���k�Ώۂ̃t�@�C���y�уf�B���N�g�����z��
	* @param encoding �t�@�C������̓G���R�[�_���i<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">�ꗗ</A>�j
	* @return �쐬���ꂽZIP�t�@�C��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
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
	* �t�@�C���y�уf�B���N�g����ZIP���k���܂��B
	* @param zipFile �쐬�����ZIP�t�@�C��
	* @param targetFiles targetFiles ���k�Ώۂ̃t�@�C���y�уf�B���N�g���z��
	* @param encoding �t�@�C������̓G���R�[�_���i<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">�ꗗ</A>�j
	* @return �쐬���ꂽZIP�t�@�C��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
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
	* �f�t�H���g(�t�@�C�������)�G���R�[�_���g�p����ZIP�t�@�C�����𓀂��܂��B
	* @param filename ZIP�t�@�C����
	* @param outDir �𓀐�f�B���N�g����
	* @return �𓀂��ꂽ�t�@�C���܂��̓f�B���N�g��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
	*/
	public static File unzip(String filename, String outDir) throws ZipException, FileNotFoundException, IOException {
		return unzip(filename, outDir, null);
	}
	/**
	* ZIP�t�@�C�����𓀂��܂��B
	* @param filename ZIP�t�@�C����
	* @param outDir �𓀐�f�B���N�g����
	* @param encoding �t�@�C������̓G���R�[�_���i<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">�ꗗ</A>�j
	* @return �𓀂��ꂽ�t�@�C���܂��̓f�B���N�g��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
	*/
	public static File unzip(String filename, String outDir, String encoding) throws ZipException, FileNotFoundException, IOException {
		return unzip( new File(filename), new File(outDir), encoding );
	}
	/**
	* ZIP�t�@�C�����𓀂��܂��B
	* @param filename ZIP�t�@�C��
	* @param outDir �𓀐�f�B���N�g��
	* @param encoding �t�@�C������̓G���R�[�_���i<A HREF="http://java.sun.com/javase/6/docs/technotes/guides/intl/encoding.doc.html">�ꗗ</A>�j
	* @return �𓀂��ꂽ�t�@�C���܂��̓f�B���N�g��
	* @throws ZipException ZIP��O
	* @throws FileNotFoundException �t�@�C����O
	* @throws IOException IO��O
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
