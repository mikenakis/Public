package mikenakis.saganaki.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements the {@link ILoggerFactory} interface required by Slf4j.
 */
public class Slf4jLoggerFactory implements ILoggerFactory
{
	private final Map<String,Logger> loggerMap = new ConcurrentHashMap<>();

	public Slf4jLoggerFactory()
	{
	}

	@Override public Logger getLogger(String name)
	{
		return loggerMap.computeIfAbsent( name, s -> new KitSlf4jLogger( s ) );
	}
}
