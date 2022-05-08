package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.object.ObjectImmutabilityAssessor;
import mikenakis.immutability.object.assessments.ImmutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.ObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.MultiReasonMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.SelfAssessedMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableFieldValueMutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsInterfaceProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.assessments.provisory.OfProvisoryTypeProvisoryFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;
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
		TypeImmutabilityAssessor typeImmutabilityAssessor = TypeImmutabilityAssessor.create( TestStringizer.instance );
		return new ObjectImmutabilityAssessor( typeImmutabilityAssessor );
	}

	private static ObjectImmutabilityAssessment assess( ObjectImmutabilityAssessor assessor, Object object )
	{
		ObjectImmutabilityAssessment assessment = assessor.assess( object );
		System.out.println( "assessment for object " + TestStringizer.instance.stringizeObjectIdentity( object ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final ObjectImmutabilityAssessor assessor = newAssessor();

	@Test public void null_is_immutable()
	{
		ObjectImmutabilityAssessment assessment = assess( assessor, null );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void java_lang_Object_is_immutable()
	{
		Object object = new Object();
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void empty_array_is_immutable()
	{
		Object object = new Object[0];
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
	}

	@Test public void non_empty_array_is_mutable() //(even with immutable elements)
	{
		Object object = new Integer[] { 0 };
		ObjectImmutabilityAssessment assessment = assess( assessor, object );
		assert assessment instanceof MutableObjectImmutabilityAssessment;
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
				assert assessor.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryTypeImmutabilityAssessment;
				var object = new SelfReferencingProvisoryClass();
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectImmutabilityAssessment;
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
				assert assessor.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof MultiReasonProvisoryTypeImmutabilityAssessment;
				assert assessor.typeImmutabilityAssessor.assess( ClassExtendingSelfReferencingProvisoryClass.class ) instanceof MultiReasonProvisoryTypeImmutabilityAssessment;
				var object = new ClassExtendingSelfReferencingProvisoryClass();
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectImmutabilityAssessment;
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
				assert assessor.typeImmutabilityAssessor.assess( ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType.class ) instanceof ProvisoryTypeImmutabilityAssessment;
				var object = new ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType();
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectImmutabilityAssessment;
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
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof MultiReasonMutableObjectImmutabilityAssessment;
				MultiReasonMutableObjectImmutabilityAssessment multiReasonAssessment = (MultiReasonMutableObjectImmutabilityAssessment)assessment;
				assert multiReasonAssessment.object == object;
				assert multiReasonAssessment.typeAssessment.type == ClassWithInvariableFieldOfInterfaceTypeWithMutableValue.class;
				assert multiReasonAssessment.reasons.size() == 1;
				HasMutableFieldValueMutableObjectImmutabilityAssessment mutableFieldValueAssessment = (HasMutableFieldValueMutableObjectImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment = mutableFieldValueAssessment.provisoryFieldAssessment;
				assert provisoryFieldAssessment.field.getName().equals( "mutableField" );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment provisoryFieldTypeAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)provisoryFieldAssessment;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment.type == List.class;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment instanceof IsInterfaceProvisoryTypeImmutabilityAssessment;
				OfMutableTypeMutableObjectImmutabilityAssessment fieldValueAssessment = (OfMutableTypeMutableObjectImmutabilityAssessment)mutableFieldValueAssessment.fieldValueAssessment;
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
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof IsSelfAssessableProvisoryTypeImmutabilityAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectImmutabilityAssessment;
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
				assert assessor.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof IsSelfAssessableProvisoryTypeImmutabilityAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof SelfAssessedMutableObjectImmutabilityAssessment;
				SelfAssessedMutableObjectImmutabilityAssessment mutableSelfAssessment = (SelfAssessedMutableObjectImmutabilityAssessment)assessment;
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
				ObjectImmutabilityAssessment assessment = assess( assessor, object );
				assert assessment instanceof ImmutableObjectImmutabilityAssessment;
			}
		}.run();
	}

	@Test public void object_with_array_of_array_is_mutable()
	{
		//TODO
	}
}
