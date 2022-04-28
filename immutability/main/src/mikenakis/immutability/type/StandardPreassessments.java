package mikenakis.immutability.type;

import mikenakis.immutability.helpers.ConcreteMapEntry;
import mikenakis.immutability.helpers.ConvertingIterable;
import mikenakis.immutability.mykit.MyKit;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adds standard preassessments to a {@link TypeImmutabilityAssessor}
 *
 * @author michael.gr
 */
public final class StandardPreassessments
{
	public static void apply( TypeImmutabilityAssessor assessor )
	{
		assessor.addDefaultImmutablePreassessment( Class.class ); //contains caching
		assessor.addDefaultImmutablePreassessment( String.class ); //contains caching
		assessor.addDefaultExtensiblePreassessment( BigDecimal.class ); //is extensible, contains caching, and also a problematic 'precision' field.
		assessor.addDefaultImmutablePreassessment( Method.class ); //contains caching
		assessor.addDefaultImmutablePreassessment( Constructor.class ); //contains caching
		assessor.addDefaultImmutablePreassessment( URI.class ); //has mutable fields, although it is guaranteed to remain constant.
		assessor.addDefaultImmutablePreassessment( URL.class ); //has mutable fields, although it is guaranteed to remain constant.
		assessor.addDefaultImmutablePreassessment( Locale.class ); //has mutable fields, although it is guaranteed to remain constant.
		assessor.addDefaultExtensiblePreassessment( BigInteger.class ); //is extensible, contains mutable fields.
		assessor.addDefaultImmutablePreassessment( StackTraceElement.class );
		assessor.addDefaultImmutablePreassessment( File.class );
		assessor.addDefaultExtensiblePreassessment( InetAddress.class ); //is extensible, contains mutable fields.
		assessor.addDefaultImmutablePreassessment( Inet4Address.class );
		assessor.addDefaultImmutablePreassessment( Inet6Address.class );
		assessor.addDefaultImmutablePreassessment( InetSocketAddress.class );
		assessor.addDefaultIterablePreassessment( MyKit.getClass( List.of() ) );
		assessor.addDefaultIterablePreassessment( MyKit.getClass( List.of( 1 ) ) );
		assessor.addDefaultIterablePreassessment( MyKit.uncheckedClassCast( ConvertingIterable.class ) );
		addJdkSupposedlyImmutableMap( assessor, Map.of() );
		addJdkSupposedlyImmutableMap( assessor, Map.of( "", "" ) );
	}

	/**
	 * PEARL: the supposedly-immutable jdk map extends {@link java.util.AbstractMap} which is mutable because its 'keySet' and 'values' fields are non-final!
	 */
	private static <K, V> void addJdkSupposedlyImmutableMap( TypeImmutabilityAssessor assessor, Map<K,V> supposedlyImmutableJdkMap )
	{
		Class<Map<K,V>> mapClass = MyKit.getClass( supposedlyImmutableJdkMap );
		Decomposer<Map<K,V>,ConcreteMapEntry<K,V>> decomposer = getSupposedlyImmutableJdkMapDecomposer();
		assessor.addDefaultCompositePreassessment( mapClass, decomposer );
	}

	private static class SupposedlyImmutableJdkMapDecomposer<K,V> implements Decomposer<Map<K,V>,ConcreteMapEntry<K,V>>
	{
		@Override public Iterable<ConcreteMapEntry<K,V>> decompose( Map<K,V> map )
		{
			/**
			 * PEARL: the decomposer for the supposedly-immutable jdk map cannot just return {@link Map#entrySet()} because the entry-set is a nested class,
			 * so it has a 'this$0' field, which points back to the map, which is mutable, and therefore makes the entry-set mutable!
			 * So, the decomposer has to iterate the entry-set and yield each element in it.
			 * PEARL: the decomposer cannot just yield each element yielded by the entry-set, because these are instances of {@link java.util.KeyValueHolder}
			 * which cannot be reflected because it is inaccessible! Therefore, the decomposer has to convert each element to an accessible key-value class.
			 */
			Iterable<Map.Entry<K,V>> entrySet = map.entrySet();
			return new ConvertingIterable<>( entrySet, StandardPreassessments::mapEntryConverter );
		}
	}

	private static final SupposedlyImmutableJdkMapDecomposer<Object,Object> supposedlyImmutableJdkMapDeconstructorInstance = new SupposedlyImmutableJdkMapDecomposer<>();

	private static <K, V> Decomposer<Map<K,V>,ConcreteMapEntry<K,V>> getSupposedlyImmutableJdkMapDecomposer()
	{
		@SuppressWarnings( "unchecked" ) Decomposer<Map<K,V>,ConcreteMapEntry<K,V>> result = (SupposedlyImmutableJdkMapDecomposer<K,V>)supposedlyImmutableJdkMapDeconstructorInstance;
		return result;
	}

	private static <K, V> ConcreteMapEntry<K,V> mapEntryConverter( Map.Entry<K,V> mapEntry )
	{
		return new ConcreteMapEntry<>( mapEntry.getKey(), mapEntry.getValue() );
	}
}
