package org.slf4j.impl;

import io.github.mikenakis.slf4j.Slf4jLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Provides the ILoggerFactory to Slf4j.
 */
@SuppressWarnings( "unused" ) //This class appears to be unused because it is dynamically discovered by Slf4j at runtime.
public final class StaticLoggerBinder implements LoggerFactoryBinder
{
	private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
	private final ILoggerFactory loggerFactory = new Slf4jLoggerFactory();

	private StaticLoggerBinder()
	{
	}

	/**
	 * PEARL: this method is not part of the LoggerFactoryBinder interface, but it must be there, otherwise Slf4j fails.
	 */
	public static StaticLoggerBinder getSingleton()
	{
		return INSTANCE;
	}

	@Override public ILoggerFactory getLoggerFactory()
	{
		return loggerFactory; //NOTE: The instance returned by this method should always be the same object.
	}

	@Override public String getLoggerFactoryClassStr()
	{
		return loggerFactory.getClass().getName();
	}
}
