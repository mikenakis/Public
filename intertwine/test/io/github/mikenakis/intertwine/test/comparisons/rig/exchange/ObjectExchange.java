package io.github.mikenakis.intertwine.test.comparisons.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
