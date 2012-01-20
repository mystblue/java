import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipCompress {
	public static void main(String[] args) {
		File cd = new File(".");
		File[] files = cd.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				System.out.println(f.getName() + "Çà≥èkÇµÇ‹Ç∑ÅB");
				compress(f.getName() + ".zip", f.getName());
			}
		}
	}

	public static void compress(String zipFilename, String folderName) {
		try {
			File zipFile = new File(zipFilename);
			File folder = new File(folderName);
			File[] files = { folder };
			ZipArchiveOutputStream zos = new ZipArchiveOutputStream(zipFile);
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
		int counter = 1;

		ArrayList<File> list = new ArrayList<File>();
		for (File file : files) {
			list.add(file);
		}
		Collections.sort(list, new Sort());
		for (File file : list) {
			if (file.isDirectory()) {
				encode(zos, file.listFiles(), folderName);
			} else {
				String filePath = file.getPath().replace('\\', '/');
				System.out.println(filePath);
				String ext = filePath.substring(filePath.lastIndexOf("."));
				String newFileName = String.format("%04d" + ext, counter);
				ZipArchiveEntry ze = new ZipArchiveEntry(newFileName);
				zos.putArchiveEntry(ze);
				InputStream is = new BufferedInputStream(new FileInputStream(file));
				int count = 0;
				while ((count = is.read(buf, 0, 1024)) != -1) {
					zos.write(buf, 0, count);
				}
				is.close();
				zos.closeArchiveEntry();
				counter++;
			}
		}
	}
}
