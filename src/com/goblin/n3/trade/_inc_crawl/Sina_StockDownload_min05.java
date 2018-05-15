package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.log.LogHelper;

public class Sina_StockDownload_min05 extends StockDownloadItfImpl {
	static final String pattern = "http://money.finance.sina.com.cn/quotes_service/api/jsonp_v2.php/var%20_{code}_5_{ms}=/CN_MarketData.getKLineData?symbol={code}&scale=5&ma=no&datalen=1023";
	static final LogHelper log = new LogHelper("StockDownload_min05_sina");
	private final Sina_Min05Parser parser = new Sina_Min05Parser();

	public Sina_StockDownload_min05() {
		super("sina_min05");
	}

	@Override
	public URL getUrl(String code) throws IOException {
		String prf;
		if (AllStock.instance.isSh(code)) {
			prf = "sh";
		} else {
			prf = "sz";
		}
		if (AllStock.sh.equals(code)) {
			code = AllStock.sh_alia.getCode();
		}
		String s = pattern.replace("{code}", prf + code).replace("{ms}", "" + (System.currentTimeMillis() - 10));
		System.out.println(s);
		return new URL(s);
	}

	@Override
	public Sina_Min05Parser getParser() {
		return parser;
	}

	public static void main(String[] args) throws Exception {
		String code = "1A0001";
		// Stock stk = AllStock.getStockByCode("603988");
		Sina_StockDownload_min05 sina = new Sina_StockDownload_min05();
		List<MinDay> ret = sina.crawl(code);

		ForEach.asc(ret, (x, y) -> {
			System.out.println(x.getStartTimeStr() + " \t" + x.getEndTimeStr() + "\t" + x);
		});
		// System.out.println(ret);
		// System.out.println(ret.get(ret.size() - 1));
		// System.out.println(ret.get(ret.size() - 1).getEndTimeStr());
	}
}
