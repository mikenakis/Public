package mikenakis_immutability_test;

import mikenakis.immutability.print.AssessmentPrinter;
import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableComponentMutableObjectAssessment;
import mikenakis.immutability.internal.mykit.MyKit;
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

	private static ObjectAssessment assess( Object object )
	{
		System.out.println( "assessment for object " + AssessmentPrinter.stringFromObjectIdentity( object ) + ":" );
		ObjectAssessment assessment;
		try
		{
			assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object );
			assessment = ImmutableObjectAssessment.instance;
		}
		catch( ObjectMustBeImmutableException exception )
		{
			assessment = exception.mutableObjectAssessment;
		}
		AssessmentPrinter.getObjectAssessmentTextTree( assessment ).forEach( s -> System.out.println( "    " + s ) );
		return assessment;
	}

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
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_1_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1 );
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_2_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1, 2 );
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_3_with_immutable_elements_is_actually_immutable()
	{
		List<?> object = List.of( 1, 2, 3 );
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void superficially_immutable_jdk_list_of_size_1_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( mutableElement );
		ObjectAssessment assessment = assess( object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 1 );
	}

	@Test public void superficially_immutable_jdk_list_of_size_2_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( 1, mutableElement );
		ObjectAssessment assessment = assess( object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 2 );
	}

	@Test public void superficially_immutable_jdk_list_of_size_3_with_a_mutable_element_is_actually_mutable()
	{
		Object mutableElement = new ArrayList<>();
		List<?> object = List.of( 1, 2, mutableElement );
		ObjectAssessment assessment = assess( object );
		checkMutableAssessmentOfSuperficiallyImmutableJdkList( mutableElement, object, assessment, 3 );
	}

	private static void checkMutableAssessmentOfSuperficiallyImmutableJdkList( Object mutableElement, List<?> superficiallyImmutableJdkList, ObjectAssessment assessment, int size )
	{
		assert assessment instanceof MutableObjectAssessment;
		MutableObjectAssessment mutableObjectAssessment = (MutableObjectAssessment)assessment;
		assert mutableObjectAssessment.object == superficiallyImmutableJdkList;
		MutableComponentMutableObjectAssessment<?,?> mutableComponentAssessment = (MutableComponentMutableObjectAssessment<?,?>)mutableObjectAssessment;
		assert mutableComponentAssessment.typeAssessment.type == superficiallyImmutableJdkList.getClass();
		assert mutableComponentAssessment.mutableElementIndex == size - 1;
		assert mutableComponentAssessment.elementAssessment.object == mutableElement;
	}
}
