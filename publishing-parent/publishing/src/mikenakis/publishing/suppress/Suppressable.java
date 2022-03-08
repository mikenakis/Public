package mikenakis.publishing.suppress;

/**
 * Represents an object with a suppression count.
 *
 * @author michael.gr
 */
public interface Suppressable
{
	/**
	 * Creates a new {@link Suppression} on this {@link Suppressable}.
	 *
	 * @return a new {@link Suppression} on this {@link Suppressable}.
	 */
	Suppression newSuppression();

	/**
	 * Increments the suppression count of this {@link Suppressable}.
	 */
	void incrementSuppressionCount();

	/**
	 * Decrements the suppression count of this {@link Suppressable}.
	 */
	void decrementSuppressionCount();

	interface Decorator extends Suppressable
	{
		Suppressable getDecoratedSuppressable();

		@Override default Suppression newSuppression()
		{
			Suppressable decoree = getDecoratedSuppressable();
			return decoree.newSuppression();
		}

		@Override default void incrementSuppressionCount()
		{
			Suppressable decoree = getDecoratedSuppressable();
			decoree.incrementSuppressionCount();
		}

		@Override default void decrementSuppressionCount()
		{
			Suppressable decoree = getDecoratedSuppressable();
			decoree.decrementSuppressionCount();
		}
	}
}
