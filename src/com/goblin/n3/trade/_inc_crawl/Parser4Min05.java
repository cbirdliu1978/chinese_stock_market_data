package com.goblin.n3.trade._inc_crawl;

import java.util.List;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.log.LogHelper;

public abstract class Parser4Min05 implements StockParser {

	@Override
	public List<MinDay> parse(String code, String html) throws Exception {

		List<MinDay> lst = parseMin05Inner(code, html);
		if (lst.size() > 0)
			check(code, lst);
		return lst;
	}

	protected abstract List<MinDay> parseMin05Inner(String code, String html) throws Exception;

	@Override
	public void check(String code, List<MinDay> list) throws Exception {
		if (list.size() > 0) {
			int hour = Misc.nowHour();
			int minute = Misc.nowMinute();
			int nowDay = Misc.nowDay();
			MinDay mday = list.get(list.size() - 1);
			if (nowDay == mday.getDay()) {
				if ((hour >= 10 && hour <= 15) || (hour == 9 && minute >= 30)) {
					if ((mday.getCloseHour() == hour && mday.getCloseMinutes() > minute) || mday.getCloseHour() > hour) {
						LogHelper.iWarn("need remove " + mday.toString());
						// list.remove(list.size() - 1);
					}
				}
			}
		}
		// Stock stk = AllStock.getStockByCode(code);

		List<MinDay> listNew = NetDownMinDayVerifier.verify(list, AllStock.getStockByCode(code), DataType.min05);
		if (listNew.size() == 0) {
			Misc.throwNewRuntimeException("fail! size is 0 for stock %s,orgList %s", code, list);
		}
		List<List<MinDay>> lst = Statics.toDayList(list);
		ForEach.asc(lst, (day, dayI) -> {
			boolean isLastOne = dayI + 1 == lst.size();
			if (!isLastOne)
				Statics.checkMin05Day(day);
		});
	}
}
