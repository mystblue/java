import java.util.*;
import org.json.simple.*;

public class JSON {
	public static void main(String[] args) {
		String json = "{\"errCode\":0,\"list\":[{\"name\":\"ルート\",\"class\":\"Board.Root\",\"guid\":\"100\",\"depth\":\"0\",\"subboardguid\":\"36afe3430c05e94486d4da4ddb51169b,de059031cb484343ae58c2ca8a361bf9\"},{\"name\":\"よく見るボード\",\"class\":\"Favorite\",\"guid\":\"1\",\"depth\":\"1\",\"subboardguid\":\"99297331f5d86946b3fe8edec9e7caea\"},{\"name\":\"001-01\",\"class\":\"Board.Sub\",\"guid\":\"99297331f5d86946b3fe8edec9e7caea\",\"depth\":\"2\",\"subboardguid\":\"\"},{\"name\":\"What\u0027s New\",\"class\":\"New\",\"guid\":\"0\",\"depth\":\"1\",\"subboardguid\":\"\"},{\"name\":\"001\",\"class\":\"Board.Main\",\"guid\":\"36afe3430c05e94486d4da4ddb51169b\",\"depth\":\"1\",\"subboardguid\":\"99297331f5d86946b3fe8edec9e7caea,4b92aebb1094e7488bd8b3e819005c12\"},{\"name\":\"001-01\",\"class\":\"Board.Sub\",\"guid\":\"99297331f5d86946b3fe8edec9e7caea\",\"depth\":\"2\",\"subboardguid\":\"9d590e399e06da44b2997fbb29739cc7\"},{\"name\":\"001-01-01\",\"class\":\"Board.Sub\",\"guid\":\"9d590e399e06da44b2997fbb29739cc7\",\"depth\":\"3\",\"subboardguid\":\"\"},{\"name\":\"001-02\",\"class\":\"Board.Sub\",\"guid\":\"4b92aebb1094e7488bd8b3e819005c12\",\"depth\":\"2\",\"subboardguid\":\"\"},{\"name\":\"002\",\"class\":\"Board.Main\",\"guid\":\"de059031cb484343ae58c2ca8a361bf9\",\"depth\":\"1\",\"subboardguid\":\"\"}]}";

		Object obj = JSONValue.parse(json);
		JSONObject array = (JSONObject)obj;
		if (obj instanceof JSONObject) {
			System.out.println("map");
		} else if (obj instanceof JSONArray) {
			System.out.println("array");
		}
		Iterator iter = array.keySet().iterator();
		while(iter.hasNext()) {
			Object key = iter.next();
			System.out.println(key);
			System.out.println(array.get(key));
		}
	}
}
