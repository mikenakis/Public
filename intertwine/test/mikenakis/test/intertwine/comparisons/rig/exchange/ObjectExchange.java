package mikenakis.test.intertwine.comparisons.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
