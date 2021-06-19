package mikenakis.kit;

public abstract class ExecutionContext
{
	public abstract boolean entered();

	/**
	 * Asserts that we are currently inside this {@link ExecutionContext}.
	 *
	 * @return always true
	 */
	public final boolean inContextAssertion()
	{
		assert entered() : toString();
		return true;
	}

	/**
	 * Asserts that we are currently outside of this {@link ExecutionContext}.
	 *
	 * @return always true
	 */
	public final boolean outOfContextAssertion()
	{
		assert !entered() : toString();
		return false;
	}
}
