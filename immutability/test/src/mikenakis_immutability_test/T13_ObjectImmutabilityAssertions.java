package mikenakis_immutability_test;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Test.
 * <p>
 * TODO: get rid of!
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

	@Test public void immutability_assertion_on_java_lang_Class_passes()
	{
		Class<?> object = Object[].class;
		assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object );
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
				assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object );
			}
		}.run();
	}

	@Test public void immutability_assertion_on_mutable_object_fails()
	{
		Object object = new ArrayList<>();
		ObjectMustBeImmutableException exception = MyTestKit.expect( ObjectMustBeImmutableException.class, () -> //
			ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object ) );
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
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				Object object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object );
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
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				Object object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				var exception = MyTestKit.expect( ObjectMustBeImmutableException.class, () -> //
					ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( object ) );
				assert exception.mutableObjectAssessment.object == object;
			}
		}.run();
	}
}
