package com.metapatrol.gitlab.ci.runner.client.util;

import java.util.Arrays;

/**
 * <p>
 * ArrayUtil class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class ArrayUtil {

	/**
	 * <p>
	 * concat.
	 * </p>
	 *
	 * @param first
	 *            an array of T objects.
	 * @param second
	 *            an array of T objects.
	 * @param <T>
	 *            a T object.
	 * @return an array of T objects.
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, (first.length + second.length));
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
