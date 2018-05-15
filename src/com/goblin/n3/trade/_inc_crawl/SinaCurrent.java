package com.goblin.n3.trade._inc_crawl;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import com.bmtech.datamine.AllStock;
import com.bmtech.datamine.Statics;
import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.mday.MinDay;
import com.bmtech.utils.Misc;
import com.bmtech.utils.StringTokenItr;

public class SinaCurrent {
	public static class PriceAndVolumn {
		public final int volumn;
		public final double price;

		public PriceAndVolumn(int volumn, double price) {
			super();
			this.volumn = volumn;
			this.price = price;
		}

		@Override
		public String toString() {
			return String.format("%d,%.3f", this.volumn, this.price);
		}

	}
	// "东方明珠,22.540,22.540,22.770,22.790,22.510,
	// 22.750,22.780,
	// 4998675,113379865.000,
	// 2017-04-05,15:00:00,00"

	// 东方明珠,22.540,22.540,22.770,22.790,22.510,
	String name;
	double lastClose;
	double thisOpen;
	double current;
	double higest;
	double lowest;
	// 22.750,22.780,
	double currentBuy;
	double currentSell;

	// 4998675,113379865.000,
	long volumn;
	double amount;

	// 1100,22.750,7000,22.740,7400,22.730,22800,22.720,17469,22.710,
	PriceAndVolumn[] buys;
	// 8000,22.780,30000,22.790,20800,22.800,3400,22.810,5000,22.820,
	PriceAndVolumn[] sells;
	//// 2017-04-05,15:00:00,00"
	long time;
	Stock stk;

	public SinaCurrent(Stock stk, String line) throws Exception {
		this(Misc.substring(line, "\"", "\"").split(","), stk);
	}

	public SinaCurrent(String[] tokens, Stock stk) throws Exception {
		if (tokens.length != 33) {
			throw new RuntimeException("expect token numbers 33, but got" + tokens.length + ", they are " + Arrays.toString(tokens));
		}
		this.stk = stk;
		init(new StringTokenItr(tokens));
	}

	private void init(StringTokenItr itr) throws Exception {
		this.name = itr.nextString();
		this.lastClose = itr.nextDouble();
		this.thisOpen = itr.nextDouble();
		this.current = itr.nextDouble();
		this.higest = itr.nextDouble();
		this.lowest = itr.nextDouble();
		this.currentBuy = itr.nextDouble();
		this.currentSell = itr.nextDouble();

		this.volumn = itr.nextLong();
		this.amount = itr.nextDouble();

		this.buys = new PriceAndVolumn[5];
		for (int x = 0; x < 5; x++) {
			this.buys[x] = new PriceAndVolumn(itr.nextInt(), itr.nextDouble());
		}

		this.sells = new PriceAndVolumn[5];
		for (int x = 0; x < 5; x++) {
			this.sells[x] = new PriceAndVolumn(itr.nextInt(), itr.nextDouble());
		}

		String timeStr = itr.nextString() + "_" + itr.nextString();
		//// 2017-04-05,15:00:00,00"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		this.time = sdf.parse(timeStr).getTime();
	}

	private String toString(Double d) {
		return String.format("%.3f", d);
	}

	private String toString(String str) {
		return str;
	}

	private String toString(Long l) {
		return l.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('"');

		sb.append(toString(name));
		sb.append(',');
		sb.append(toString(lastClose));
		sb.append(',');
		sb.append(toString(thisOpen));
		sb.append(',');
		sb.append(toString(current));
		sb.append(',');
		sb.append(toString(higest));
		sb.append(',');
		sb.append(toString(lowest));
		sb.append(',');
		// 22.750,22.780,
		sb.append(toString(currentBuy));
		sb.append(',');
		sb.append(toString(currentSell));
		sb.append(',');

		// 4998675,113379865.000,
		sb.append(toString(volumn));
		sb.append(',');
		sb.append(toString(amount));
		sb.append(',');

		// 1100,22.750,7000,22.740,7400,22.730,22800,22.720,17469,22.710,
		sb.append(toString(buys));
		sb.append(',');
		// 8000,22.780,30000,22.790,20800,22.800,3400,22.810,5000,22.820,
		sb.append(toString(sells));
		sb.append(',');
		//// 2017-04-05,15:00:00,00"
		sb.append(new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss,ss").format(time));
		sb.append('"');
		return sb.toString();
	}

	private static final int mod = 24 * 60 * 60 * 1000;

	public int endTimeMs() {
		return (int) (time % mod);
	}

	private String toString(PriceAndVolumn[] arr) {
		return arr[0] + "," + arr[1] + "," + arr[2] + "," + arr[3] + "," + arr[4];
	}

	public static void main(String[] args) throws Exception {
		String line = "var hq_str_sh600637=\"东方明珠,22.540,22.540,22.770,22.790,22.510,22.750,22.780,4998675,113379865.000,1100,22.750,7000,22.740,7400,22.730,22800,22.720,17469,22.710,8000,22.780,30000,22.790,20800,22.800,3400,22.810,5000,22.820,2017-04-05,15:00:00,00\";";
		SinaCurrent crt = new SinaCurrent(AllStock.getStockByCode("600637"), line);

		System.out.println(crt.toString());
		System.out.println(crt.time % (24 * 60 * 60 * 1000));
		System.out.println(crt.endTimeMs());

	}

	public MinDay toMinDay() {
		MinDay mday = new MinDay();

		mday.setAmount(0);
		mday.setClose(this.currentSell);
		mday.setCode(this.stk);

		mday.setDay(this.day());
		mday.setEndTime(Misc.time(this.time));
		mday.setHigh(this.currentSell);
		mday.setLow(this.currentBuy);
		mday.setOpen(this.currentBuy);
		mday.setStartTime(mday.getEndTime());
		mday.setVolumn(this.volumn);

		return mday;
	}

	private int day() {
		return Statics.day(this.time);
	}
}
