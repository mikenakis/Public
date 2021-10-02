package mikenakis.test.intertwine.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
