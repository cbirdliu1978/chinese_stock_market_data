package com.goblin.n3.trade._inc_crawl.ext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.Misc;
import com.bmtech.utils.bmfs.MDir;
import com.bmtech.utils.bmfs.MFile;
import com.bmtech.utils.bmfs.MFileReader;
import com.bmtech.utils.bmfs.MFileReaderIterator;
import com.bmtech.utils.log.L;
import com.goblin.n3.trade._inc_crawl.Parser4Min05;
import com.goblin.n3.trade._inc_crawl.Sina_Min05Parser;

public class DoMDirMin05 {
	Parser4Min05 parser;
	Map<String, Map<String, MinDay>> mapAll = new HashMap<>();
	Map<String, List<MinDay>> results = new HashMap<>();

	public DoMDirMin05(Parser4Min05 parser) {
		this.parser = parser;
	}

	public String getName() {
		return "min5";
	}

	public String getFileSuffix() {
		return ".mn5";
	}

	public void loadToMap(File mdir) throws IOException {
		MDir dir = MDir.open(mdir);
		try {
			MFileReaderIterator itr = dir.openReader();

			while (itr.hasNext()) {
				MFileReader e = itr.next();
				MFile mf = e.getMfile();
				String txt = new String(e.getBytesUnGZiped());
				String code = Misc.getSubString(mf.getName(), null, ".");
				Map<String, MinDay> map = mapAll.get(code);
				if (map == null) {
					map = new HashMap<>();
					mapAll.put(code, map);
				}
				// L.f("load %s, file length %s", mf, txt.length());
				try {
					List<MinDay> mindayList = parser.parse(code, txt);
					// parser.check(code, mindayList);
					L.f("load %s got records %s", mf, mindayList.size());
					for (MinDay minday : mindayList) {
						String daytime = minday.getDay() + minday.getStartTimeStr();
						map.put(daytime, minday);
					}
				} catch (Exception exc) {
					exc.printStackTrace();
					L.f("when parse %s of mfile %s", code, mf);
					Consoler.pause();
				}
			}
			itr.close();
		} finally {
			dir.close();
		}
	}

	public Map<String, List<MinDay>> doAllList() {

		Set<String> set = mapAll.keySet();
		for (String code : set) {
			Map<String, MinDay> map = this.mapAll.get(code);
			List<MinDay> lst = new ArrayList<>(map.values());
			lst.sort((a, b) -> {
				int dayDiff = a.getDay() - b.getDay();
				if (dayDiff == 0) {
					return a.getStartTime() - b.getStartTime();
				} else
					return dayDiff;
			});
			if (lst.size() > 0) {
				try {
					parser.check(code, lst);
					this.results.put(code, lst);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return results;
	}

	public static void main(String[] args) throws IOException {
		String path = "C:\\data\\datamine\\bak-sinaInc\\min5\\20180331";
		File file = new File(path);

		DoMDirMin05 p = new DoMDirMin05(new Sina_Min05Parser());
		p.loadToMap(file);

		Map<String, MinDay> c = p.mapAll.get("600301");
		ArrayList<Entry<String, MinDay>> lst = new ArrayList<>(c.entrySet());
		lst.sort((a, b) -> {
			return a.getKey().compareTo(b.getKey());
		});
		String txt = lst.toString();
		System.out.println(txt);

	}
}