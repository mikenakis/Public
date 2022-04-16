package mikenakis.kit.lifetime;

/**
 * {@link Mortal} Wrapper for non-{@link Mortal} interfaces.
 *
 * @author michael.gr
 */
public interface MortalWrapper<T> extends Mortal.Defaults
{
	T getTarget();
}
