package intertwine_test.rig.exchange;

public interface ObjectExchange<P, Q>
{
	P doExchange( Q request );
}
