package com.goblin.n3.trade._inc_crawl.ext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Consoler;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.L;
import com.goblin.n3.trade._inc_crawl.DFCF_Min05Parser;
import com.goblin.n3.trade._inc_crawl.Parser4Min05;
import com.goblin.n3.trade._inc_crawl.Sina_Min05Parser;

public class DoMDirMin05List {

	private File dir, saveToDir;//
	private DoMDirMin05 worker;

	public DoMDirMin05List(File dir, Parser4Min05 parser, File saveToDir) {
		this.dir = dir;

		worker = new DoMDirMin05(parser);
		this.saveToDir = saveToDir;
	}

	public void extract() throws IOException {
		Misc.besureDirExists(this.saveToDir);
		File[] fs = dir.listFiles();
		Arrays.sort(fs, (a, b) -> {
			return a.getName().compareTo(b.getName());
		});
		for (File mdir : fs) {
			if (!mdir.isDirectory()) {
				L.f("bad dir, not directory %s", mdir);
				continue;
			}
			try {
				worker.loadToMap(mdir);
			} catch (Exception e) {
				L.f("error when extract from mdir %s", mdir);
			}
		}

		L.f("do all list");
		Map<String, List<MinDay>> map = worker.doAllList();
		L.f("saving to %s ", saveToDir);
		ForEach.map(map, (key, value) -> {
			LineWriter lw = new LineWriter(new File(this.saveToDir, key + ".mn5"));
			ForEach.asc(value, (line) -> {
				lw.writeLine(line.toLine());
			});
			lw.close();
		});

	}

	public static void extract(String name, File base, File saveTo) throws IOException {
		Parser4Min05 parser = null;
		if (name.equals("dfcfInc")) {
			parser = new DFCF_Min05Parser();
		} else if (name.equals("sinaInc")) {
			parser = new Sina_Min05Parser();
		} else {
			Misc.throwNewRuntimeException("unknown parser for %s", name);
		}
		L.f("extracting %s, save to %s", base, saveTo);

		DoMDirMin05List dm = new DoMDirMin05List(base, parser, saveTo);
		dm.extract();

		L.f("saved to %s", saveTo);
	}

	public static File extract(File base) throws IOException {

		String from = base.getParentFile().getName();
		File saveTo = Misc.besureDirExists("/data/datamine/ext-" + base.getParentFile().getName() + "." + Misc.timeStr() + "/min5/");

		extract(from, base, saveTo);
		return saveTo;

	}

	public static File extract(String name) throws IOException {
		name = name.trim();
		File base;
		if (name.contains(":\\") || name.contains(":/") || name.startsWith("/")) {
			base = new File(name + "/min5/");
		} else {
			base = new File(new File("/data/datamine/"), name + "/min5/");
		}

		return extract(base);

	}

	public static void main(String[] args) throws IOException {
		String name;
		if (args.length == 0) {
			name = Consoler.readLine("dirName");
		} else {
			name = args[0];
		}
		extract(name);
	}
}
