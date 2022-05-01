package mikenakis_immutability_test;

import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.object.ObjectImmutabilityAssessor;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Test.
 * <p>
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
 */
@SuppressWarnings( { "FieldMayBeFinal", "InstanceVariableMayNotBeInitialized" } )
public class T13_ObjectImmutabilityAssertions
{
	public T13_ObjectImmutabilityAssertions()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private final ObjectImmutabilityAssessor assessor = new ObjectImmutabilityAssessor( TypeImmutabilityAssessor.create( TestStringizer.instance ) );

	@Test public void immutability_assertion_on_java_lang_Class_passes()
	{
		Class<?> object = Object[].class;
		assert assessor.mustBeImmutableAssertion( object );
	}

	@Test public void immutability_assertion_on_immutable_object_passes()
	{
		new Runnable()
		{
			static final class ImmutableClass
			{ }

			@Override public void run()
			{
				Object object = new ImmutableClass();
				assert assessor.mustBeImmutableAssertion( object );
			}
		}.run();
	}

	@Test public void immutability_assertion_on_mutable_object_fails()
	{
		Object object = new ArrayList<>();
		ObjectMustBeImmutableException exception = MyTestKit.expect( ObjectMustBeImmutableException.class, () -> //
			assessor.mustBeImmutableAssertion( object ) );
		assert exception.mutableObjectAssessment.object == object;
	}

	@Test public void immutability_assertion_on_positively_self_assessing_object_passes()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesPositively implements ImmutabilitySelfAssessable
			{
				@SuppressWarnings( "unused" ) private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return true; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof SelfAssessableAssessment;
				Object object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				assert assessor.mustBeImmutableAssertion( object );
			}
		}.run();
	}

	@Test public void immutability_assertion_on_negatively_self_assessing_object_fails()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesNegatively implements ImmutabilitySelfAssessable
			{
				@SuppressWarnings( "unused" ) private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return false; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof SelfAssessableAssessment;
				Object object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				var exception = MyTestKit.expect( ObjectMustBeImmutableException.class, () -> //
					assessor.mustBeImmutableAssertion( object ) );
				assert exception.mutableObjectAssessment.object == object;
			}
		}.run();
	}
}
