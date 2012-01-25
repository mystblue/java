import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rename {
	public static void main(String[] args) {
		rename();
	}

	public static String getNewName(String fileName) {
		Pattern pattern = Pattern.compile("\\[a\\](.+)");
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	private static void rename() {
		File cd = new File(".");
		File[] files = cd.listFiles();
		for (File f : files) {
			System.out.println(f.getName());
			if (f.isFile()) {
				String newName = getNewName(f.getName());
				System.out.println(newName);
				if (newName != null) {
					f.renameTo(new File(newName));
				}
			}
		}
	}
}
