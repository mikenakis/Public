package mikenakis_immutability_test;

import mikenakis.immutability.internal.assessments.mutable.MutableSuperclassMutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisorySuperclassProvisoryTypeAssessment;
import mikenakis.immutability.print.AssessmentPrinter;
import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.ObjectImmutabilityAssessor;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableClassMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryTypeAssessment;
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

	@Test public void null_is_immutable()
	{
		ObjectAssessment assessment = assess( null );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void java_lang_Object_is_immutable()
	{
		Object object = new Object();
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void empty_array_is_immutable()
	{
		Object object = new Object[0];
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof ImmutableObjectAssessment;
	}

	@Test public void array_of_immutable_elements_is_mutable()
	{
		Object object = new Integer[] { 1 };
		ObjectAssessment assessment = assess( object );
		assert assessment instanceof MutableObjectAssessment;
	}

	@Test public void immutable_object_with_mutable_super_is_mutable()
	{
		new Runnable()
		{
			static class Superclass
			{
				@SuppressWarnings( "unused" ) final Object provisoryFieldAssessedAsMutable = new StringBuilder();
			}

			static final class Derived extends Superclass
			{ }

			@Override public void run()
			{
				var object = new Derived();
				ObjectAssessment assessment = assess( object );
				assert assessment instanceof MutableSuperclassMutableObjectAssessment;
				var mutableSuperclassMutableObjectAssessment = (MutableSuperclassMutableObjectAssessment)assessment;
				assert mutableSuperclassMutableObjectAssessment.object == object;
				assert mutableSuperclassMutableObjectAssessment.typeAssessment instanceof ProvisorySuperclassProvisoryTypeAssessment;
				assert mutableSuperclassMutableObjectAssessment.object == object;
				assert mutableSuperclassMutableObjectAssessment.mutableSuperObjectAssessment instanceof MutableFieldValueMutableObjectAssessment;
				var mutableFieldValueMutableObjectAssessment = (MutableFieldValueMutableObjectAssessment)mutableSuperclassMutableObjectAssessment.mutableSuperObjectAssessment;
				assert mutableFieldValueMutableObjectAssessment.object == object;
				assert mutableFieldValueMutableObjectAssessment.declaringTypeAssessment instanceof ProvisoryFieldProvisoryTypeAssessment;
				assert mutableFieldValueMutableObjectAssessment.fieldValueAssessment instanceof MutableClassMutableObjectAssessment;
				assert mutableFieldValueMutableObjectAssessment.fieldValueAssessment.object == object.provisoryFieldAssessedAsMutable;
				assert mutableFieldValueMutableObjectAssessment.provisoryFieldAssessment instanceof ProvisoryFieldTypeProvisoryFieldAssessment;
			}
		}.run();
	}

	@Test public void circularly_self_referencing_immutable_object_is_immutable()
	{
		new Runnable()
		{
			static final class SelfReferencingProvisoryClass
			{
				@SuppressWarnings( "unused" ) private final Object selfReference = this;
			}

			@Override public void run()
			{
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryTypeAssessment;
				var object = new SelfReferencingProvisoryClass();
				ObjectAssessment assessment = assess( object );
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
				@SuppressWarnings( "unused" ) private final Object selfReference = this;
			}

			static final class ClassExtendingSelfReferencingProvisoryClass extends SelfReferencingProvisoryClass
			{
				@SuppressWarnings( "unused" ) private final Object selfReference = this;
			}

			@Override public void run()
			{
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( SelfReferencingProvisoryClass.class ) instanceof ProvisoryFieldProvisoryTypeAssessment;
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ClassExtendingSelfReferencingProvisoryClass.class ) instanceof MultiReasonProvisoryTypeAssessment;
				var object = new ClassExtendingSelfReferencingProvisoryClass();
				ObjectAssessment assessment = assess( object );
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
				@SuppressWarnings( "unused" ) private final Comparable<String> invariableFieldOfInterfaceType = "";
			}

			@Override public void run()
			{
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType.class ) instanceof ProvisoryTypeAssessment;
				var object = new ClassWithInvariableFieldOfInterfaceTypeWithValueOfImmutableType();
				ObjectAssessment assessment = assess( object );
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
				ObjectAssessment assessment = assess( object );
				assert assessment instanceof MutableFieldValueMutableObjectAssessment;
				MutableFieldValueMutableObjectAssessment mutableFieldValueAssessment = (MutableFieldValueMutableObjectAssessment)assessment;
				assert mutableFieldValueAssessment.object == object;
				ProvisoryFieldAssessment provisoryFieldAssessment = mutableFieldValueAssessment.provisoryFieldAssessment;
				assert provisoryFieldAssessment.field.getName().equals( "mutableField" );
				ProvisoryFieldTypeProvisoryFieldAssessment provisoryFieldTypeAssessment = (ProvisoryFieldTypeProvisoryFieldAssessment)provisoryFieldAssessment;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment.type == List.class;
				assert provisoryFieldTypeAssessment.provisoryTypeAssessment instanceof InterfaceProvisoryTypeAssessment;
				MutableClassMutableObjectAssessment fieldValueAssessment = (MutableClassMutableObjectAssessment)mutableFieldValueAssessment.fieldValueAssessment;
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
				@SuppressWarnings( "unused" ) private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return true; }
			}

			@Override public void run()
			{
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesPositively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesPositively();
				ObjectAssessment assessment = assess( object );
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
				@SuppressWarnings( "unused" ) private final Object provisoryMember = null;
				@Override public boolean isImmutable() { return false; }
			}

			@Override public void run()
			{
				assert ObjectImmutabilityAssessor.instance.typeImmutabilityAssessor.assess( ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class ) instanceof SelfAssessableProvisoryTypeAssessment;
				var object = new ProvisorySelfAssessableClassWhichSelfAssessesNegatively();
				ObjectAssessment assessment = assess( object );
				assert assessment instanceof SelfAssessedMutableObjectAssessment;
				SelfAssessedMutableObjectAssessment mutableSelfAssessment = (SelfAssessedMutableObjectAssessment)assessment;
				assert mutableSelfAssessment.object == object;
				assert mutableSelfAssessment.typeAssessment.type == ProvisorySelfAssessableClassWhichSelfAssessesNegatively.class;
			}
		}.run();
	}

	@Test public void object_with_invariable_array_of_provisory_element_type_with_mutable_elements_is_mutable()
	{
		new Runnable()
		{
			static class ClassWithInvariableArrayOfProvisoryType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final Object[] arrayField = { new StringBuilder() };
			}

			@Override public void run()
			{
				Object object = new ClassWithInvariableArrayOfProvisoryType();
				ObjectAssessment assessment = assess( object );
				assert assessment instanceof MutableObjectAssessment;
			}
		}.run();
	}

	@Test public void object_with_invariable_array_of_provisory_element_type_with_immutable_elements_is_immutable()
	{
		new Runnable()
		{
			static class ClassWithInvariableArrayOfProvisoryType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final Object[] arrayField = { 1 };
			}

			@Override public void run()
			{
				Object object = new ClassWithInvariableArrayOfProvisoryType();
				ObjectAssessment assessment = assess( object );
				assert assessment instanceof ImmutableObjectAssessment;
			}
		}.run();
	}

	@Test public void object_with_array_of_array_is_mutable()
	{
		//TODO
	}
}
