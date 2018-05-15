package com.goblin.n3.trade._inc_crawl;

import java.io.File;
import java.net.URL;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.inc.IncDataIO;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Misc;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.bmfs.MDir;
import com.bmtech.utils.log.L;

public class Sina_StockOfflineDownloadMin05Crawler extends StockCrawler {

	public Sina_StockOfflineDownloadMin05Crawler() throws Exception {
		super(new Sina_StockDownload_min05());
		super.createRestoreableExecute();
	}

	private void incDataAdd(String code, List<MinDay> list) throws Exception {

		IncDataIO io = new IncDataIO(DataType.min05, AllStock.getStockByCode(code), true);
		while (list.size() > 0) {
			MinDay last = list.get(list.size() - 1);
			if (last.getEndTime() != Statics.m15_00) {
				list.remove(list.size() - 1);

			} else {
				break;
			}
		}
		io.merge(list);
		io.save(null);
	}

	@Override
	public DataType getDataType() {
		return DataType.min05;
	}

	@Override
	public final void consume(String code, URL url, String html) throws Exception {
		L.f("got %s for %s", html.length(), url);
		try {
			mdirRawFile.addFile(code + "." + Misc.timeStr() + ".gz", ZipUnzip.gzip(html.getBytes()));
		} catch (Exception e) {
			L.f(e, "add raw fila fail! , for code %s of url %s", code, url);
		}
		try {
			List<MinDay> list = this.downloader.getParser().parse(code, html);
			incDataAdd(code, list);
		} catch (Exception e) {
			L.f(e, "Inc add fail!!!!! for code %s of url %s", code, url);
		}
	}

	static MDir mdirRawFile;

	public static void main(String[] args) throws Exception {
		mdirRawFile = MDir.open4Write(new File("/data/datamine/sinaInc/min5/" + Misc.nowDay()));
		Sina_StockOfflineDownloadMin05Crawler crl = new Sina_StockOfflineDownloadMin05Crawler();
		crl.getExe().setWaitMsPerTaskForSingleThread(200);
		crl.setThreadNum(1);
		crl.run();
	}

}
