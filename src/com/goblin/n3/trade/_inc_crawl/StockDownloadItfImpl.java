package com.goblin.n3.trade._inc_crawl;

import java.util.List;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Charsets;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.http.HttpHandler;
import com.bmtech.utils.log.LogHelper;

public abstract class StockDownloadItfImpl implements StockDownloadItf {
	protected final LogHelper log;

	public StockDownloadItfImpl(String taskName) {
		this.log = new LogHelper(taskName);
	}

	@Override
	public abstract Parser4Min05 getParser();

	@Override
	public int minSize() {
		return 1024;
	}

	public List<MinDay> crawl(String code) throws Exception {
		return crawl(code, HttpHandler.getCrawlHandler());
	}

	@Override
	public List<MinDay> crawl(String code, HttpHandler hdl) throws Exception {
		long t1 = System.currentTimeMillis();
		HttpCrawler crl = HttpCrawler.makeCrawler(this.getUrl(code), hdl);
		String html = crl.getString(Charsets.UTF8_CS);
		long t2 = System.currentTimeMillis();
		log.info("crawl use %s ms for code %s", t2 - t1, code);

		List<MinDay> ret = this.getParser().parse(code, html);
		long t3 = System.currentTimeMillis();
		log.info("parse use %s ms for code %s", t3 - t2, code);
		return ret;
	}

}
