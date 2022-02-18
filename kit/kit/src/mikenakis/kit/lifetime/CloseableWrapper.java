package mikenakis.kit.lifetime;

/**
 * Closeable Wrapper for non-{@link Closeable} interfaces.
 *
 * @author michael.gr
 */
public interface CloseableWrapper<T> extends Closeable.Defaults
{
	T getTarget();
}
