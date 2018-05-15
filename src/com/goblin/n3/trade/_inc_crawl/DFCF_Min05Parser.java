package com.goblin.n3.trade._inc_crawl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.log.L;
import com.bmtech.utils.var.VarLong;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;

public class DFCF_Min05Parser extends Parser4Min05 {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Parsed {
		String name;
		String code;
		List<String> data;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public List<String> getData() {
			return data;
		}

		public void setData(List<String> data) {
			this.data = data;
		}

	}

	TypeReference<Parsed> ref = new TypeReference<Parsed>() {

	};

	@Override
	protected List<MinDay> parseMin05Inner(String code, String html) throws Exception {
		int start = html.indexOf('(');
		int end = html.lastIndexOf(')');
		String json = html.substring(start + 1, end);
		List<MinDay> ret = new ArrayList<MinDay>();
		Parsed p = Misc.parseJson(json, ref);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		for (String line : p.data) {
			String[] tokens = line.split(",");
			long time = sdf.parse(tokens[0]).getTime();
			double open = Double.parseDouble(tokens[1].trim());
			double close = Double.parseDouble(tokens[2].trim());
			double low = Double.parseDouble(tokens[3].trim());
			double high = Double.parseDouble(tokens[4].trim());
			long volume = Long.parseLong(tokens[5].trim());

			MinDay mday = new MinDay();
			mday.updateTime(time - Statics.min05BarMs, time);
			mday.setClose(close);
			mday.setCode(p.code);
			mday.setHigh(high);
			mday.setLow(low);
			mday.setOpen(open);
			mday.setVolumn(volume * 100);
			ret.add(mday);
		}
		List<List<MinDay>> lst = Statics.toDayList(ret);
		List<MinDay> days = new ArrayList<>();
		ForEach.asc(lst, (a) -> {
			if (valid(a)) {
				days.addAll(a);
			} else {
				int size = a.size();
				boolean needPrint = false;
				for (int x = 1; x < a.size(); x++) {
					if (a.get(0).getClose() != a.get(x).getClose()) {
						needPrint = true;
					}
				}
				if (needPrint)
					L.f("skip day %s@%s, %s-%s vs %s-%s", code, a.get(0).getDay(), 1, a.get(0), size, a.get(size - 1));
			}
		});
		return days;
	}

	private boolean valid(List<MinDay> a) {
		VarLong sum = new VarLong();
		a.forEach((e) -> {
			sum.value += e.getVolumn();
		});
		return sum.value != 0;
	}

	public static void main(String[] args) throws Exception {
		String html = HttpCrawler.getString(
				"http://pdfm2.eastmoney.com/EM_UBG_PDTI_Fast/api/js?id=0000212&TYPE=m5k&js=fsData1518182483915((x))&rtntype=5&isCR=false&authorityType=fa&fsData1518182483915=fsData1518182483915#");

		DFCF_Min05Parser parser = new DFCF_Min05Parser();

		List<MinDay> ret = parser.parse("000021", html);

		ret.forEach((e) -> {
			System.out.println(e);
		});
	}
}
