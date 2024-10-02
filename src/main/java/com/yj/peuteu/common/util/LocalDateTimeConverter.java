package com.yj.peuteu.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter {
	public static final String MAX_DATE = "9999-12-31";

	/**
	 * LocalDateTime -> 문자열(yyyy-MM-dd)패턴으로 변환
	 * @param target
	 * @return
	 */
	public static String toStringDate(LocalDateTime target) {
		return toString(target, "yyyy-MM-dd");
	}

	public static String toStringDate(LocalDate target) {
		return toString(target, "yyyy-MM-dd");
	}

	/**
	 * LocalDateTime -> 문자열(yyyy-MM-dd hh:mm:ss)패턴으로 변환
	 * @param target
	 * @return
	 */
	public static String toStringDateTime(LocalDateTime target) {
		return toString(target, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * LocalDateTime -> 입력받은 문자열 패턴으로 변환
	 * @param target
	 * @param pattern
	 * @return
	 */
	public static String toString(LocalDateTime target, String pattern) {
		if (target == null)
			return null;
		return target.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static String toString(LocalDate target, String pattern) {
		if (target == null)
			return null;
		return target.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 문자열 pattern 으로 target 문자열 LocalDateTime 변환
	 * @param target
	 * @param pattern
	 * @return
	 */
	public static LocalDateTime toLocalDateTime(String target, String pattern) {
		return LocalDateTime.parse(target, DateTimeFormatter.ofPattern(pattern));
	}

	public static LocalDateTime toLocalDateTimeSecond(String target) {
		return toLocalDateTime(target, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 문자열 pattern 으로 target 문자열 LocalDate 변환
	 * @param target
	 * @param pattern
	 * @return
	 */
	public static LocalDate toLocalDate(String target, String pattern) {
		return LocalDate.parse(target, DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * yyyy-MM-dd 형식의 문자열 LocalDate 변환
	 * Default formatter ("yyyy-MM-dd")
	 * ex) 2023-10-13
	 */
	public static LocalDate toLocalDateDefault(String target) {
		return toLocalDate(target, "yyyy-MM-dd");
	}
}
