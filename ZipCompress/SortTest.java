import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

public class SortTest extends TestCase {

	Sort sort = null;

	protected void setUp() {
		sort = new Sort();
	}

	public void testSymbol() {
		assertTrue(sort.isSymbol('\''));
	}

	public void testSymbol2() {
		assertFalse(sort.isSymbol('a'));
	}

	public void testCompare() {
		assertEquals(0, sort.compare("a", "a"));
		assertEquals(1, sort.compare("b", "a"));
		assertEquals(-1, sort.compare("a", "b"));
		assertEquals(0, sort.compare("1", "1"));
		assertEquals(-1, sort.compare("1", "2"));
		assertEquals(1, sort.compare("2", "1"));
		assertEquals(0, sort.compare("111", "111"));
		assertEquals(0, sort.compare("_", "_"));
		assertEquals(-1, sort.compare("!", "_"));
		assertEquals(-1, sort.compare("-.jpg", "1.jpg"));
		assertEquals(-1, sort.compare("001.jpg", "01.jpg"));
		assertEquals(-1, sort.compare("001.jpg", "a001.jpg"));
		assertEquals(-1, sort.compare("00001.jpg", "001.jpg"));
		assertEquals(-1, sort.compare("001-256.jpg", "001.jpg"));
	}

	public void testArray1() {
		ArrayList list = new ArrayList();
		list.add("88.txt");
		list.add("5.txt");
		list.add("11.txt");

		ArrayList result = (ArrayList)list.clone();
		Collections.sort(result, new Sort());
		assertEquals("5.txt", result.get(0));
		assertEquals("11.txt", result.get(1));
		assertEquals("88.txt", result.get(2));
		assertEquals(3, result.size());
	}

	public void testArray2() {
		ArrayList list = new ArrayList();
		list.add("Ie5");
		list.add("Ie6");
		list.add("Ie4_01");
		list.add("Ie401sp2");
		list.add("Ie4_128");
		list.add("Ie501sp2");

		ArrayList result = (ArrayList)list.clone();
		Collections.sort(result, new Sort());
		assertEquals("Ie4_01", result.get(0));
		assertEquals("Ie4_128", result.get(1));
		assertEquals("Ie5", result.get(2));
		assertEquals("Ie6", result.get(3));
		assertEquals("Ie401sp2", result.get(4));
		assertEquals("Ie501sp2", result.get(5));
		assertEquals(6, result.size());
	}

}

