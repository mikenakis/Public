package mikenakis.tyraki.conversion;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableMap;

/**
 * Base class in this package for classes implementing {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
abstract class AbstractMap<K, V> implements UnmodifiableMap.Defaults<K,V>
{
	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " elements";
	}
}
