package com.ovidiuiliescu;

/**
 * Class used for timing stuff.
 * @author Ovidiu
 *
 */
public class Timer {

	private static long lastTime = 0;
	private static long[] totalExecutionTimes = null;
	private static long[] tempExecutionTimes = null;
	private static String[] timerDescriptions = null;
	private static boolean outputResults = true ;
	
	/**
	 * Reset all timers
	 */
	private static void resetTimers() {

		int size = totalExecutionTimes.length ;
		for (int i = 0; i < size; i++) {
			totalExecutionTimes[i] = 0;
			tempExecutionTimes[i] = 0;
		}
		lastTime = System.currentTimeMillis(); 
	}
	
	public Timer(int noOfTimers)
	{
		initTimers(noOfTimers); 
	}
	
	/**
	 * Output timer information to console
	 */
	private static void outputTimers()
	{
		int size = totalExecutionTimes.length ;
		String temp ;
		for (int i = 0; i < size; i++) {
			if ( totalExecutionTimes[i] > 0)
			{
				if ( timerDescriptions[i] == null )
				{
					temp = "#" + i;
				}
				else
				{
					temp = "\"" + timerDescriptions[i] + "\"";
				}
				
				if ( outputResults )
				{
					//#debug ovidiu
					System.out.println("Timer " + temp + " has value " + totalExecutionTimes[i]);
				}
			}
		} 
	}

	/**
	 * (Re-)Initialize all timers
	 * @param noOfTimers
	 */
	public static void initTimers(int noOfTimers) { 
		totalExecutionTimes = new long[noOfTimers];
		tempExecutionTimes = new long[noOfTimers];
		timerDescriptions = new String[noOfTimers];
		lastTime = System.currentTimeMillis();
	}
	
	/**
	 * Set the description text of a certain timer
	 * @param timerIndex
	 * @param description
	 */
	public static void setDescription(int timerIndex, String description)
	{
		timerDescriptions[timerIndex] = description;
	}
	
	/**
	 * Start the clock on a certain timer
	 * @param index
	 */
	public static void startTimer(int index) 
	{
		tempExecutionTimes[index] = System.currentTimeMillis();
	}
	
	/**
	 * Increment a certain timer by 1. Useful for measuring how many times a certain piece of code was executed.
	 * @param index
	 */
	public static void incrementTimer(int index)
	{
		totalExecutionTimes[index]++;
	}
	
	/**
	 * Pause the clock on a certain timer
	 * @param index
	 */
	public static void pauseTimer(int index)
	{
		// If timer is paused without being started first, we must ignore the rest of the method 
		if ( tempExecutionTimes[index] == 0 )
		{
			return ;
		}
		
		totalExecutionTimes[index] += System.currentTimeMillis() - tempExecutionTimes[index];
		tempExecutionTimes[index] = 0 ;
	}
	
	/**
	 * Check if enough time has elapsed since this method was last called. If enough time has passed, output the timer values.
	 * @param desiredElapsedTime
	 */
	public static void check(long desiredElapsedTime)
	{
		long currentTime = System.currentTimeMillis();
		if ( currentTime - desiredElapsedTime > lastTime )
		{
			if ( outputResults )
			{
				//#debug ovidiu
				System.out.println("\n> TIME ELAPSED : " + (currentTime - lastTime) );
			}
			outputTimers();
			resetTimers();
		}
	}
	
	public static void haltOutput()
	{
		outputResults = false;
	}
	
	public static void resumeOutput()
	{
		outputResults = true ;
	}

}
