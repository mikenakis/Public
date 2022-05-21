package io.github.mikenakis.kit.logging;

import io.github.mikenakis.kit.SourceLocation;

import java.time.Instant;

/**
 * Logger.
 *
 * @author michael.gr
 */
public interface Logger
{
	class Event
	{
		public final Log.Level logLevel;
		public final SourceLocation sourceLocation;
		public final int eventNumber;
		public final Instant timeStamp;
		public final Thread thread;
		public final String message;

		public Event( Log.Level logLevel, SourceLocation sourceLocation, int eventNumber, Instant timeStamp, Thread thread, String message )
		{
			this.logLevel = logLevel;
			this.sourceLocation = sourceLocation;
			this.eventNumber = eventNumber;
			this.timeStamp = timeStamp;
			this.thread = thread;
			this.message = message;
		}
	}

	void logEvent( Event event );
}
