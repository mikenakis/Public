package mikenakis_immutability_test;

import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.print.AssessmentPrinter;
import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.OfMutableTypeMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.TypeImmutabilityAssessor;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.InterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldTypeProvisoryFieldAssessment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test.
 * <p>
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
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
		TypeImmutabilityAssessor typeImmutabilityAssessor = TypeImmutabilityAssessor.create();
		return new ObjectImmutabilityAssessor( typeImmutabilityAssessor );
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
				@SuppressWarnings( "unused" )
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
				@SuppressWarnings( "unused" )
				private final Object selfReference = this;
			}

			static final class ClassExtendingSelfReferencingProvisoryClass extends SelfReferencingProvisoryClass
			{
				@SuppressWarnings( "unused" )
				private final Object selfReference = this;
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryFieldProvisoryTypeAssessment;
				assert assessor.typeImmutabilityAssessor.assess( ClassExtendingSelfReferencingProvisoryClass.class ) instanceof MultiReasonProvisoryTypeAssessment;
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
				@SuppressWarnings( "unused" )
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
				assert assessment instanceof MutableFieldValueMutableObjectAssessment;
				MutableFieldValueMutableObjectAssessment mutableFieldValueAssessment = (MutableFieldValueMutableObjectAssessment)assessment;
				assert mutableFieldValueAssessment.object == object;
				ProvisoryFieldAssessment provisoryFieldAssessment = mutableFieldValueAssessment.provisoryFieldAssessment;
				assert provisoryFieldAssessment.field.getName().equals( "mutableField" );
				ProvisoryFieldTypeProvisoryFieldAssessment provisoryFieldTypeAssessment = (ProvisoryFieldTypeProvisoryFieldAssessment)provisoryFieldAssessment;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment.type == List.class;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment instanceof InterfaceProvisoryTypeAssessment;
				OfMutableTypeMutableObjectAssessment fieldValueAssessment = (OfMutableTypeMutableObjectAssessment)mutableFieldValueAssessment.fieldValueAssessment;
				assert fieldValueAssessment.object == object.mutableField;
				assert fieldValueAssessment.typeAssessment.type == ArrayList.class;
			}
		}.run();
	}

	@Test public void positively_self_assessing_object_is_immutable()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesPositively implements ImmutabilitySelfAssessable
			{
				@SuppressWarnings( "unused" )
				private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return true; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void negatively_self_assessing_object_is_mutable()
	{
		new Runnable()
		{
			static final class ProvisorySelfAssessableClassWhichSelfAssessesNegatively implements ImmutabilitySelfAssessable
			{
				@SuppressWarnings( "unused" )
				private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return false; }
			}

			@Override public void run()
			{
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				ObjectAssessment assessment = assess( assessor, object );
				assert assessment instanceof SelfAssessedMutableObjectAssessment;
				SelfAssessedMutableObjectAssessment mutableSelfAssessment = (SelfAssessedMutableObjectAssessment)assessment;
				assert mutableSelfAssessment.object == object;
				assert mutableSelfAssessment.typeAssessment.type == ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class;
			}
		}.run();
	}

	@Test public void object_with_invariable_array_of_provisory_element_type_with_immutable_elements_is_immutable()
	{
		new Runnable()
		{
			static class ClassWithInvariableArrayOfProvisoryType
			{
				@SuppressWarnings( "unused" )
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
