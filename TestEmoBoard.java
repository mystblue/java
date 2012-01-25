import java.io.*;
import java.net.*;
import java.util.*;

import junit.framework.TestCase;

import org.json.simple.*;

public class TestEmoBoard extends TestCase {
	
	String cookie = null;
	
	protected void setUp() throws Exception {
		cookie = login();
	}
	
	private static String login() throws Exception {
		URL url = new URL("http://172.17.101.138/56mobile/Logon/Logon");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setRequestProperty( "Connection", "keep-alive" );
		conn.connect();

		String query = "user=administrator&pwd=pass@INOS";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		out.write(query);
		out.flush();
		out.close();

		Map<String, List<String>> header = conn.getHeaderFields();
		String cookie = "";
		for (String key : header.keySet()) {
			List<String> list = header.get(key);
			if (key != null && key.equals("Set-Cookie")) {
				String val = list.get(0);
				int index = val.indexOf(" ");
				cookie = val.substring(0, index);
				cookie += list.get(1);
			}
		}
		return cookie;
	}

	public void testBoardList() throws Exception {
		String json = getBoardList();
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(7, list.size());
			for (Object board : list) {
//				System.out.println(((JSONObject)board).get("name"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}
	
	public String getBoardList() throws Exception {
		return post("Board/BoardList", "");
	}
	
	public void testDocList1() throws Exception {
		String json = getDocList(1, 15, "36afe3430c05e94486d4da4ddb51169b");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(15, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
//				System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 検索結果最大数を 10 に変えて、検索結果の取得数が 10 になるかをテスト */
	public void testDocList2() throws Exception {
		String json = getDocList(1, 10, "36afe3430c05e94486d4da4ddb51169b");
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(10, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
//				System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 検索結果最大数を 10 に変えて、検索結果の取得数が 10 になるかをテスト */
	public void testDocList3() throws Exception {
		String json = getDocList(2, 10, "36afe3430c05e94486d4da4ddb51169b");
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(10, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
//				System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 検索結果が 0 の場合のテスト */
	public void testDocList4() throws Exception {
		String json = getDocList(1, 10, "6dc0039b01d01d4f9f7be2e6d536d496");
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(0L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertNull(list);
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** ボードの Guid が不正な場合のテスト */
	public void testDocList5() throws Exception {
		String json = getDocList(1, 10, "xxx0039b01d01d4f9f7be2e6d536d496");
//		System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(0L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertNull(list);
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 文書情報取得のテスト */
	public String getDocList(int page, int maxPerPage, String boardGuid) throws Exception {
		return post("Board/DocList",
			String.format("page=%d&maxPerPage=%d&onlyNewDoc=false&onlyUnReadDoc=false&boardGuid=%s",
				page, maxPerPage, boardGuid));
	}
	
	/** 添付文書がない場合のテスト */
	public void testReferDoc1() throws Exception {
		String json = getDoc("36afe3430c05e94486d4da4ddb51169b", "4-36afe3430c05e94486d4da4ddb51169b");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertNull(map.get("filelist"));
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 添付文書がある場合のテスト */
	public void testReferDoc2() throws Exception {
		String json = getDoc("36afe3430c05e94486d4da4ddb51169b", "37-36afe3430c05e94486d4da4ddb51169b");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertNotNull(map.get("filelist"));
			Object objFl = map.get("filelist");
			if (objFl instanceof JSONArray) {
				JSONArray filelist = (JSONArray) objFl;
				assertEquals(1, filelist.size());
			}
			//System.out.println(map.get("filelist"));
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 不正な文書 ID の場合のテスト[500が返るのでテスト不可] */
	//public void testReferDoc3() throws Exception {
	//	String json = getDoc("36afe3430c05e94486d4da4ddb51169b", "99-xxxfe3430c05e94486d4da4ddb51169b");
	//}

	public String getDoc(String boardGuid, String docId) throws Exception {
		return post("Board/ReferDoc",
			String.format("boardGuid=%s&docId=%s", boardGuid, docId));
	}
	
	/***************************************************************
	  文書検索のテスト
	***************************************************************/
	
	/** 検索結果なし */
	public void testSearchDoc1() throws Exception {
		String json = searchDoc(1, 15, "false", "false",
			"36afe3430c05e94486d4da4ddb51169b,99297331f5d86946b3fe8edec9e7caea",
			"システム管理者", "#", "true", "true", "true", 2,
			"2011/12/12 00:00:00", "2012/02/01 00:00:00", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(0L, map.get("totalpage"));
			assertNull(map.get("list"));
		} else {
			fail("JSON の形式が不正です。");
		}
	}
	
	/** 検索結果あり */
	public void testSearchDoc2() throws Exception {
		String json = searchDoc(1, 15, "false", "false",
			"36afe3430c05e94486d4da4ddb51169b",
			"システム管理者", "#", "true", "true", "true", 1,
			"2011/12/12 00:00:00", "2012/02/01 00:00:00", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(2L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(15, list.size());
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 検索結果数を 10 件に変更 */
	public void testSearchDoc3() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"36afe3430c05e94486d4da4ddb51169b",
			"システム管理者", "#", "true", "true", "true", 1,
			"2011/12/12 00:00:00", "2012/02/01 00:00:00", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(2L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(10, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 検索結果数を 10 件に変更し、次のページを取得 */
	public void testSearchDoc4() throws Exception {
		String json = searchDoc(2, 10, "false", "false",
			"36afe3430c05e94486d4da4ddb51169b",
			"システム管理者", "#", "true", "true", "true", 1,
			"2011/12/12 00:00:00", "2012/02/01 00:00:00", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(2L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(9, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 優先度が高いものだけ検索 */
	public void testSearchDoc5() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"99297331f5d86946b3fe8edec9e7caea",
			"", "", "false", "false", "false", 0,
			"", "", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 優先度が高いものだけ検索 */
	public void testSearchDoc6() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"99297331f5d86946b3fe8edec9e7caea",
			"", "", "false", "false", "false", 2,
			"", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(2, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 優先度が低いものだけ検索 */
	public void testSearchDoc7() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"99297331f5d86946b3fe8edec9e7caea",
			"", "", "false", "false", "false", 2,
			"", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(2, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 期間で検索 */
	public void testSearchDoc8() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "", "false", "false", "false", 1,
			"2011/12/27 10:30:00", "2011/12/27 10:50:00", "between");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(2, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「まで」で検索 */
	public void testSearchDoc9() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "", "false", "false", "false", 1,
			"", "2011/12/28 00:00:00", "to");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(2L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(10, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「から」で検索 */
	public void testSearchDoc10() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "", "false", "false", "false", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(5, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「タイトル」で検索 */
	public void testSearchDoc11() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "test", "true", "false", "false", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「本文」で検索 */
	public void testSearchDoc12() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "これは本文です。", "false", "true", "false", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「添付ファイル」で検索 */
	public void testSearchDoc13() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "abc", "false", "false", "true", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(2, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「添付ファイル」で検索 2 */
	public void testSearchDoc14() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "添付", "false", "false", "true", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「添付ファイル」で検索 3 */
	public void testSearchDoc15() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "word", "false", "false", "true", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「添付ファイル」で検索 4 */
	public void testSearchDoc16() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "Excel", "false", "false", "true", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 「添付ファイル」で検索 5 */
	public void testSearchDoc17() throws Exception {
		String json = searchDoc(1, 10, "false", "false",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "PowerPoint", "false", "false", "true", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(1, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 未読のみで検索 */
	public void testSearchDoc18() throws Exception {
		String json = searchDoc(1, 10, "false", "true",
			"de059031cb484343ae58c2ca8a361bf9",
			"", "", "false", "false", "false", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(4, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 複数ボードで検索 */
	public void testSearchDoc19() throws Exception {
		String json = searchDoc(1, 10, "false", "true",
			"99297331f5d86946b3fe8edec9e7caea,de059031cb484343ae58c2ca8a361bf9",
			"", "test", "true", "false", "false", 100,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(2, list.size());
			for (Object objBoard : list) {
				JSONObject board = (JSONObject) objBoard;
				//System.out.println(((JSONObject)board).get("Subject"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** 所有者名で検索 */
	public void testSearchDoc20() throws Exception {
		String json = searchDoc(1, 10, "false", "true",
			"de059031cb484343ae58c2ca8a361bf9",
			"なにか", "", "false", "false", "false", 1,
			"2011/12/28 00:00:00", "", "from");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(0L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertNull(list);
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	public String searchDoc(int page, int maxPerPage, String onlyNewDoc,
			String onlyUnReadDoc, String boardGuid, String ownername,
			String fulltext, String searchTitle, String searchBody,
			String searchFile, int importance, String dateFrom,
			String dateTo, String dateOption) throws Exception {
		return post("Board/SearchDoc",
			String.format("page=%d&maxPerPage=%d&onlyNewDoc=%s&onlyUnReadDoc=%s&boardGuid=%s&ownername=%s&fulltext=%s&searchTitle=%s&searchBody=%s&searchFile=%s&importance=%d&dateFrom=%s&dateTo=%s&dateOption=%s",
				page, maxPerPage, onlyNewDoc, onlyUnReadDoc, boardGuid,
				ownername, fulltext, searchTitle, searchBody, searchFile, importance,
				dateFrom, dateTo, dateOption));
	}
	
	/** よく見るボードのテスト */
	public void testWatchBoardList() throws Exception {
		String json = getWatchBoardList();
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(9, list.size());
			for (Object board : list) {
			//	System.out.println(((JSONObject)board).get("name"));
			}
		} else {
			fail("JSON の形式が不正です。");
		}
	}
	
	public String getWatchBoardList() throws Exception {
		return post("Board/WatchBoardList", "");
	}

	/** What's New のテスト すべて */
	public void testWhatsNewList1() throws Exception {
		String json = getWhatsNewList(1, 10, "false","36afe3430c05e94486d4da4ddb51169b,99297331f5d86946b3fe8edec9e7caea,9d590e399e06da44b2997fbb29739cc7,4b92aebb1094e7488bd8b3e819005c12,de059031cb484343ae58c2ca8a361bf9,6dc0039b01d01d4f9f7be2e6d536d496");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(6, list.size());
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** What's New のテスト 未読のみ */
	public void testWhatsNewList2() throws Exception {
		String json = getWhatsNewList(1, 10, "true","36afe3430c05e94486d4da4ddb51169b,99297331f5d86946b3fe8edec9e7caea,9d590e399e06da44b2997fbb29739cc7,4b92aebb1094e7488bd8b3e819005c12,de059031cb484343ae58c2ca8a361bf9,6dc0039b01d01d4f9f7be2e6d536d496");
		//System.out.println(json);
		Object obj = JSONValue.parse(json);
		if (obj instanceof JSONObject) {
			JSONObject map = (JSONObject)obj;
			assertEquals(0L, map.get("errCode"));
			assertEquals(1L, map.get("totalpage"));
			JSONArray list = (JSONArray)map.get("list");
			assertEquals(5, list.size());
		} else {
			fail("JSON の形式が不正です。");
		}
	}

	/** What's Newのテスト */
	public String getWhatsNewList(int page, int maxPerPage, String onlyUnReadDoc, String boardGuid) throws Exception {
		return post("Board/NewDocList",
			String.format("page=%d&maxPerPage=%d&onlyNewDoc=false&onlyUnReadDoc=%s&boardGuid=%s",
				page, maxPerPage, onlyUnReadDoc, boardGuid));
	}

	/** 添付ファイルのテスト */
	public void testGetAttachment() throws Exception {
		URL url = new URL("http://172.17.101.138/56mobile/Board/GetAttachment?docId=37-36afe3430c05e94486d4da4ddb51169b&fileId=1&extension=txt&fileName=js_list.txt");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty( "Cookie", cookie );
		conn.setRequestProperty( "Connection", "keep-alive" );
		conn.connect();
		
		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			StringBuffer buf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			int c;
			while( (c = reader.read()) != -1) {
				buf.append((char)c);
			}
			reader.close();
			//System.out.println(buf.toString());
		} else {
			fail("レスポンスコードが 200 ではありません。");
		}
	}
	public String post(String method, String query) throws Exception {
		URL url = new URL("http://172.17.101.138/56mobile/" + method);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setRequestProperty( "Cookie", cookie );
		conn.setRequestProperty( "Connection", "keep-alive" );
		conn.connect();

		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		out.write(query);
		out.flush();
		out.close();

		Map<String, List<String>> header = conn.getHeaderFields();
		for (String key : header.keySet()) {
			List<String> list = header.get(key);
		}
		StringBuffer buf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
		int c;
		while( (c = reader.read()) != -1) {
			buf.append((char)c);
		}
		reader.close();
		return buf.toString();
	}
}
