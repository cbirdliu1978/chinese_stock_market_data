package com.goblin.n3.trade._inc_crawl;

import java.io.File;
import java.net.URL;

import com.bmtech.datamine.data.DataType;
import com.bmtech.utils.Misc;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.bmfs.MDir;
import com.bmtech.utils.log.L;

public class DFCF_StockOfflineDownloadMin05Crawler extends StockCrawler {

	public DFCF_StockOfflineDownloadMin05Crawler() throws Exception {
		super(new DFCF_StockDownload_min05());
		super.createRestoreableExecute();
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
			e.printStackTrace();
		}

		try {
			downloader.getParser().parse(code, html);
		} catch (Exception e) {
			L.f(e, "when parse " + url);
		}
	}

	static MDir mdirRawFile;

	public static void main(String[] args) throws Exception {
		// mdirFile = MDir.open4Write(new File("/data/datamine/sinaInc.min5"));
		mdirRawFile = MDir.open4Write(new File("/data/datamine/dfcfInc/min5/" + Misc.nowDay()));
		DFCF_StockOfflineDownloadMin05Crawler crl = new DFCF_StockOfflineDownloadMin05Crawler();
		crl.getExe().setWaitMsPerTaskForSingleThread(200);
		crl.setThreadNum(1);
		crl.run();
	}

}
