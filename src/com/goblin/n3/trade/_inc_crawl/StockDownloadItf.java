package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.http.HttpHandler;

public interface StockDownloadItf {

	public URL getUrl(String code) throws IOException;

	public int minSize();

	public StockParser getParser();

	public List<MinDay> crawl(String code, HttpHandler hdl) throws Exception;

}
