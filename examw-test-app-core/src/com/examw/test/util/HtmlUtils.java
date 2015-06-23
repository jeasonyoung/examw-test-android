package com.examw.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author fengwei.
 * @since 2014年12月24日 下午2:40:32.
 */
public class HtmlUtils {
	//过滤P标签
	private final static Pattern PTag = Pattern.compile("</?p[^>]*>", Pattern.CASE_INSENSITIVE);
	//过滤img标签
	private final static Pattern ImgTag = Pattern.compile("<\\s*img\\s+([^>]*)\\s*>", Pattern.CASE_INSENSITIVE);
	/**
	 * 过滤P标签
	 * @param input
	 * @return
	 */
	public static String filterPTag(String input)
	{
		if(input == null) return null;
		Matcher m_html = PTag.matcher(input);
		input = m_html.replaceAll("");
		return replaceSpecialChars(input);
	}
	/**
	 * 替换特殊字符
	 * @param input
	 * @return
	 */
	public static String replaceSpecialChars(String input) {
		if(input == null) return null;
		input = input.replaceAll("&nbsp;", " ");
		input = input.replaceAll("&gt;", ">");
		input = input.replaceAll("&lt;", "<");
		input = input.replaceAll("&amp;", "&");
		return input;
	}
	
	public static String filterImgTag(String input)
	{
		if(input == null) return null;
		Matcher m_html = ImgTag.matcher(input);
		return m_html.replaceAll("");
	}
}
