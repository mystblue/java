import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipUtil {
	public static void main(String[] args) {
		compress("test.zip", "img");
	}
	public static void compress(String zipFilename, String folderName) {
		try {
			File zipFile = new File(zipFilename);
			File folder = new File(folderName);
			File[] files = { folder };
			ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new FileOutputStream(zipFile));
			zos.setEncoding("Windows-31J");
			try {
				encode(zos, files, folderName);
			} finally {
				zos.finish();
				zos.flush();
				zos.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static byte[] buf = new byte[1024];

	private static void encode(ZipArchiveOutputStream zos, File[] files, String folderName) throws Exception {
		for (File file : files) {
			if (file.isDirectory()) {
				encode(zos, file.listFiles(), folderName);
			} else {
				String filePath = file.getPath().replace('\\', '/');
/*				if (filePath.startsWith(folderName + "/")) {
					filePath = filePath.substring((folderName + "/").length());
				}*/
				System.out.println(filePath);
				ZipArchiveEntry ze = new ZipArchiveEntry(filePath);
				zos.putArchiveEntry(ze);
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				int count = 0;
				while ((count = is.read(buf, 0, 1024)) != -1) {
					zos.write(buf, 0, count);
				}
				is.close();
				zos.closeArchiveEntry();
			}
		}
	}

	/*
	public static void decode(File zipFile) throws Exception {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()) {
			// System.out.println(ze.getName());
			if (ze.isDirectory()) continue;

			BufferedInputStream bis = null;
			try {
				// ZipFile から 対象ZipEntry の InputStream を取り出す。
				InputStream is = zipFile.getInputStream(ze);
				// 効率よく読み込むため BufferedInputStream でラップする。
				bis = new BufferedInputStream(is);
				// 入力ストリームから読み込み、出力ストリームへ書き込む。
				int ava;
				while ((ava = bis.available()) > 0) {
					byte[] bs = new byte[ava];
					// 入力
					bis.read(bs);
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					if (bis != null)
						// ストリームは必ず close する。
						bis.close();
				} catch (IOException e) {
				}
			}
		}
		zis.close();
	}*/
}
