package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.object.ObjectImmutabilityAssessor;
import mikenakis.immutability.object.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.object.assessments.ObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableFieldValuesAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableSelfAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeAssessment;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.object.fieldvalue.MutableFieldValueAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.InterfaceAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldTypeAssessment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test.
 * <p>
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
 * <p>
 * NOTE: due to a bug either in testana or in the intellij idea debugger, breakpoints inside these runnables do not hit when running from within testana.
 * See Stackoverflow: "Intellij Idea breakpoints do not hit in anonymous inner class" https://stackoverflow.com/q/70949498/773113
 */
@SuppressWarnings( { "FieldMayBeFinal", "InstanceVariableMayNotBeInitialized" } )
public class T10_ObjectImmutabilityAssessor
{
	public T10_ObjectImmutabilityAssessor()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static ObjectImmutabilityAssessor newAssessor()
	{
		TypeImmutabilityAssessor typeImmutabilityAssessor = TypeImmutabilityAssessor.create( TestStringizer.instance );
		return new ObjectImmutabilityAssessor( typeImmutabilityAssessor );
	}

	private static ObjectAssessment assess( ObjectImmutabilityAssessor assessor, Object object )
	{
		ObjectAssessment assessment = assessor.assess( object );
		System.out.println( "assessment for object " + TestStringizer.instance.stringizeObjectIdentity( object ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final ObjectImmutabilityAssessor assessor = newAssessor();

	@Test public void null_is_immutable()
	{
		ObjectAssessment assessment = assess( assessor, null );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void java_lang_Object_is_immutable()
	{
		Object object = new Object();
		ObjectAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void empty_array_is_immutable()
	{
		Object object = new Object[0];
		ObjectAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void non_empty_array_is_mutable() //(even with immutable elements)
	{
		Object object = new Integer[] { 0 };
		ObjectAssessment assessment = assess( assessor, object );
		assert assessment instanceof MutableObjectAssessment;
	}

	@Test public void circularly_self_referencing_immutable_object_is_immutable()
	{
		new Runnable()
		{
			static final class SelfReferencingProvisoryClass
			{
				private final Object selfReference = this;
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryTypeAssessment;
				var object = new SelfReferencingProvisoryClass();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void circularly_self_referencing_object_extending_circularly_self_referencing_immutable_object_is_immutable()
	{
		new Runnable()
		{
			static class SelfReferencingProvisoryClass
			{
				private final Object selfReference = this;
			}

			static final class ClassExtendingSelfReferencingProvisoryClass extends SelfReferencingProvisoryClass
			{
				private final Object selfReference = this;
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryContentAssessment;
				assert assessor.typeImmutabilityAssessor.assess( ClassExtendingSelfReferencingProvisoryClass.class ) instanceof ProvisoryContentAssessment;
				var object = new ClassExtendingSelfReferencingProvisoryClass();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void object_with_invariable_field_of_interface_type_with_immutable_value_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType
			{
				private final Comparable<String> invariableFieldOfInterfaceType = "";
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType.class ) instanceof ProvisoryTypeAssessment;
				var object = new ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void object_with_invariable_field_of_interface_type_with_mutable_value_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfInterfaceTypeWithMutableValue
			{
				final List<String> mutableField = new ArrayList<>();
			}

			@Override public void run()
			{
				var object = new ClassWithInvariableFieldOfInterfaceTypeWithMutableValue();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof MutableFieldValuesAssessment;
				MutableFieldValuesAssessment mutableFieldValuesAssessment = (MutableFieldValuesAssessment)assessment;
				assert mutableFieldValuesAssessment.object == object;
				assert mutableFieldValuesAssessment.typeAssessment.type == ClassWithInvariableFieldOfInterfaceTypeWithMutableValue.class;
				assert mutableFieldValuesAssessment.typeAssessment.fieldAssessments.size() == 1;
				ProvisoryFieldAssessment provisoryFieldAssessment = mutableFieldValuesAssessment.typeAssessment.fieldAssessments.get( 0 );
				assert provisoryFieldAssessment.field.getName().equals( "mutableField" );
				assert provisoryFieldAssessment instanceof ProvisoryFieldTypeAssessment;
				ProvisoryFieldTypeAssessment provisoryFieldTypeAssessment = (ProvisoryFieldTypeAssessment)provisoryFieldAssessment;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment.type == List.class;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment instanceof InterfaceAssessment;
				assert mutableFieldValuesAssessment.fieldValueAssessments.size() == 1;
				MutableFieldValueAssessment fieldValueAssessment = mutableFieldValuesAssessment.fieldValueAssessments.get( 0 );
				assert fieldValueAssessment.provisoryFieldAssessment == provisoryFieldAssessment;
				assert fieldValueAssessment.fieldValue.equals( object.mutableField );
				assert fieldValueAssessment.mutableObjectAssessment.object == object.mutableField;
				assert fieldValueAssessment.mutableObjectAssessment instanceof OfMutableTypeAssessment;
				OfMutableTypeAssessment ofMutableTypeAssessment = (OfMutableTypeAssessment)fieldValueAssessment.mutableObjectAssessment;
				assert ofMutableTypeAssessment.typeAssessment.type == ArrayList.class;
			}
		}.run();
	}

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
		ObjectMustBeImmutableException exception = MyTestKit.expect( ObjectMustBeImmutableException.class, () -> assessor.mustBeImmutableAssertion( object ) );
		assert exception.mutableObjectAssessment.object == object;
	}

	@Test public void positively_self_assessing_object_is_immutable()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesPositively implements ImmutabilitySelfAssessable
			{
				private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return true; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof SelfAssessableAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void immutability_assertion_on_positively_self_assessing_object_passes()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesPositively implements ImmutabilitySelfAssessable
			{
				private final Object provisoryMember = null;
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

	@Test public void negatively_self_assessing_object_is_mutable()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesNegatively implements ImmutabilitySelfAssessable
			{
				private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return false; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof SelfAssessableAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof MutableSelfAssessment;
				MutableSelfAssessment mutableSelfAssessment = (MutableSelfAssessment)assessment;
				assert mutableSelfAssessment.object == object;
				assert mutableSelfAssessment.typeAssessment.type == ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class;
			}
		}.run();
	}

	@Test public void immutability_assertion_on_negatively_self_assessing_object_fails()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesNegatively implements ImmutabilitySelfAssessable
			{
				private final Object provisoryMember = null;
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

	@Test public void object_with_invariable_array_of_provisory_element_type_with_immutable_elements_is_immutable()
	{
		new Runnable()
		{
			static class ClassWithInvariableArrayOfProvisoryType
			{
				@InvariableArray private final Object[] arrayField = { 1 };
			}

			@Override public void run()
			{
				Object object = new ClassWithInvariableArrayOfProvisoryType();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void object_with_array_of_array_is_mutable()
	{
		//TODO
	}
}
