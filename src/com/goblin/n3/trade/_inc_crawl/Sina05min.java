package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bmtech.datamine.Statics;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Misc;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Sina05min {

	public static class Maped {
		private List<Map<String, String>> min05s;

		public List<Map<String, String>> getMin05s() {
			return min05s;
		}

		private String code;

		public Maped(String code, List<Map<String, String>> min05s) {
			this.min05s = min05s;
			this.code = code;
		}

		public List<MinDay> toMinDay() {
			List<MinDay> ret = new ArrayList<>();
			for (Map<String, String> map : min05s) {
				ret.add(map(map));
			}
			return ret;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// {day=2017-03-27 14:55:00, open=22.950, high=22.970, low=22.950,
		// close=22.960, volume=119628}

		private MinDay map(Map<String, String> map) {
			String daySgtr = map.get("day");
			String[] tokens = daySgtr.split(" ");
			int day = Integer.parseInt(tokens[0].replace("-", ""));
			tokens = tokens[1].split(":");
			int hour = Integer.parseInt(tokens[0]);
			int minute = Integer.parseInt(tokens[1]);

			MinDay mday = new MinDay();
			mday.setDay(day);
			mday.setCode(code);
			mday.setStartTime(Statics.getTime(hour, minute - 5));
			mday.setEndTime(Statics.getTime(hour, minute));
			mday.setOpen(Double.parseDouble(map.get("open")));
			mday.setHigh(Double.parseDouble(map.get("high")));
			mday.setLow(Double.parseDouble(map.get("low")));
			mday.setClose(Double.parseDouble(map.get("close")));
			mday.setVolumn(Long.parseLong(map.get("volume")));
			return mday;
		}
	}

	public Maped parse(String code, String txt) throws JsonParseException, JsonMappingException, IOException {

		txt = Misc.substring(txt, "(", ")");

		txt = txt.replaceAll("([a-z]+)\\:", "\"$1\":");
		@SuppressWarnings("unchecked")
		List<Map<String, String>> lst = Misc.parseJson(txt, List.class);
		return new Maped(code, lst);
	}

	// txt = txt.replaceAll("([a-z0-9]+)\\:", "\"$1:\"");
}
