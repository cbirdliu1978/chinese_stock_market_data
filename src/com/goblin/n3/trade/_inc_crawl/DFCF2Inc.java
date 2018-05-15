// package com.goblin.n3.trade._inc_crawl;
//
// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
//
// import com.bmtech.datamine.Stock;
// import com.bmtech.datamine.data.DataType;
// import com.bmtech.datamine.data.inc.IncHolder;
// import com.bmtech.datamine.data.mday.MinDay;
// import com.bmtech.utils.Consoler;
// import com.bmtech.utils.ForEach;
// import com.bmtech.utils.Misc;
// import com.bmtech.utils.ZipUnzip;
// import com.bmtech.utils.bmfs.MDir;
// import com.bmtech.utils.bmfs.MFile;
// import com.bmtech.utils.log.L;
// import com.bmtech.utils.var.VarInt;
//
// public class DFCF2Inc {
//
// private File path;
// MDir mdir;
//
// public DFCF2Inc(File path) throws IOException {
//
// this.path = path;
// this.mdir = MDir.open(path);
// }
//
// public void doCodes() {
// Set<String> set = getCodes();
// for (String code : set) {
// L.f("exe code %s", code);
//
// try {
// this.doCode(code);
//
// } catch (Exception e) {
// L.f("error when doing %s", code);
// }
// }
//
// }
//
// public Set<String> getCodes() {
// Set<String> codes = new HashSet<>();
// mdir.getMFiles().forEach((mf) -> {
// String name = mf.getName();
// String code = Misc.substring(name, null, ".");
// codes.add(code);
// });
// L.f("add codes size %s", codes.size());
// return codes;
// }
//
// public void doCode(String code) throws Exception {
// L.f("doing code %s", code);
// List<MinDay> lst = getCodeData(code);
// IncHolder holder = new IncHolder(DataType.min05, Stock.toStockInstance(code));
// holder.merge(lst);
// holder.saveToInc();
// L.f("done code %s, save %s", code, holder.size());
// }
//
// public List<MinDay> getCodeData(String code) {
// Map<Long, MinDay> map = new HashMap<>();
//
// VarInt fileNum = new VarInt();
// ForEach.asc(mdir.getMFiles(), (mfile, mI) -> {
// if (!mfile.getName().startsWith(code)) {
// return;
// }
// try {
// List<MinDay> toMerge = load(code, mfile);
// if (toMerge != null) {
// toMerge.forEach((m) -> {
// map.put(m.dayTime(), m);
// });
// }
// } catch (Exception e) {
// L.f(e, "when doing %s for file %s", mfile.fsId, path);
// e.printStackTrace();
// } finally {
// fileNum.value++;
// }
// });
// List<MinDay> lst = new ArrayList<>(map.values());
// lst.sort((a, b) -> {
// return Long.compare(a.dayTime(), b.dayTime());
// });
// L.f("do code %s from files %s", code, fileNum);
// return lst;
// }
//
// private List<MinDay> load(String code, MFile mfile) throws Exception {
// byte[] bs = mfile.getBytes();
// bs = ZipUnzip.unGzip(bs);
// DFCF_Min05Parser parser = new DFCF_Min05Parser();
//
// List<MinDay> ret = parser.parse(code, new String(bs));
//
// return ret;
// }
//
// public void close() {
// try {
// this.mdir.close();
// } catch (Exception e) {
// e.printStackTrace();
// L.f("when close mdir %s", mdir);
// }
// }
//
// @Override
// public void finalize() {
// this.close();
// }
//
// public static void main(String[] args) throws IOException {
// String path;
// if (args.length > 0) {
// path = args[0];
// } else {
// path = Consoler.readLine("path2Read:");
// }
//
// File dir = new File(path);
// if (!dir.exists()) {
// L.f("dir not exists! %s", dir.getAbsolutePath());
// System.exit(0);
// }
//
// DFCF2Inc dfcf = new DFCF2Inc(dir);
//
// try {
// dfcf.doCodes();
//
// } finally {
// dfcf.close();
// }
// }
//
// }
