package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.log.LogHelper;

public class DFCF_StockDownload_min05 extends StockDownloadItfImpl {
	static final String pattern = "http://pdfm2.eastmoney.com/EM_UBG_PDTI_Fast/api/js?id={code}&TYPE=m5k&js=fsData{ms}((x))&rtntype=5&isCR=false&authorityType=fa&fsData{ms}=fsData{ms}#";
	static final LogHelper log = new LogHelper("StockDownload_min05_sina");
	private DFCF_Min05Parser parser = new DFCF_Min05Parser();

	public DFCF_StockDownload_min05() {
		super("dfcf_min05");
	}

	@Override
	public URL getUrl(String code) throws IOException {
		String prf;

		if (AllStock.instance.isSh(code)) {
			prf = "1";
		} else {
			prf = "2";
		}
		if (AllStock.sh.equals(code)) {
			code = AllStock.sh_alia.getCode();
		}
		String s = pattern.replace("{code}", code + prf).replace("{ms}", "" + (System.currentTimeMillis() - 5000));
		System.out.println(s);
		return new URL(s);
	}

	@Override
	public Parser4Min05 getParser() {
		return parser;
	}

	public static void main(String[] args) throws Exception {
		// Stock stk = AllStock.getStockByCode("603988");
		String code = "1A0001";
		DFCF_StockDownload_min05 crawler = new DFCF_StockDownload_min05();
		List<MinDay> ret = crawler.crawl(code);

		ForEach.asc(ret, (x, y) -> {
			System.out.println(x.getStartTimeStr() + " \t" + x.getEndTimeStr() + "\t" + x);
		});
		// System.out.println(ret);
		// System.out.println(ret.get(ret.size() - 1));
		// System.out.println(ret.get(ret.size() - 1).getEndTimeStr());
	}

}
