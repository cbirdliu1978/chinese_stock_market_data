package com.goblin.n3.trade._inc_crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.ForEach;
import com.bmtech.utils.Misc;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class SinaDay {

	public static class Maped {
		private List<Map<String, String>> days;
		private Stock stk;

		public List<Map<String, String>> getDays() {
			return days;
		}

		public Maped(Stock stk, List<Map<String, String>> days) {
			this.days = days;
			this.stk = stk;
		}

		public List<MinDay> toMinDay() {
			List<MinDay> ret = new ArrayList<>();
			for (Map<String, String> map : days) {
				ret.add(map(map));
			}
			return ret;
		}

		private MinDay map(Map<String, String> map) {
			String daySgtr = map.get("day");
			int day = Integer.parseInt(daySgtr.replace("-", ""));

			MinDay mday = new MinDay();
			mday.setDay(day);
			mday.setCode(stk);
			mday.setStartTime(Statics.startMarketTime);
			mday.setEndTime(Statics.endMarketTime);
			mday.setOpen(Double.parseDouble(map.get("open")));
			mday.setHigh(Double.parseDouble(map.get("high")));
			mday.setLow(Double.parseDouble(map.get("low")));
			mday.setClose(Double.parseDouble(map.get("close")));
			mday.setVolumn(Long.parseLong(map.get("volume")));
			return mday;
		}
	}

	public Maped parse(Stock stk, String txt) throws JsonParseException, JsonMappingException, IOException {

		txt = Misc.substring(txt, "(", ")");

		txt = txt.replaceAll("([a-z]+)\\:", "\"$1\":");
		@SuppressWarnings("unchecked")
		List<Map<String, String>> lst = Misc.parseJson(txt, List.class);
		return new Maped(stk, lst);
	}

	// txt = txt.replaceAll("([a-z0-9]+)\\:", "\"$1:\"");
	public static void main(String[] args) throws Exception, ScriptException {
		// String txt = HttpCrawler.getString(
		// "http://money.finance.sina.com.cn/quotes_service/api/jsonp_v2.php/var%20_sh600637_5_1491397000635=/CN_MarketData.getKLineData?symbol=sh600637&scale=5&ma=no&datalen=1023");
		// System.out.println(txt);
		// txt = Misc.substring(txt, "(", ")");
		//
		// txt = txt.replaceAll("([a-z]+)\\:", "\"$1\":");
		// JjsEmulate.emulate("txt", txt);
		SinaDay m = new SinaDay();

		String xx = "var _sz000596_240_1506345189243=([{day:\"2017-05-19\",open:\"49.250\",high:\"50.200\",low:\"48.840\",close:\"49.160\",volume:\"1498337\"},{day:\"2017-05-22\",open:\"49.250\",high:\"49.410\",low:\"48.100\",close:\"48.470\",volume:\"1543309\"},{day:\"2017-05-23\",open:\"48.110\",high:\"50.430\",low:\"48.110\",close:\"49.230\",volume:\"3020932\"},{day:\"2017-05-24\",open:\"49.180\",high:\"49.180\",low:\"46.090\",close:\"48.580\",volume:\"1561120\"},{day:\"2017-05-25\",open:\"48.580\",high:\"48.580\",low:\"46.910\",close:\"47.390\",volume:\"3478959\"},{day:\"2017-05-26\",open:\"47.840\",high:\"48.500\",low:\"47.700\",close:\"47.890\",volume:\"2808025\"},{day:\"2017-05-31\",open:\"47.890\",high:\"48.390\",low:\"46.800\",close:\"47.400\",volume:\"2704101\"},{day:\"2017-06-01\",open:\"47.000\",high:\"47.680\",low:\"46.990\",close:\"47.210\",volume:\"1656721\"},{day:\"2017-06-02\",open:\"47.210\",high:\"47.570\",low:\"46.290\",close:\"46.970\",volume:\"1294257\"},{day:\"2017-06-05\",open:\"46.970\",high:\"47.200\",low:\"45.970\",close:\"46.150\",volume:\"2570270\"},{day:\"2017-06-06\",open:\"46.300\",high:\"47.360\",low:\"46.000\",close:\"47.320\",volume:\"1568227\"},{day:\"2017-06-07\",open:\"47.320\",high:\"50.800\",low:\"47.100\",close:\"50.220\",volume:\"4292634\"},{day:\"2017-06-08\",open:\"50.070\",high:\"51.500\",low:\"50.070\",close:\"50.650\",volume:\"2928589\"},{day:\"2017-06-09\",open:\"51.050\",high:\"52.460\",low:\"50.540\",close:\"50.690\",volume:\"3699412\"},{day:\"2017-06-12\",open:\"50.940\",high:\"52.800\",low:\"50.640\",close:\"51.520\",volume:\"3400049\"},{day:\"2017-06-13\",open:\"51.400\",high:\"52.100\",low:\"50.430\",close:\"51.500\",volume:\"1611176\"},{day:\"2017-06-14\",open:\"51.310\",high:\"52.490\",low:\"50.660\",close:\"51.150\",volume:\"2101882\"},{day:\"2017-06-15\",open:\"51.500\",high:\"51.500\",low:\"49.170\",close:\"50.300\",volume:\"2372724\"},{day:\"2017-06-16\",open:\"49.830\",high:\"50.200\",low:\"49.200\",close:\"49.420\",volume:\"2042827\"},{day:\"2017-06-19\",open:\"49.630\",high:\"50.100\",low:\"49.300\",close:\"49.880\",volume:\"2331590\"},{day:\"2017-06-20\",open:\"50.000\",high:\"50.170\",low:\"49.060\",close:\"49.420\",volume:\"2000811\"},{day:\"2017-06-21\",open:\"49.520\",high:\"50.900\",low:\"49.200\",close:\"50.900\",volume:\"2839171\"},{day:\"2017-06-22\",open:\"50.830\",high:\"50.830\",low:\"49.980\",close:\"50.000\",volume:\"2928875\"},{day:\"2017-06-23\",open:\"49.990\",high:\"50.390\",low:\"49.330\",close:\"50.330\",volume:\"1827177\"},{day:\"2017-06-26\",open:\"50.190\",high:\"52.550\",low:\"50.190\",close:\"52.400\",volume:\"4638725\"},{day:\"2017-06-27\",open:\"52.300\",high:\"52.780\",low:\"51.450\",close:\"52.250\",volume:\"2585069\"},{day:\"2017-06-28\",open:\"52.330\",high:\"52.480\",low:\"51.180\",close:\"51.560\",volume:\"2578112\"},{day:\"2017-06-29\",open:\"51.010\",high:\"51.360\",low:\"50.700\",close:\"51.190\",volume:\"1350931\"},{day:\"2017-06-30\",open:\"50.810\",high:\"51.270\",low:\"50.600\",close:\"50.950\",volume:\"1179708\"},{day:\"2017-07-03\",open:\"50.920\",high:\"51.180\",low:\"49.620\",close:\"49.900\",volume:\"1928291\"},{day:\"2017-07-04\",open:\"49.880\",high:\"50.070\",low:\"48.210\",close:\"48.970\",volume:\"2843496\"},{day:\"2017-07-05\",open:\"49.040\",high:\"49.450\",low:\"48.670\",close:\"49.280\",volume:\"2105631\"},{day:\"2017-07-06\",open:\"49.250\",high:\"49.370\",low:\"48.480\",close:\"48.600\",volume:\"2806941\"},{day:\"2017-07-07\",open:\"48.700\",high:\"48.800\",low:\"47.460\",close:\"48.000\",volume:\"3036377\"},{day:\"2017-07-10\",open:\"47.850\",high:\"48.560\",low:\"47.750\",close:\"48.410\",volume:\"1522219\"},{day:\"2017-07-11\",open:\"48.430\",high:\"49.180\",low:\"48.410\",close:\"48.500\",volume:\"2106724\"},{day:\"2017-07-12\",open:\"48.500\",high:\"48.800\",low:\"47.860\",close:\"48.400\",volume:\"1315215\"},{day:\"2017-07-13\",open:\"48.400\",high:\"49.150\",low:\"47.920\",close:\"48.430\",volume:\"1538828\"},{day:\"2017-07-14\",open:\"48.420\",high:\"48.780\",low:\"48.100\",close:\"48.720\",volume:\"1774848\"},{day:\"2017-07-17\",open:\"48.780\",high:\"48.980\",low:\"46.720\",close:\"46.990\",volume:\"2952985\"},{day:\"2017-07-18\",open:\"46.990\",high:\"47.340\",low:\"45.600\",close:\"47.070\",volume:\"2338733\"},{day:\"2017-07-19\",open:\"47.300\",high:\"48.100\",low:\"46.850\",close:\"47.620\",volume:\"3157925\"},{day:\"2017-07-20\",open:\"47.680\",high:\"48.200\",low:\"47.220\",close:\"48.000\",volume:\"2666692\"},{day:\"2017-07-21\",open:\"48.000\",high:\"49.350\",low:\"47.700\",close:\"48.830\",volume:\"3219278\"},{day:\"2017-07-24\",open:\"48.690\",high:\"50.410\",low:\"48.600\",close:\"49.060\",volume:\"3634290\"},{day:\"2017-07-25\",open:\"49.230\",high:\"49.290\",low:\"48.260\",close:\"48.310\",volume:\"1797721\"},{day:\"2017-07-26\",open:\"48.050\",high:\"49.030\",low:\"47.270\",close:\"47.520\",volume:\"2595595\"},{day:\"2017-07-27\",open:\"47.530\",high:\"48.760\",low:\"47.270\",close:\"48.710\",volume:\"3046159\"},{day:\"2017-07-28\",open:\"48.540\",high:\"53.580\",low:\"48.260\",close:\"53.300\",volume:\"9323186\"},{day:\"2017-07-31\",open:\"53.390\",high:\"55.850\",low:\"53.030\",close:\"53.800\",volume:\"8504257\"},{day:\"2017-08-01\",open:\"53.790\",high:\"55.080\",low:\"53.500\",close:\"54.070\",volume:\"4259198\"},{day:\"2017-08-02\",open:\"53.960\",high:\"54.360\",low:\"52.530\",close:\"52.990\",volume:\"3913321\"},{day:\"2017-08-03\",open:\"52.760\",high:\"54.200\",low:\"51.550\",close:\"51.910\",volume:\"3902399\"},{day:\"2017-08-04\",open:\"51.790\",high:\"52.320\",low:\"51.000\",close:\"51.240\",volume:\"2418730\"},{day:\"2017-08-07\",open:\"51.050\",high:\"54.000\",low:\"50.630\",close:\"53.680\",volume:\"4266641\"},{day:\"2017-08-08\",open:\"53.430\",high:\"54.360\",low:\"51.330\",close:\"51.700\",volume:\"6443720\"},{day:\"2017-08-09\",open:\"51.340\",high:\"54.580\",low:\"51.340\",close:\"54.050\",volume:\"5605096\"},{day:\"2017-08-10\",open:\"54.050\",high:\"56.800\",low:\"53.860\",close:\"54.980\",volume:\"6781402\"},{day:\"2017-08-11\",open:\"54.500\",high:\"55.800\",low:\"53.850\",close:\"54.190\",volume:\"3442541\"},{day:\"2017-08-14\",open:\"54.000\",high:\"56.700\",low:\"53.540\",close:\"54.930\",volume:\"4772173\"},{day:\"2017-08-15\",open:\"54.230\",high:\"54.780\",low:\"53.010\",close:\"53.550\",volume:\"4095242\"},{day:\"2017-08-16\",open:\"53.400\",high:\"53.940\",low:\"52.320\",close:\"52.850\",volume:\"3201369\"},{day:\"2017-08-17\",open:\"53.300\",high:\"53.300\",low:\"52.050\",close:\"52.580\",volume:\"2856143\"},{day:\"2017-08-18\",open:\"52.600\",high:\"52.970\",low:\"51.860\",close:\"52.500\",volume:\"2695987\"},{day:\"2017-08-21\",open:\"52.630\",high:\"52.800\",low:\"51.500\",close:\"52.160\",volume:\"2926275\"},{day:\"2017-08-22\",open:\"51.930\",high:\"51.930\",low:\"50.160\",close:\"50.940\",volume:\"4208126\"},{day:\"2017-08-23\",open:\"50.900\",high:\"51.490\",low:\"50.600\",close:\"51.350\",volume:\"2444430\"},{day:\"2017-08-24\",open:\"51.210\",high:\"51.390\",low:\"50.380\",close:\"50.490\",volume:\"2366578\"},{day:\"2017-08-25\",open:\"50.610\",high:\"51.850\",low:\"50.610\",close:\"51.620\",volume:\"2277935\"},{day:\"2017-08-28\",open:\"53.000\",high:\"53.500\",low:\"52.000\",close:\"52.100\",volume:\"8488433\"},{day:\"2017-08-29\",open:\"51.980\",high:\"52.850\",low:\"51.520\",close:\"52.660\",volume:\"3511315\"},{day:\"2017-08-30\",open:\"52.460\",high:\"52.580\",low:\"51.410\",close:\"52.310\",volume:\"3499177\"},{day:\"2017-08-31\",open:\"52.310\",high:\"52.580\",low:\"51.120\",close:\"51.800\",volume:\"2862896\"},{day:\"2017-09-01\",open:\"51.610\",high:\"51.940\",low:\"51.400\",close:\"51.640\",volume:\"2072228\"},{day:\"2017-09-04\",open:\"51.500\",high:\"52.000\",low:\"50.680\",close:\"52.000\",volume:\"3590530\"},{day:\"2017-09-05\",open:\"51.620\",high:\"52.000\",low:\"51.510\",close:\"51.660\",volume:\"1506427\"},{day:\"2017-09-06\",open:\"51.660\",high:\"51.700\",low:\"50.940\",close:\"51.070\",volume:\"1497564\"},{day:\"2017-09-07\",open:\"51.070\",high:\"51.100\",low:\"49.880\",close:\"50.050\",volume:\"3573002\"},{day:\"2017-09-08\",open:\"49.800\",high:\"49.990\",low:\"49.080\",close:\"49.440\",volume:\"2307508\"},{day:\"2017-09-11\",open:\"49.380\",high:\"51.180\",low:\"49.090\",close:\"51.100\",volume:\"4013590\"},{day:\"2017-09-12\",open:\"51.000\",high:\"52.350\",low:\"50.490\",close:\"52.350\",volume:\"4006521\"},{day:\"2017-09-13\",open:\"52.060\",high:\"53.920\",low:\"51.930\",close:\"52.560\",volume:\"4879686\"},{day:\"2017-09-14\",open:\"52.420\",high:\"53.080\",low:\"51.670\",close:\"51.970\",volume:\"2865489\"},{day:\"2017-09-15\",open:\"51.800\",high:\"52.270\",low:\"51.000\",close:\"51.330\",volume:\"2344765\"},{day:\"2017-09-18\",open:\"51.040\",high:\"56.460\",low:\"51.040\",close:\"56.460\",volume:\"10035021\"},{day:\"2017-09-19\",open:\"57.000\",high:\"57.500\",low:\"55.510\",close:\"56.980\",volume:\"9250822\"},{day:\"2017-09-20\",open:\"56.630\",high:\"60.390\",low:\"56.630\",close:\"57.490\",volume:\"10626088\"},{day:\"2017-09-21\",open:\"57.990\",high:\"58.780\",low:\"56.510\",close:\"57.600\",volume:\"5466693\"},{day:\"2017-09-22\",open:\"57.600\",high:\"58.550\",low:\"55.870\",close:\"57.830\",volume:\"5573850\"},{day:\"2017-09-25\",open:\"57.500\",high:\"58.490\",low:\"56.990\",close:\"58.000\",volume:\"4391010\"}])";

		Maped l = m.parse(AllStock.getStockByCode("000596"), xx);
		List<MinDay> ret = l.toMinDay();
		ForEach.asc(ret, (mx, mI) -> {
			System.out.println(mx);
		});
		// JjsEmulate.emulate("l", l);
	}
}
