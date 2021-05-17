package mikenakis.kit.events.autonomous;

import mikenakis.kit.Kit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A Blocking Queue which supports wait-while-empty.
 * PEARL: the JDK's {@link BlockingQueue} interface does not offer any means of waiting while the queue is empty.
 * Presumably they do this because they only envision multiple-consumer scenarios, where the queue may momentarily become non-empty and then
 * immediately empty again, before a consumer that was waiting has had a chance to read an element.
 * However, in a single-consumer scenario, wait-while-empty makes sense.
 */
final class SingleConsumerBlockingQueue<T>
{
	private final BlockingQueue<T> jdkQueue = new LinkedBlockingQueue<>();
	private T previewElement = null;

	SingleConsumerBlockingQueue()
	{
	}

	Collection<T> extractAllElements()
	{
		if( previewElement == null && jdkQueue.isEmpty() )
			return List.of();
		List<T> elements = new ArrayList<>();
		if( previewElement != null )
		{
			elements.add( previewElement );
			previewElement = null;
		}
		jdkQueue.drainTo( elements );
		return elements;
	}

	boolean waitUntilNonEmptyOrTimeout( Duration timeout )
	{
		if( previewElement != null )
			return true;
		if( timeout == null )
		{
			previewElement = Kit.unchecked( () -> jdkQueue.take() );
			return true;
		}
		else
		{
			previewElement = Kit.unchecked( () -> jdkQueue.poll( timeout.toNanos(), TimeUnit.NANOSECONDS ) );
			return previewElement != null;
		}
	}

	void add( T item )
	{
		jdkQueue.add( item );
	}
}
