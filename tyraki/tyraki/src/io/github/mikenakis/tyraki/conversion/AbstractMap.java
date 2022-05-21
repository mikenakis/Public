package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableMap;

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
