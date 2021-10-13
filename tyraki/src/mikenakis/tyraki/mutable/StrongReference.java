package mikenakis.tyraki.mutable;

import java.lang.ref.WeakReference;

/**
 * A strong reference.
 *
 * NOTE: java.lang.ref.Reference<T> cannot be subclassed directly, so we subclass WeakReference<T> and make it act as a strong reference.
 *
 * @author michael.gr
 */
final class StrongReference<T> extends WeakReference<T>
{
	final T anchor; //permanently anchors the referent, thus making us a strong reference.

	StrongReference( T referent )
	{
		super( referent, null );
		anchor = referent;
	}
}
