package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.net.URL;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Charsets;
import com.bmtech.utils.Misc;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.http.HttpHandler;
import com.bmtech.utils.log.L;

public class Sina_StockDownload_current {
	static final String pattern = "http://hq.sinajs.cn/etag.php?_={ms}&list={code}";

	public Sina_StockDownload_current() {
		super();
	}

	public URL getUrl(String code) throws IOException {
		String codePrf;
		if (code.startsWith("60")) {
			codePrf = "sh" + code;
		} else {
			codePrf = "sz" + code;
		}
		String s = pattern.replace("{code}", codePrf).replace("{ms}", "" + (System.currentTimeMillis() - 10));
		return new URL(s);
	}

	public MinDay crawl(Stock stk) throws Exception {
		return crawl(stk, HttpHandler.getCrawlHandler());
	}

	public MinDay crawl(Stock stk, HttpHandler hdl) throws Exception {
		HttpCrawler crl = HttpCrawler.makeCrawler(this.getUrl(stk.getCode()), hdl);
		String html = crl.getString(Charsets.UTF8_CS);
		SinaCurrent crt = new SinaCurrent(stk, html);
		return crt.toMinDay();
	}

	public MinDay parse(Stock stk, String html) throws Exception {
		SinaCurrent crt = new SinaCurrent(stk, html);
		return crt.toMinDay();
	}

	public static void main(String[] args) throws Exception {
		Stock stk = AllStock.getStockByCode("600165");
		Misc.startDaemonLoopThread(() -> {
			Sina_StockDownload_current sina = new Sina_StockDownload_current();
			MinDay ret = sina.crawl(stk);

			L.f(ret.getStartTimeStr() + " \t" + ret.getEndTimeStr() + "\t" + ret);
			Misc.sleep(3000);
		});
		Misc.block();
	}

}
