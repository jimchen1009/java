package com.jim.demo;

import org.apache.commons.lang3.RandomUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PJGMain {
	
	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("C:\\Users\\chenjingjun\\Desktop\\求助.txt");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		List<AskCls> readLines = new ArrayList<>();
		String readLine = bufferedReader.readLine();
		while(readLine != null){
			readLines.add(new AskCls(readLine));
			readLine = bufferedReader.readLine();
		}
		Map<String, List<AskCls>> id2Clses = new HashMap<>();
		for (int i = 1; i < readLines.size(); i++) {
			AskCls askCls = readLines.get(i);
			List<AskCls> askClsList = id2Clses.computeIfAbsent(askCls.Id, key -> new ArrayList<>());
			askClsList.add(askCls);
		}
		List<String> matchList = new LinkedList<>();
		for (List<AskCls> askCls : id2Clses.values()) {
			matchList.addAll(match(askCls));
		}
		int count = 10000;
		for (int i = 1; i <= 3; i++) {
			int min = (i - 1) * count;
			int max = i * count;
			String fileName = String.format("求助压测%s.txt", i );
			FileWriter fileWriter = new FileWriter("C:\\Users\\chenjingjun\\Desktop\\" + fileName);
			fileWriter.write(readLines.get(0).toString() + "\n");
			for (int j = min + 1; j <= max; j++) {
				int length = matchList.size();
				if (length == 0){
					break;
				}
				int index = RandomUtils.nextInt(0, length);
				fileWriter.write(matchList.remove(index) + "\n");
			}
			fileWriter.flush();
			fileWriter.close();
		}
	}

	private static List<String> match(List<AskCls> askClsList){
		Map<String, Deque<AskCls>> account2Clses = new HashMap<>();
		for (AskCls askCls : askClsList) {
			Deque<AskCls> clsList = account2Clses.computeIfAbsent(askCls.openid, key -> new ArrayDeque<>());
			clsList.addLast(askCls);
		}
		List<String> matchStringList = new ArrayList<>();
		Deque<Deque<AskCls>> arrayList = new ArrayDeque<>(account2Clses.values());
		while(arrayList.size() > 1){
			Deque<AskCls> clsDeque0 = arrayList.pollFirst();
			Deque<AskCls> clsDeque1 = arrayList.pollFirst();
			AskCls askCls0 = clsDeque0.pollFirst();
			AskCls askCls1 = clsDeque1.pollFirst();
			if (askCls0 == null){
				if (askCls1 != null){
					clsDeque1.addLast(askCls1);
				}
			}
			else {
				if (askCls1 == null){
					clsDeque0.addLast(askCls0);
				}
				else {
					matchStringList.add(askCls0.toString(askCls1.openid));
					matchStringList.add(askCls1.toString(askCls0.openid));
				}
			}
			if (!clsDeque0.isEmpty()){
				arrayList.addLast(clsDeque0);
			}
			if (!clsDeque1.isEmpty()){
				arrayList.addLast(clsDeque1);
			}
		}
		return matchStringList;
	}

	private static class AskCls{
		private final String openid;
		private final String userid;
		private final String type;
		private final String uniqueid;
		private final String Id;
		private final String DoUserId;

		public AskCls(String string) {
			String[] strings = string.substring(0, string.length() - 1).split(",");
			this.openid = strings[0];
			this.userid = strings[1];
			this.type = strings[2];
			this.uniqueid = strings[3];
			this.Id = strings[4];
			this.DoUserId = strings[5];
		}

		public String toString(String changeAccountId){
			return String.format("%s,%s,%s,%s;", changeAccountId, userid, type, uniqueid);
		}

		public String toString(){
			return toString(openid);
		}
	}
}
