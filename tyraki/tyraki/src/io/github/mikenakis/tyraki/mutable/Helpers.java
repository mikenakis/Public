package io.github.mikenakis.tyraki.mutable;

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
		return switch( referencingMethod )
			{
				case WEAK -> new WeakReference<>( payload, referenceQueue );
				case SOFT -> new SoftReference<>( payload, referenceQueue );
				case STRONG -> new StrongReference<>( payload );
			};
	}
}
