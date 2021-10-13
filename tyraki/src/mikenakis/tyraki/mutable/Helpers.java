package mikenakis.tyraki.mutable;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Helpers.
 *
 * @author michael.gr
 */
final class Helpers
{
	private Helpers()
	{
	}

	static <T> Reference<T> newReference( ReferencingMethod referencingMethod, T payload, ReferenceQueue<T> referenceQueue )
	{
		switch( referencingMethod )
		{
			case WEAK:
				return new WeakReference<>( payload, referenceQueue );
			case SOFT:
				return new SoftReference<>( payload, referenceQueue );
			case STRONG:
				return new StrongReference<>( payload );
			default:
				throw new AssertionError();
		}
	}
}
