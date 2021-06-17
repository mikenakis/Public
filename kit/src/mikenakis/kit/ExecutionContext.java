package mikenakis.kit;

public abstract class ExecutionContext
{
	public abstract Thread thread(); //XXX REMOVE

	/**
	 * Asserts that we are currently inside this {@link ExecutionContext}.
	 *
	 * @return always true
	 */
	public abstract boolean inContextAssertion();

	/**
	 * Asserts that we are currently outside of this {@link ExecutionContext}.
	 *
	 * @return always true
	 */
	public abstract boolean outOfContextAssertion();
}
