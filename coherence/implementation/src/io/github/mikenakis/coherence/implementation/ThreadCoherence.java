package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.Coherence;

/**
 * Thread {@link Coherence}.
 *
 * @author michael.gr
 */
public final class ThreadCoherence extends SimpleCoherence
{
	private final Thread constructionThread;

	public ThreadCoherence()
	{
		constructionThread = Thread.currentThread();
	}

	@Override protected boolean isEntered()
	{
		return Thread.currentThread() == constructionThread;
	}
}
