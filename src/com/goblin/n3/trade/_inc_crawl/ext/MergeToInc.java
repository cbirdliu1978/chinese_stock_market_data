package com.goblin.n3.trade._inc_crawl.ext;

import java.io.File;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.inc.IncDataIO;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.log.L;

public class MergeToInc {
	public static void mergeToInc(File dir) throws Exception {
		File[] fs = dir.listFiles();
		ForEach.asc(fs, (f, fI) -> {
			if (f.getName().startsWith(".") || f.isDirectory() || !f.getName().endsWith("mn5")) {
				L.f("skip not regular stock file %s", f);
				return;
			}
			if (f.length() == 0) {
				L.f("bad! skip empty file %s", f);
				return;
			}

			String code = Misc.substring(f.getName(), null, ".");
			try {
				mergeToInc(code, f);
			} catch (Exception e) {
				e.printStackTrace();
				L.f("when mergeInc %s from file %s", code, f);
			}
		});
	}

	private static void mergeToInc(String code, File f) throws Exception {
		DataType dataType = DataType.min05;
		Stock stk = AllStock.getStockByCode(code);
		List<MinDay> mdayList = MinDay.readMinFile(f, dataType.orgBarRangeSeconds(), stk);
		IncDataIO io = new IncDataIO(dataType, stk);
		io.merge(mdayList);
		io.trim2FinishedList();
		io.setCanWrite();
		io.save();

	}

	public static void main(String... args) throws Exception {
		String baseDirStr;
		if (args.length == 0) {
			baseDirStr = Consoler.readLine("toIncFolder");
		} else {
			baseDirStr = args[0];
		}
		File dir = new File(baseDirStr);
		if (!dir.exists()) {
			Misc.throwNewRuntimeException("not exist file %s", dir);
		}

		if (!dir.isDirectory()) {
			Misc.throwNewRuntimeException("not isDirectory file %s", dir);
		}
		mergeToInc(dir);
	}
}
