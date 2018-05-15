package com.goblin.n3.trade._inc_crawl;

import java.util.List;

import com.bmtech.datamine.data.mday.MinDay;

public interface StockParser {

	public List<MinDay> parse(String code, String html) throws Exception;

	public void check(String code, List<MinDay> list) throws Exception;
}
