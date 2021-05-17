package lambdatwine_test.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
