package mikenakis_immutability_test;

import mikenakis.immutability.AssessmentPrinter;
import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableComponentMutableObjectAssessment;
import mikenakis.immutability.internal.helpers.ConcreteMapEntry;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.TypeImmutabilityAssessor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Test.
 */
@SuppressWarnings( { "FieldMayBeFinal", "InstanceVariableMayNotBeInitialized" } )
public class T12_SuperficiallyImmutableJdkMap
{
	public T12_SuperficiallyImmutableJdkMap()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static ObjectImmutabilityAssessor newAssessor()
	{
		TypeImmutabilityAssessor classImmutabilityAssessor = TypeImmutabilityAssessor.create( TestStringizer.instance );
		return new ObjectImmutabilityAssessor( classImmutabilityAssessor );
	}

	private static ObjectAssessment assess( ObjectImmutabilityAssessor assessor, Object object )
	{
		System.out.println( "assessment for object " + TestStringizer.instance.stringizeObjectIdentity( object ) + ":" );
		ObjectAssessment assessment;
		try
		{
			assert assessor.mustBeImmutableAssertion( object );
			assessment = ImmutableObjectAssessment.instance;
		}
		catch( ObjectMustBeImmutableException exception )
		{
			assessment = exception.mutableObjectAssessment;
		}
		new AssessmentPrinter( TestStringizer.instance ).getAssessmentTextTree( assessment ).forEach( s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final ObjectImmutabilityAssessor assessor = newAssessor();

	/**
	 * This checks to make sure that the JDK is using the same jdk-internal superficially-immutable map class for zero entries as for 2 entries,
	 * so that we know for sure that we do not need a special case test for zero entries.
	 */
	@Test public void superficially_immutable_jdk_map_class_for_zero_entries_is_same_as_for_N_entries()
	{
		assert MyKit.getClass( Map.<Integer,Integer>of() ) == MyKit.getClass( Map.of( 1, 0, 2, 0 ) );
	}

	/**
	 * This checks to make sure that the JDK is using the same jdk-internal superficially-immutable map class for any number of entries above 2,
	 * so that we know for sure that we do not need to test for more than 2 entries.
	 */
	@Test public void superficially_immutable_jdk_map_class_for_2_entries_is_same_as_for_N_entries()
	{
		assert MyKit.getClass( Map.of( 1, 0, 2, 0 ) ) == MyKit.getClass( Map.of( 1, 0, 2, 0, 3, 0 ) );
	}

	/**
	 * This checks to make sure that the JDK is using the same jdk-internal superficially-immutable map class for a certain number of entries regardless of how
	 * the map is created.
	 */
	@Test public void superficially_immutable_jdk_map_class_for_N_entries_is_same_no_matter_how_it_is_created()
	{
		assert MyKit.getClass( Map.of() ) == MyKit.getClass( Map.ofEntries() );
		assert MyKit.getClass( Map.of( 1, 0 ) ) == MyKit.getClass( Map.ofEntries( Map.entry( 1, 0 ) ) );
		assert MyKit.getClass( Map.of( 1, 0, 2, 0 ) ) == MyKit.getClass( Map.ofEntries( Map.entry( 1, 0 ), Map.entry( 2, 0 ) ) );
		assert MyKit.getClass( Map.of( 1, 0, 2, 0, 3, 0 ) ) == MyKit.getClass( Map.ofEntries( Map.entry( 1, 0 ), Map.entry( 2, 0 ), Map.entry( 3, 0 ) ) );
	}

	@Test public void superficially_immutable_jdk_map_of_size_0_is_actually_immutable()
	{
		Map<?,?> mapObject = Map.of();
		ObjectAssessment assessment = assess( assessor, mapObject );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_map_of_size_1_with_immutable_keys_and_values_is_immutable()
	{
		Map<?,?> mapObject = Map.of( 1, "", 2, "" );
		ObjectAssessment assessment = assess( assessor, mapObject );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_map_of_size_2_with_immutable_keys_and_values_is_immutable()
	{
		Map<?,?> mapObject = Map.of( 1, "", 2, "", 3, "" );
		ObjectAssessment assessment = assess( assessor, mapObject );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_map_of_size_1_with_a_mutable_key_is_actually_mutable()
	{
		ConcreteMapEntry<ArrayList<?>,String> mutableEntry = new ConcreteMapEntry<>( new ArrayList<>(), "" );
		Map<?,?> mapObject = Map.of( mutableEntry.key(), mutableEntry.value() );
		ObjectAssessment assessment = assess( assessor, mapObject );
		checkMutableAssessmentOfSuperficiallyImmutableJdkMap( mapObject, mutableEntry, assessment, 1 );
	}

	@Test public void superficially_immutable_jdk_map_of_size_2_with_a_mutable_key_is_actually_mutable()
	{
		ConcreteMapEntry<ArrayList<?>,String> mutableEntry = new ConcreteMapEntry<>( new ArrayList<>(), "" );
		Map<?,?> mapObject = Map.of( 1, "", mutableEntry.key(), mutableEntry.value() );
		ObjectAssessment assessment = assess( assessor, mapObject );
		checkMutableAssessmentOfSuperficiallyImmutableJdkMap( mapObject, mutableEntry, assessment, 2 );
	}

	@Test public void superficially_immutable_jdk_map_of_size_1_with_a_mutable_value_is_actually_mutable()
	{
		ConcreteMapEntry<String,ArrayList<?>> mutableEntry = new ConcreteMapEntry<>( "1", new ArrayList<>() );
		Map<?,?> mapObject = Map.of( mutableEntry.key(), mutableEntry.value() );
		ObjectAssessment assessment = assess( assessor, mapObject );
		checkMutableAssessmentOfSuperficiallyImmutableJdkMap( mapObject, mutableEntry, assessment, 1 );
	}

	@Test public void superficially_immutable_jdk_map_of_size_2_with_a_mutable_value_is_actually_mutable()
	{
		ConcreteMapEntry<String,ArrayList<?>> mutableEntry = new ConcreteMapEntry<>( "2", new ArrayList<>() );
		Map<?,?> mapObject = Map.of( "1", 0, mutableEntry.key(), mutableEntry.value() );
		ObjectAssessment assessment = assess( assessor, mapObject );
		checkMutableAssessmentOfSuperficiallyImmutableJdkMap( mapObject, mutableEntry, assessment, 2 );
	}

	private static void checkMutableAssessmentOfSuperficiallyImmutableJdkMap( Map<?,?> mapObject, Object mutableEntry, ObjectAssessment assessment, int size )
	{
		assert assessment instanceof MutableObjectAssessment;
		MutableObjectAssessment mutableObjectAssessment = (MutableObjectAssessment)assessment;
		assert mutableObjectAssessment.object == mapObject;
		assert mutableObjectAssessment instanceof MutableComponentMutableObjectAssessment;
		MutableComponentMutableObjectAssessment<?,?> mutableComponentAssessment = (MutableComponentMutableObjectAssessment<?,?>)mutableObjectAssessment;
		assert mutableComponentAssessment.typeAssessment.type == mapObject.getClass();
		assert mutableComponentAssessment.mutableElementIndex >= 0 && mutableComponentAssessment.mutableElementIndex < size; //cannot assert precise index because hashmaps garble the order of items
		assert mutableComponentAssessment.elementAssessment.object == mutableComponentAssessment.mutableElement;
		assert mutableComponentAssessment.mutableElement.equals( mutableEntry );
	}
}
