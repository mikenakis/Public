package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.object.ObjectImmutabilityAssessor;
import mikenakis.immutability.object.assessments.ImmutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.ObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableIterableElementMutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test.
 */
@SuppressWarnings( { "FieldMayBeFinal", "InstanceVariableMayNotBeInitialized" } )
public class T11_SuperficiallyImmutableJdkList
{
	public T11_SuperficiallyImmutableJdkList()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static ObjectImmutabilityAssessor newAssessor()
	{
		TypeImmutabilityAssessor classImmutabilityAssessor = TypeImmutabilityAssessor.create( TestStringizer.instance );
		return new ObjectImmutabilityAssessor( classImmutabilityAssessor );
	}

	private static ObjectImmutabilityAssessment assess( ObjectImmutabilityAssessor assessor, Object object )
	{
		ObjectImmutabilityAssessment assessment = assessor.assess( object );
		System.out.println( "assessment for object " + TestStringizer.instance.stringizeObjectIdentity( object ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final ObjectImmutabilityAssessor assessor = newAssessor();

	/**
	 * This checks to make sure that the JDK is using the same jdk-internal superficially-immutable list class for any number of elements above 3,
	 * so that we know for sure that we do not need to test for more than 3 elements.
	 */
	@Test public void superficially_immutable_jdk_list_class_for_3_elements_is_same_as_for_N_elements()
	{
		assert MyKit.getClass( List.of( 1, 2, 3 ) ) == MyKit.getClass( List.of( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ) );
	}

	@Test public void superficially_immutable_jdk_list_of_size_0_is_actually_immutable()
	{
		List<?> object = List.of();
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_1_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1 );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_2_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1, 2 );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_3_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1, 2, 3 );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_1_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( mutableElement );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 1 );
	}

	@Test public void superficially_immutable_jdk_list_of_size_2_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( 1, mutableElement );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 2 );
	}

	@Test public void superficially_immutable_jdk_list_of_size_3_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( 1, 2, mutableElement );
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 3 );
	}

	private static void checkMutableAssessmentOfSuperficiallyImmutableJdkList( Object mutableElement, List<?> superficiallyImmutableJdkList, ObjectImmutabilityAssessment assessment, int size )
	{
		assert assessment instanceof MutableObjectImmutabilityAssessment;
		MutableObjectImmutabilityAssessment mutableObjectAssessment = (MutableObjectImmutabilityAssessment)assessment;
		assert mutableObjectAssessment.object == superficiallyImmutableJdkList;
		assert mutableObjectAssessment instanceof HasMutableIterableElementMutableObjectImmutabilityAssessment;
		HasMutableIterableElementMutableObjectImmutabilityAssessment<?> mutableElementAssessment = (HasMutableIterableElementMutableObjectImmutabilityAssessment<?>)mutableObjectAssessment;
		assert mutableElementAssessment.iterableObject == superficiallyImmutableJdkList;
		assert mutableElementAssessment.typeAssessment.type == superficiallyImmutableJdkList.getClass();
		assert mutableElementAssessment.mutableElementIndex == size - 1;
		assert mutableElementAssessment.elementAssessment.object == mutableElement;
	}
}
