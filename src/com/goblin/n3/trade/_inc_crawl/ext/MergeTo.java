package com.goblin.n3.trade._inc_crawl.ext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.L;
import com.goblin.n3.trade._inc_crawl.NetDownMinDayVerifier;

public class MergeTo {
	class O implements Comparable<O> {
		public O(long day, String line) {
			this.day = day;
			this.line = line;
		}

		long day;
		String line;

		@Override
		public boolean equals(Object obj) {
			O o = (O) obj;
			return o.day == this.day;
		}

		@Override
		public int hashCode() {
			if (day > Integer.MAX_VALUE) {
				return (int) (day % 10000);
			} else {
				return (int) day;
			}
		}

		@Override
		public int compareTo(O o) {
			long r = this.day - o.day;
			if (r == 0)
				return 0;
			else if (r > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	File baseFile;
	File incFile;
	File saveTo;
	Map<Long, O> s = new HashMap<Long, O>();

	private MergeTo(File baseFile, File incFile, File saveTo) {
		this.baseFile = baseFile;
		this.incFile = incFile;
		this.saveTo = saveTo;
	}

	// FIXME maybe should check cross
	void doFile(File f, DataType dataType) throws Exception {
		if (f.exists()) {
			List<MinDay> mdayList;
			String stockCode = Misc.substring(f.getName(), null, ".");
			if (dataType == DataType.min05) {
				mdayList = MinDay.readMinFile(f, dataType.orgBarRangeSeconds(), new Stock(stockCode, stockCode));
			} else {
				mdayList = MinDay.readDayFile(f, new Stock(stockCode, stockCode));
			}
			// mdayList = DataIntergrationVerifier.eliminateEmptyDay(mdayList);
			ForEach.asc(mdayList, (mday, i) -> {
				long time = mday.getDay() * 10000L + mday.getCloseHour() * 100 + mday.getCloseMinutes();
				String line = mday.toLine();
				O other = s.put(time, new O(time, line));
				if (other != null) {
					if (!other.line.equals(line)) {
						L.d("\n%s\ndiff:'%s'\n org:'%s'", stockCode, line, other.line);
					}
				}

			});
		}
	}

	private void merge(String code, DataType dt) throws Exception {
		Stock stk = AllStock.getStockByCode(code);
		doFile(baseFile, dt);
		doFile(incFile, dt);
		List<O> lst = new ArrayList<>(s.size());
		lst.addAll(s.values());
		Collections.sort(lst);
		ArrayList<MinDay> list = new ArrayList<>(lst.size());
		for (O o : lst) {
			list.add(MinDay.parseMinDay(o.line, dt.orgBarRangeSeconds(), stk));
		}
		List<MinDay> listNew = NetDownMinDayVerifier.verify(list, stk, DataType.min05);
		if (listNew.size() == 0) {
			Misc.throwNewRuntimeException("fail! size is 0 for stock %s,orgList %s", code, lst);
		}
		save(lst);
	}

	private void save(List<O> lst) throws Exception {
		LineWriter lw = new LineWriter(this.saveTo);
		ForEach.asc(lst, (line, i) -> {
			lw.writeLine(line.line);
		});
		lw.close();
	}

	public static void merge(File base, File inc, File dirSave) throws Exception {

		if (!base.exists()) {
			throw new Exception("dir not exists: " + base);
		}
		if (!inc.exists()) {
			throw new Exception("dir not exists: " + inc);
		}
		Misc.besureDirExists(dirSave);

		Set<String> name = new HashSet<>();
		ForEach.asc(base.listFiles(), (f, i) -> {
			name.add(f.getName());
		});
		ForEach.asc(inc.listFiles(), (f, i) -> {
			name.add(f.getName());
		});
		DataType dataType = DataType.min05;
		for (String x : name) {
			if (x.startsWith(".")) {
				L.f("skip file %s", name);
				continue;
			}
			System.out.println("merging stock " + x);
			File baseFile = new File(base, x);
			File incFile = new File(inc, x);
			File fSave = new File(dirSave, x);
			MergeTo m = new MergeTo(baseFile, incFile, fSave);
			String code = Misc.substring(x, null, ".");
			m.merge(code, dataType);
		}
	}

	public static void main(String... args) throws Exception {
		String baseDirStr = Consoler.readLine("base dir:");
		String incDirStr = Consoler.readLine("override/inc dir:");

		File baseDir = new File(baseDirStr);
		File incDir = new File(incDirStr);

		File dftSaveDir = new File(baseDir.getParent(), baseDir.getName() + "_+_" + incDir.getName());
		String dirSaveStr = Consoler.readLine("saveDir:", dftSaveDir.getCanonicalPath());
		File dirSave = new File(dirSaveStr);

		merge(baseDir, incDir, dirSave);
	}

}
