package com.accenture.ai.utils;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyzerUtil {
	
	public static String wordSplit(String text) throws IOException{
		String word = "";
		 //创建分词对象
        Analyzer anal = new IKAnalyzer(false);
        StringReader reader = new StringReader(text);
        //分词
        TokenStream ts = anal.tokenStream("", reader);
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
        //遍历分词数据
        while(ts.incrementToken()){
        	word += term.toString() + ",";
        }
        reader.close();
        
        return word;
	}

//	public static void main(String[] args) throws IOException {
//
//        System.out.println(IKAnalyzerUtil.wordSplit("我们的男女比例是多少"));
//
//    }
	
}
