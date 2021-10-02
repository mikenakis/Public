package mikenakis.test.lambdatwine.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
