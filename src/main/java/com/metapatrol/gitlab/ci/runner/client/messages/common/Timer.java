package com.metapatrol.gitlab.ci.runner.client.messages.common;

import java.util.Date;

/**
 * <p>
 * Timer class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class Timer {

	private Date start;
	private long difference = 0;

	/**
	 * <p>
	 * tic.
	 * </p>
	 *
	 * @return a {@link Timer} object.
	 */
	public static Timer tic() {
		Timer timer = new Timer();
		timer.start = new Date();
		return timer;
	}

	/**
	 * <p>
	 * toc.
	 * </p>
	 *
	 * @return a long.
	 */
	public long toc() {
		Date end = new Date();
		difference = end.getTime() - start.getTime();

		return difference;
	}

	/**
	 * <p>
	 * Getter for the field <code>difference</code>.
	 * </p>
	 *
	 * @return a long.
	 */
	public long getDifference() {
		return difference;
	}
}
