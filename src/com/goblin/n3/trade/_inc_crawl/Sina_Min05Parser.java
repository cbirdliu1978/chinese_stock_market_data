package com.goblin.n3.trade._inc_crawl;

import java.util.ArrayList;
import java.util.List;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.log.L;
import com.bmtech.utils.log.LogHelper;
import com.goblin.n3.trade._inc_crawl.Sina05min.Maped;

public class Sina_Min05Parser extends Parser4Min05 {

	private LogHelper log = new LogHelper("sinaMin05Parser");

	private List<MinDay> insertMissing(List<MinDay> list) {
		List<MinDay> ret = new ArrayList<>();
		MinDay last = null;
		for (int x = 0; x < list.size(); x++) {
			MinDay feed = list.get(x);
			try {
				if (last != null) {
					if (feed.getStartTime() - last.getEndTime() != 0 && feed.getDay() == last.getDay()) {
						if (feed.getStartTime() == Statics.m13_00 && last.getEndTime() == Statics.m11_30) {
							// no need inject
						} else {
							List<MinDay> inject = inject(last, feed);
							ret.addAll(inject);
						}
					}
					if (feed.getDay() > last.getDay()) {
						// for end
						if (last.getEndTime() != Statics.endMarketTime) {
							// Consoler.readInt("??" + feed + " " + last);
							MinDay insert = last.copy();
							double prc = last.getClose();
							insert.setHigh(prc);
							insert.setLow(prc);
							insert.setOpen(prc);
							insert.setClose(prc);

							insert.setStartTime(Statics.m14_55);
							insert.setEndTime(Statics.m15_00);

							List<MinDay> inject = inject(last, insert);
							inject.add(insert);

							ret.addAll(inject);
						}

						// for start market
						if (feed.getStartTime() != Statics.startMarketTime) {
							// Consoler.readInt("??" + feed + " " + last);
							MinDay insert = last.copy();
							double prc = last.getClose();

							insert.setHigh(prc);
							insert.setLow(prc);
							insert.setOpen(prc);
							insert.setClose(prc);

							insert.setDay(feed.getDay());
							insert.setStartTime(Statics.m09_30);
							insert.setEndTime(Statics.m09_35);

							List<MinDay> inject = inject(insert, feed);

							ret.add(insert);
							ret.addAll(inject);
						}
					}

				}

				// FIXME check validate day by day
			} finally {
				ret.add(feed);
				last = feed;
			}
		}
		return ret;
	}

	private List<MinDay> inject(MinDay lastData, MinDay feedData) {
		List<MinDay> ret = new ArrayList<MinDay>();
		double margin = Math.abs(lastData.getClose() - feedData.getOpen()) / lastData.getClose();
		if (margin <= 0.21) {
			MinDay startOne = lastData;
			while (startOne.getEndTime() < feedData.getStartTime()) {
				int nextStartTime = nextStartTime(startOne.getEndTime());
				int nextEndTime = nextStartTime + Statics.min05BarMs;
				if (nextEndTime > feedData.getStartTime()) {
					// set last close as feed's open
					// if (ret.size() > 0) {
					// ret.get(ret.size() - 1).setClose(feedData.getOpen());
					// }
					break;
				}
				MinDay insert = startOne.copy();
				double prc = startOne.getClose();
				insert.setHigh(prc);
				insert.setLow(prc);
				insert.setOpen(prc);
				insert.setClose(prc);

				insert.setStartTime(nextStartTime);
				insert.setEndTime(nextEndTime);
				ret.add(insert);

				startOne = insert;
			}
		} else {
			log.error("can not inject data! too much margin %s, for %s with %s", margin, lastData, feedData);
		}
		L.w("inject %s from %s to %s", ret.size(), lastData, feedData);
		return ret;
	}

	private static int nextStartTime(int nextEndTime) {
		if (nextEndTime == Statics.m11_30) {
			return Statics.m13_00;
		}
		return nextEndTime;
	}

	@Override
	protected List<MinDay> parseMin05Inner(String code, String html) throws Exception {
		Sina05min min05 = new Sina05min();
		Maped m = min05.parse(code, html);
		List<MinDay> list = m.toMinDay();
		list = insertMissing(list);
		int startIndex = 0;
		for (; startIndex < list.size(); startIndex++) {
			if (list.get(startIndex).getStartTime() == Statics.startMarketTime)
				break;
		}

		return list.subList(startIndex, list.size());
	}

}
