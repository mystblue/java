import java.io.File;

public class Sort implements java.util.Comparator<Object> {
	public int stringCompare(String str1, String str2) {
		int len1 = str1.length();
		int len2 = str2.length();
		int min = len1 >= len2 ? len2 : len1;

		for (int i = 0; i < min; i++) {
			char c1 = str1.charAt(i);
			char c2 = str2.charAt(i);
			if (c1 == c2) {
				continue;
			} else {
				if (Character.isDigit(c1) && Character.isDigit(c2)) {
					int i1 = i + 1;
					int i2 = i + 1;
					String strNum1 = String.valueOf(c1);
					String strNum2 = String.valueOf(c2);
					while (i1 < len1) {
						if (Character.isDigit(str1.charAt(i1))) {
							strNum1 = strNum1 + str1.charAt(i1);
						} else {
							break;
						}
						i1++;
					}
					while (i2 < len2) {
						if (Character.isDigit(str2.charAt(i2))) {
							strNum2 = strNum2 + str2.charAt(i2);
						} else {
							break;
						}
						i2++;
					}
					if (Long.parseLong(strNum1) == Long.parseLong(strNum2)) {
						int ret = str1.compareTo(str2);
						if (ret == 0) return 0;
						return ret > 0 ? 1 : -1;
					} else {
						return Long.parseLong(strNum1) < Long.parseLong(strNum2) ? -1 : 1;
					}
				}
				if (isSymbol(c1) && !isSymbol(c2)) {
					return -1;
				}
				if (!isSymbol(c1) && isSymbol(c2)) {
					return 1;
				}
				if (c1 == c2) {
					return 0;
				} else if (c1 > c2) {
					return 1;
				} else {
					return -1;
				}
			}
		}
		int ret = str1.compareTo(str2);
		if (ret == 0) return 0;
		return ret > 0 ? 1 : -1;
	}

	public int compare(Object o1, Object o2) {
		if (o1 instanceof String && o2 instanceof String) {
			String str1 = (String) o1;
			String str2 = (String) o2;

			return stringCompare(str1, str2);
		} else if (o1 instanceof File && o2 instanceof File) {
			File f1 = (File)o1;
			File f2 = (File)o2;

			return stringCompare(f1.getName(), f2.getName());
		} else {
			return 0;
		}
	}

	public boolean isSymbol(char c) {
		char[] symbol = {'.', '?', '-', '_', '[', ']', '(', ')', '+', '*', '/', '<', '>', '!', '"', '#', '$', '%', '&', '\'','=', '~', '^', '@', '{', '}', ':', ';'};
		for (char ch : symbol) {
			if (ch == c) {
				return true;
			}
		}
		return false;
	}
}
