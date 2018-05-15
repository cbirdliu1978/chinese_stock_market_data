package com.goblin.n3.trade._inc_crawl;

import java.util.List;

import com.bmtech.datamine.Stock;
import com.bmtech.datamine.data.DataIntergrationVerifier;
import com.bmtech.datamine.data.DataType;
import com.bmtech.datamine.data.mday.MinDay;

public class NetDownMinDayVerifier {

	private static void throwExc(String def) throws Exception {
		throw new Exception(def);
	}

	public static List<MinDay> verify(List<MinDay> toVerify, Stock stk, DataType dataType) throws Exception {
		// eliminateEmptyDay(List<MinDay>)
		// toVerify = DataIntergrationVerifier.eliminateEmptyDay(toVerify);
		// isDataTypeMatch(MinDay, DataType)
		if (!DataIntergrationVerifier.isDataTypeMatch(toVerify, dataType)) {
			throwExc("dataType not match! expect " + dataType + " for stock " + stk);
		}

		// isCodeMatch(List<MinDay>, String)
		if (!DataIntergrationVerifier.isCodeMatch(toVerify, stk.getCode())) {
			throwExc("code not match! expect " + stk.getCode() + " for stock " + stk);
		}
		// isOrderOk(MinDay, MinDay)
		if (!DataIntergrationVerifier.isOrderOk(toVerify)) {
			throwExc("Order not match! datas is " + toVerify.get(0) + " for stock " + stk);
		}
		if (!DataIntergrationVerifier.isChangeInRange(toVerify)) {
			throwExc("is not ChangeInRange! datas is " + toVerify.get(0) + " for stock " + stk);
		}
		return toVerify;
	}
}
