package com.goblin.n3.trade._inc_crawl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.stockcodefilters.All_Filter;
import com.bmtech.datamine.data.stockcodefilters.StockCodeFilter;
import com.bmtech.utils.Charsets;
import com.bmtech.utils.Runnor;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.http.HttpHandler;
import com.bmtech.utils.restoreable.RestoreableExecute;
import com.bmtech.utils.restoreable.RetriableItrCheck;

public abstract class StockCrawler {
	private StockCodeFilter flt;
	private RetriableItrCheck itr;
	private RestoreableExecute exe;
	protected StockDownloadItf downloader;

	public StockCrawler(StockDownloadItf downloader) {
		this(new All_Filter());
		this.downloader = downloader;
	}

	public StockCrawler(StockCodeFilter flt) {
		this.flt = flt;
	}

	private List<String> getCodes() {
		Iterator<Stock> itr = AllStock.instance.getStocks(flt);
		List<String> ret = new ArrayList<>();
		while (itr.hasNext()) {
			Stock stk = itr.next();
			ret.add(stk.getCode());
		}
		Collections.shuffle(ret);
		return ret;
	}

	protected String byte2String(byte[] bs) {
		return new String(bs, Charsets.UTF8_CS);
	}

	public RetriableItrCheck getItr() {
		return itr;
	}

	public RestoreableExecute getExe() {
		return exe;
	}

	protected HttpHandler getHttpCrawler() {
		return HttpHandler.getCrawlHandler(true);
	}

	public RestoreableExecute createRestoreableExecute() {
		List<String> codes = getCodes();
		Collections.shuffle(codes);
		itr = new RetriableItrCheck(codes);
		itr.setMaxRoundNum(5);

		exe = RetriableItrCheck.getExecutor(itr, (code) -> {
			return new Runnor() {
				@Override
				public void run() throws Exception {
					URL url = downloader.getUrl(code);
					byte[] bs = HttpCrawler.getBytes(url, getHttpCrawler());
					String txt = byte2String(bs);
					if (txt.length() >= downloader.minSize())
						consume(code, url, txt);
					else
						throw new RuntimeException("too small datas:{" + txt + "} for url " + url);
				}
			};
		});

		return exe;
	}

	public abstract void consume(String code, URL url, String html) throws Exception;
	// {
	// Stock stk = AllStock.getStockByCode(code);
	// List<MinDay> listOrg = downloader.parse(stk, html);
	// List<MinDay> list = NetDownMinDayVerifier.verify(listOrg, stk, getDataType());
	// if (list.size() == 0) {
	// L.iFatal("fail! size is 0 for stock %s, url %s,orgList %s", code, url, listOrg);
	// return;
	// }
	// consumeAfterVerify(stk, list);
	// }

	// public abstract void consumeAfterVerify(Stock stk, List<MinDay> list) throws Exception;

	public abstract DataType getDataType();

	public void run() {
		beuserExeCreated();
		this.exe.execute();
	}

	public void setThreadNum(int threadNum) {
		beuserExeCreated();
		this.exe.setThreadNum(threadNum);
	}

	private void beuserExeCreated() {
		if (this.exe == null)
			this.createRestoreableExecute();
	}

	public void setMaxRoundNum(int maxRoundNum) {
		this.beuserExeCreated();
		this.itr.setMaxRoundNum(maxRoundNum);
	}

}
