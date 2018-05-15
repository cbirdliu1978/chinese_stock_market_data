package com.goblin.n3.trade._inc_crawl.ext;

import java.io.File;
import java.io.IOException;

import com.bmtech.utils.Misc;
import com.bmtech.utils.log.L;

public class ExtractAndMerge {

	public static void main(String[] args) throws Exception {

		File sinaMdir = new File("/data/datamine/sinaInc/min5/");
		File dfcfMdir = new File("/data/datamine/dfcfInc/min5/");

		File sinaExtTo = DoMDirMin05List.extract(sinaMdir);
		System.gc();
		System.gc();

		File dfcfExtTo = DoMDirMin05List.extract(dfcfMdir);
		System.gc();
		System.gc();

		File mergeToDir = new File("/data/datamine/ext-merge/min5/");
		Misc.besureDirExists(mergeToDir);

		MergeTo.merge(sinaExtTo, dfcfExtTo, mergeToDir);

		bak(sinaMdir);
		bak(dfcfMdir);
	}

	private static void bak(File mdir) throws IOException {

		File bakDir = new File("/data/datamine/_parsed_download_mdir_bak/" + mdir.getParentFile().getName() + "/" + Misc.timeStr());
		Misc.besureDirExists(bakDir);

		File[] fs = mdir.listFiles();
		for (File toBak : fs) {
			String name = toBak.getName();
			File bakTo = new File(bakDir, name);

			int parsedNameInt = Misc.parseInt(name, -1);
			if (parsedNameInt == -1) {
				L.f("skip bad name dir %s", toBak);
				continue;
			}

			int today = Misc.nowDay();
			if (parsedNameInt == today) {
				L.f("not bak today's dir %s", toBak);
				continue;
			}

			L.f("baking %s to %s", toBak, bakTo);
			boolean bakResult = toBak.renameTo(bakTo);

			L.f("%s bak %s to %s", bakResult ? "ok" : "fail", toBak, bakTo);
		}
	}

}
