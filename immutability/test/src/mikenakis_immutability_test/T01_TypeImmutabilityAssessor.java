package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.functional.Function0;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.IsArrayMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.HasMutableFieldsMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.HasMutableSuperclassMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsExtensibleProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsInterfaceProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryAncestorProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryFieldProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableArrayFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMayNotAlreadyBeInvariableException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.NonArrayFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotAlreadyBeImmutableException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotBePreviouslyAssessedException;
import mikenakis.immutability.type.exceptions.PreassessedTypeMustBeClassException;
import mikenakis.immutability.type.exceptions.SelfAssessableClassMustBeNonImmutableException;
import mikenakis.immutability.type.exceptions.VariableFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.OfMutableFieldTypeMutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.IsInvariableArrayProvisoryFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.OfProvisoryTypeProvisoryFieldImmutabilityAssessment;
import org.junit.Test;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Test.
 * <p>
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
 */
@SuppressWarnings( { "FieldMayBeStatic", "FieldMayBeFinal", "InstanceVariableMayNotBeInitialized" } )
public class T01_TypeImmutabilityAssessor
{
	public T01_TypeImmutabilityAssessor()
	{
		if( !MyKit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	private static TypeImmutabilityAssessor newAssessor()
	{
		return TypeImmutabilityAssessor.create( TestStringizer.instance );
	}

	private static TypeImmutabilityAssessment assess( TypeImmutabilityAssessor assessor, Class<?> type )
	{
		TypeImmutabilityAssessment assessment = assessor.assess( type );
		System.out.println( "assessment for type " + TestStringizer.instance.stringizeClassName( type ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final TypeImmutabilityAssessor assessor = newAssessor();

	@Test public void primitive_classes_are_immutable()
	{
		for( Class<?> primitiveClass : MyTestKit.getAllPrimitives() )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, primitiveClass );
			assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
		}
	}

	@Test public void primitive_wrapper_classes_are_immutable()
	{
		for( Class<?> primitiveWrapperClass : MyTestKit.getAllPrimitiveWrappers() )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, primitiveWrapperClass );
			assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
		}
	}

	@Test public void array_class_is_mutable()
	{
		TypeImmutabilityAssessment assessment = assess( assessor, Integer[].class );
		assert assessment instanceof IsArrayMutableTypeImmutabilityAssessment : assessment;
	}

	@Test public void java_lang_object_is_extensible()
	{
		TypeImmutabilityAssessment assessment = assess( assessor, Object.class );
		assert assessment instanceof IsExtensibleProvisoryTypeImmutabilityAssessment : assessment;
	}

	@Test public void preassessed_provisory_classes_are_provisory()
	{
		List<Class<?>> classes = List.of( BigDecimal.class, BigInteger.class, InetAddress.class, //
			List.of().getClass(), List.of( 1 ).getClass(), List.of( 2 ).getClass(), Map.of().getClass(), Map.of( 1, 0 ).getClass(), Map.of( 2, 0 ).getClass() );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ProvisoryTypeImmutabilityAssessment : assessment;
		}
	}

	@Test public void preassessed_immutable_classes_are_immutable()
	{
		List<Class<?>> classes = List.of( Class.class, String.class, Method.class, Constructor.class, URI.class, URL.class, Locale.class, //
			StackTraceElement.class, File.class, Inet4Address.class, Inet6Address.class, InetSocketAddress.class );
		for( Class<?> jvmClass : classes )
		{
			TypeImmutabilityAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
		}
	}

	@Test public void interface_is_provisory()
	{
		new Runnable()
		{
			private interface EmptyInterface
			{ }

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, EmptyInterface.class );
				assert assessment instanceof IsInterfaceProvisoryTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void empty_inextensible_class_is_immutable()
	{
		new Runnable()
		{
			static final class EmptyInextensibleClass
			{ }

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, EmptyInextensibleClass.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void immutable_class_with_invariable_circular_reference_is_immutable()
	{
		new Runnable()
		{
			static final class ImmutableClassWithInvariableCircularReference
			{
				@SuppressWarnings( "unused" ) public final ImmutableClassWithInvariableCircularReference circularReference = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ImmutableClassWithInvariableCircularReference.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void extensible_class_with_invariable_circular_reference_is_extensible()
	{
		new Runnable()
		{
			static class ExtensibleClassWithInvariableCircularReference
			{
				@SuppressWarnings( "unused" ) public final ExtensibleClassWithInvariableCircularReference circularReference = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ExtensibleClassWithInvariableCircularReference.class );
				assert assessment instanceof IsExtensibleProvisoryTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void immutable_class_with_mutable_static_field_is_immutable()
	{
		new Runnable()
		{
			static final class ImmutableClassWithStaticMutableField
			{
				@SuppressWarnings( "unused" ) private static int staticMutableField = 0;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ImmutableClassWithStaticMutableField.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_mutable_type_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfMutableType
			{
				@SuppressWarnings( "unused" ) private final ArrayList<String> invariableFieldOfMutableType = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableFieldOfMutableType.class );
				assert assessment instanceof HasMutableFieldsMutableTypeImmutabilityAssessment : assessment;
				HasMutableFieldsMutableTypeImmutabilityAssessment mutableFieldsAssessment = (HasMutableFieldsMutableTypeImmutabilityAssessment)assessment;
				assert mutableFieldsAssessment.type == ClassWithInvariableFieldOfMutableType.class;
				assert mutableFieldsAssessment.mutableFieldAssessments.size() == 1;
				MutableFieldImmutabilityAssessment mutableFieldAssessment = mutableFieldsAssessment.mutableFieldAssessments.get( 0 );
				assert mutableFieldAssessment.field.getName().equals( "invariableFieldOfMutableType" );
				assert mutableFieldAssessment instanceof OfMutableFieldTypeMutableFieldImmutabilityAssessment;
				OfMutableFieldTypeMutableFieldImmutabilityAssessment finalButOfMutableTypeMutableFieldAssessment = (OfMutableFieldTypeMutableFieldImmutabilityAssessment)mutableFieldAssessment;
				assert finalButOfMutableTypeMutableFieldAssessment.fieldTypeAssessment.type == ArrayList.class;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_immutable_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfImmutableType
			{
				@SuppressWarnings( "unused" ) private final String invariableFieldOfImmutableType = "";
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableFieldOfImmutableType.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_extending_extensible_provisory_class_is_immutable()
	{
		new Runnable()
		{
			static class ExtensibleProvisoryClass
			{ }

			static final class InextensibleProvisoryClass extends ExtensibleProvisoryClass
			{ }

			@Override public void run()
			{
				assert assess( newAssessor(), ExtensibleProvisoryClass.class ) instanceof IsExtensibleProvisoryTypeImmutabilityAssessment;
				TypeImmutabilityAssessment assessment = assess( assessor, InextensibleProvisoryClass.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_extending_mutable_class_is_mutable()
	{
		new Runnable()
		{
			static class MutableClass
			{
				@SuppressWarnings( "unused" ) private final ArrayList<String> mutableField = null;
			}

			static final class ClassExtendingMutableClass extends MutableClass
			{ }

			@Override public void run()
			{
				assert assess( newAssessor(), MutableClass.class ) instanceof HasMutableFieldsMutableTypeImmutabilityAssessment;
				TypeImmutabilityAssessment assessment = assess( assessor, ClassExtendingMutableClass.class );
				assert assessment instanceof HasMutableSuperclassMutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_variable_field_of_immutable_type_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithVariableFieldOfImmutableType
			{
				@SuppressWarnings( "unused" ) private int variableFieldOfPrimitiveType = 0;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithVariableFieldOfImmutableType.class );
				assert assessment instanceof HasMutableFieldsMutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_interface_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfInterfaceType
			{
				@SuppressWarnings( "unused" ) private final List<String> invariableFieldOfInterfaceType = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableFieldOfInterfaceType.class );
				MultiReasonProvisoryTypeImmutabilityAssessment multiReasonAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert multiReasonAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment reason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment fieldAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)reason.fieldAssessment;
				assert fieldAssessment.field.getName().equals( "invariableFieldOfInterfaceType" );
				IsInterfaceProvisoryTypeImmutabilityAssessment fieldTypeAssessment = (IsInterfaceProvisoryTypeImmutabilityAssessment)fieldAssessment.provisoryTypeAssessment;
				assert fieldTypeAssessment.type == List.class;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_provisory_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfProvisoryType
			{
				@SuppressWarnings( "unused" ) private final Object invariableFieldOfProvisoryType = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableFieldOfProvisoryType.class );
				MultiReasonProvisoryTypeImmutabilityAssessment multiReasonAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert multiReasonAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment reason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment fieldAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)reason.fieldAssessment;
				assert fieldAssessment.field.getName().equals( "invariableFieldOfProvisoryType" );
				IsExtensibleProvisoryTypeImmutabilityAssessment fieldTypeAssessment = (IsExtensibleProvisoryTypeImmutabilityAssessment)fieldAssessment.provisoryTypeAssessment;
				assert fieldTypeAssessment.type == Object.class;
			}
		}.run();
	}

	@Test public void class_with_annotated_invariable_field_of_immutable_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithAnnotatedInvariableFieldOfImmutableType
			{
				@SuppressWarnings( "unused" ) @InvariableField private int annotatedInvariableFieldOfImmutableType = 0;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableFieldOfImmutableType.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_annotated_invariable_field_of_provisory_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithAnnotatedInvariableFieldOfProvisoryType
			{
				@SuppressWarnings( "unused" ) @InvariableField private List<Integer> stableField = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableFieldOfProvisoryType.class );
				MultiReasonProvisoryTypeImmutabilityAssessment multiReasonAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert multiReasonAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment reason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment fieldAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)reason.fieldAssessment;
				assert fieldAssessment.field.getName().equals( "stableField" );
				IsInterfaceProvisoryTypeImmutabilityAssessment fieldTypeAssessment = (IsInterfaceProvisoryTypeImmutabilityAssessment)fieldAssessment.provisoryTypeAssessment;
				assert fieldTypeAssessment.type == List.class;
			}
		}.run();
	}

	@Test public void invariable_annotation_on_public_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPublicFieldAnnotatedInvariable
			{
				@SuppressWarnings( "unused" ) @InvariableField public Integer publicFieldAnnotatedInvariable;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithPublicFieldAnnotatedInvariable.class ) );
				assert exception.field.getName().equals( "publicFieldAnnotatedInvariable" );
			}
		}.run();
	}

	@Test public void invariable_annotation_on_protected_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithProtectedFieldAnnotatedInvariable
			{
				@SuppressWarnings( "unused" ) @InvariableField protected Integer protectedFieldAnnotatedInvariable;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithProtectedFieldAnnotatedInvariable.class ) );
				assert exception.field.getName().equals( "protectedFieldAnnotatedInvariable" );
			}
		}.run();
	}

	@Test public void invariable_annotation_on_package_private_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPublicFieldAnnotatedInvariable
			{
				@SuppressWarnings( "unused" ) @InvariableField Integer packagePrivateFieldAnnotatedInvariable;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithPublicFieldAnnotatedInvariable.class ) );
				assert exception.field.getName().equals( "packagePrivateFieldAnnotatedInvariable" );
			}
		}.run();
	}

	@Test public void class_with_annotated_invariable_self_reference_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithAnnotatedInvariableSelfReference
			{
				@SuppressWarnings( "unused" ) @InvariableField private ClassWithAnnotatedInvariableSelfReference annotatedInvariableSelfReference = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableSelfReference.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void invariable_annotation_on_already_invariable_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithInvariableFieldAnnotatedInvariable
			{
				@SuppressWarnings( "unused" ) @InvariableField private final Integer invariableFieldAnnotatedInvariable = Integer.MAX_VALUE;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableFieldMayNotAlreadyBeInvariableException.class, () -> assess( assessor, ClassWithInvariableFieldAnnotatedInvariable.class ) );
				assert exception.field.getName().equals( "invariableFieldAnnotatedInvariable" );
			}
		}.run();
	}

	// TODO: revise the purposefulness of this. Perhaps we should throw an exception, because doing such a thing cannot possibly lead to any good.
	@Test public void invariable_annotation_on_mutable_field_is_ignored()
	{
		new Runnable()
		{
			static class ClassWithMutableFieldAnnotatedInvariable
			{
				@SuppressWarnings( "unused" ) @InvariableField private ArrayList<Integer> mutableFieldAnnotatedInvariable = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithMutableFieldAnnotatedInvariable.class );
				assert assessment instanceof MutableTypeImmutabilityAssessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_provisory_element_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfProvisoryElementType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final Object[] array = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfProvisoryElementType.class );
				MultiReasonProvisoryTypeImmutabilityAssessment multiReasonAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert multiReasonAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment reason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				assert reason.fieldAssessment.field.getName().equals( "array" );
				IsInvariableArrayProvisoryFieldImmutabilityAssessment fieldAssessment = (IsInvariableArrayProvisoryFieldImmutabilityAssessment)reason.fieldAssessment;
				assert fieldAssessment.arrayElementTypeAssessment.type == Object.class;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_immutable_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfImmutableElementType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final int[] array = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfImmutableElementType.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_invariable_field_of_immutable_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayInvariableFieldOfImmutableElementType
			{
				@SuppressWarnings( "unused" ) @InvariableField @InvariableArray private int[] array = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableArrayInvariableFieldOfImmutableElementType.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_public_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPublicFieldAnnotatedInvariableArray
			{
				@SuppressWarnings( "unused" ) @InvariableArray public final int[] publicFieldAnnotatedInvariableArray = null;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableArrayFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithPublicFieldAnnotatedInvariableArray.class ) );
				assert exception.field.getName().equals( "publicFieldAnnotatedInvariableArray" );
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_protected_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithProtectedFieldAnnotatedInvariableArray
			{
				@SuppressWarnings( "unused" ) @InvariableArray protected final int[] protectedFieldAnnotatedInvariableArray = null;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableArrayFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithProtectedFieldAnnotatedInvariableArray.class ) );
				assert exception.field.getName().equals( "protectedFieldAnnotatedInvariableArray" );
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_package_private_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPackagePrivateFieldAnnotatedInvariableArray
			{
				@SuppressWarnings( "unused" ) @InvariableArray final int[] packagePrivateFieldAnnotatedInvariableArray = null;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( AnnotatedInvariableArrayFieldMustBePrivateException.class, () -> //
					assess( assessor, ClassWithPackagePrivateFieldAnnotatedInvariableArray.class ) );
				assert exception.field.getName().equals( "packagePrivateFieldAnnotatedInvariableArray" );
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_non_array_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithNonArrayField
			{
				@SuppressWarnings( "unused" ) @InvariableArray private Object nonArrayField = null;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( NonArrayFieldMayNotBeAnnotatedInvariableArrayException.class, () -> //
					assess( assessor, ClassWithNonArrayField.class ) );
				assert exception.field.getName().equals( "nonArrayField" );
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_variable_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithVariableFieldAnnotatedAsInvariableArray
			{
				@SuppressWarnings( "unused" ) @InvariableArray private Object[] variableField = null;
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( VariableFieldMayNotBeAnnotatedInvariableArrayException.class, () -> //
					assess( assessor, ClassWithVariableFieldAnnotatedAsInvariableArray.class ) );
				assert exception.field.getName().equals( "variableField" );
			}
		}.run();
	}

	@Test public void immutable_preassessment_on_already_immutable_class_is_caught()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfImmutableType
			{
				@SuppressWarnings( "unused" ) private final String immutableField = "";
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( PreassessedClassMustNotAlreadyBeImmutableException.class, () -> //
					assessor.addImmutablePreassessment( ClassWithInvariableFieldOfImmutableType.class ) );
				assert exception.jvmClass == ClassWithInvariableFieldOfImmutableType.class;
			}
		}.run();
	}

	@Test public void immutable_preassessment_on_interface_is_caught()
	{
		new Runnable()
		{
			interface Interface
			{ }

			@Override public void run()
			{
				var exception = MyTestKit.expect( PreassessedTypeMustBeClassException.class, () -> //
					assessor.addImmutablePreassessment( Interface.class ) );
				assert exception.type == Interface.class;
			}
		}.run();
	}

	@Test public void immutable_preassessment_on_array_is_caught()
	{
		Class<?> arrayClass = int[].class;
		var exception = MyTestKit.expect( PreassessedTypeMustBeClassException.class, () -> //
			assessor.addImmutablePreassessment( arrayClass ) );
		assert exception.type == arrayClass;
	}

	@Test public void immutable_preassessment_on_previously_assessed_immutable_class_is_caught()
	{
		new Runnable()
		{
			static final class ImmutableClass
			{ }

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ImmutableClass.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment;
				var exception = MyTestKit.expect( PreassessedClassMustNotBePreviouslyAssessedException.class, () -> //
					assessor.addImmutablePreassessment( ImmutableClass.class ) );
				assert exception.previousTypeAssessment == assessment;
			}
		}.run();
	}

	@Test public void immutable_preassessment_on_previously_assessed_mutable_class_is_caught()
	{
		new Runnable()
		{
			static class ClassWithMutableField
			{
				@SuppressWarnings( "unused" ) private final ArrayList<String> mutableField = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithMutableField.class );
				assert assessment instanceof HasMutableFieldsMutableTypeImmutabilityAssessment;
				HasMutableFieldsMutableTypeImmutabilityAssessment mutableFieldsAssessment = (HasMutableFieldsMutableTypeImmutabilityAssessment)assessment;
				assert mutableFieldsAssessment.type == ClassWithMutableField.class;
				var exception = MyTestKit.expect( PreassessedClassMustNotBePreviouslyAssessedException.class, () -> //
					assessor.addImmutablePreassessment( ClassWithMutableField.class ) );
				assert exception.previousTypeAssessment == assessment;
			}
		}.run();
	}

	@Test public void mutable_class_preassessed_as_immutable_is_immutable()
	{
		new Runnable()
		{
			static final class MutableClass
			{
				@SuppressWarnings( "unused" ) private int mutableField;
			}

			@Override public void run()
			{
				assert newAssessor().assess( MutableClass.class ) instanceof MutableTypeImmutabilityAssessment;
				assessor.addImmutablePreassessment( MutableClass.class );
				TypeImmutabilityAssessment assessment = assess( assessor, MutableClass.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void provisory_class_preassessed_as_immutable_is_immutable()
	{
		new Runnable()
		{
			static final class ProvisoryClass
			{
				@SuppressWarnings( "unused" ) private final Function0<Integer> provisoryField = null;
			}

			@Override public void run()
			{
				assert newAssessor().assess( ProvisoryClass.class ) instanceof ProvisoryTypeImmutabilityAssessment;
				assessor.addImmutablePreassessment( ProvisoryClass.class );
				TypeImmutabilityAssessment assessment = assess( assessor, ProvisoryClass.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_extending_class_with_reference_to_descendant_is_correctly_assessed()
	{
		new Runnable()
		{
			static class ClassWithReferenceToDescendant
			{
				@SuppressWarnings( "unused" ) private final ClassExtendingClassWithReferenceToDescendant descendant = null;
			}

			static final class ClassExtendingClassWithReferenceToDescendant extends ClassWithReferenceToDescendant
			{ }

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassExtendingClassWithReferenceToDescendant.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_containing_invariable_member_of_interface_type_is_provisory()
	{
		new Runnable()
		{
			public interface SomeInterface
			{ }

			static class ClassWithInterfaceMember
			{
				@SuppressWarnings( "unused" ) private final SomeInterface someInterface = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInterfaceMember.class );
				MultiReasonProvisoryTypeImmutabilityAssessment multiReasonAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert multiReasonAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment reason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)multiReasonAssessment.reasons.get( 0 );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment fieldAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)reason.fieldAssessment;
				assert fieldAssessment.field.getName().equals( "someInterface" );
				IsInterfaceProvisoryTypeImmutabilityAssessment fieldTypeAssessment = (IsInterfaceProvisoryTypeImmutabilityAssessment)fieldAssessment.provisoryTypeAssessment;
				assert fieldTypeAssessment.type == SomeInterface.class;
			}
		}.run();
	}

	@Test public void inextensible_class_extending_provisory_class_is_provisory()
	{
		new Runnable()
		{
			public interface SomeInterface
			{ }

			static class ProvisoryClass
			{
				@SuppressWarnings( "unused" ) private final SomeInterface someInterface = null;
			}

			static final class ClassExtendingProvisoryClass extends ProvisoryClass
			{ }

			@Override public void run()
			{
				assert assess( newAssessor(), ProvisoryClass.class ) instanceof MultiReasonProvisoryTypeImmutabilityAssessment;
				TypeImmutabilityAssessment assessment = assess( assessor, ClassExtendingProvisoryClass.class );
				assert assessment instanceof MultiReasonProvisoryTypeImmutabilityAssessment : assessment;
				MultiReasonProvisoryTypeImmutabilityAssessment provisoryContentAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)assessment;
				assert provisoryContentAssessment.reasons.size() == 1;
				HasProvisoryAncestorProvisoryTypeImmutabilityAssessment reason = (HasProvisoryAncestorProvisoryTypeImmutabilityAssessment)provisoryContentAssessment.reasons.get( 0 );
				MultiReasonProvisoryTypeImmutabilityAssessment ancestorAssessment = (MultiReasonProvisoryTypeImmutabilityAssessment)reason.ancestorAssessment;
				assert ancestorAssessment.type == ProvisoryClass.class;
				assert ancestorAssessment.reasons.size() == 1;
				HasProvisoryFieldProvisoryTypeImmutabilityAssessment ancestorReason = (HasProvisoryFieldProvisoryTypeImmutabilityAssessment)ancestorAssessment.reasons.get( 0 );
				OfProvisoryTypeProvisoryFieldImmutabilityAssessment fieldAssessment = (OfProvisoryTypeProvisoryFieldImmutabilityAssessment)ancestorReason.fieldAssessment;
				assert fieldAssessment.field.getName().equals( "someInterface" );
				IsInterfaceProvisoryTypeImmutabilityAssessment fieldTypeAssessment = (IsInterfaceProvisoryTypeImmutabilityAssessment)fieldAssessment.provisoryTypeAssessment;
				assert fieldTypeAssessment.type == SomeInterface.class;
			}
		}.run();
	}

	@Test public void enums_are_normally_immutable()
	{
		new Runnable()
		{
			enum E
			{
				@SuppressWarnings( "unused" ) A //this is essentially a static field, so it does not affect the assessment.
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, E.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void enums_with_variable_fields_are_mutable()
	{
		new Runnable()
		{
			enum EnumWithVariableField
			{
				@SuppressWarnings( "unused" ) A;
				@SuppressWarnings( "unused" ) int a;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, EnumWithVariableField.class );
				assert assessment instanceof HasMutableFieldsMutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_array_field_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithArrayField
			{
				@SuppressWarnings( "unused" ) public final int[] array = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithArrayField.class );
				assert assessment instanceof HasMutableFieldsMutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_circular_reference_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfCircularReferenceElementType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final ClassWithInvariableArrayFieldOfCircularReferenceElementType[] array = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfCircularReferenceElementType.class );
				assert assessment instanceof ImmutableTypeImmutabilityAssessment : assessment;
			}
		}.run();
	}

	// TODO: revise the purposefulness of allowing this. Perhaps we should not allow it.
	@Test public void invariable_array_annotation_on_array_field_of_mutable_element_type_is_ignored()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayInvariableFieldOfMutableElementType
			{
				@SuppressWarnings( "unused" ) @InvariableArray private final ArrayList<Integer>[] arrayOfMutableElements = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, ClassWithInvariableArrayInvariableFieldOfMutableElementType.class );
				assert assessment instanceof MutableTypeImmutabilityAssessment;
			}
		}.run();
	}

	@Test public void mutable_self_assessable_class_is_self_assessable_provisory()
	{
		new Runnable()
		{
			static final class MutableSelfAssessableClass implements ImmutabilitySelfAssessable
			{
				@SuppressWarnings( "unused" ) public int mutableField;
				@Override public boolean isImmutable() { throw new AssertionError(); /* we do not expect this to be invoked. */ }
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, MutableSelfAssessableClass.class );
				assert assessment instanceof IsSelfAssessableProvisoryTypeImmutabilityAssessment;
			}
		}.run();
	}

	@Test public void self_assessable_class_that_is_already_immutable_is_caught()
	{
		new Runnable()
		{
			static final class ImmutableSelfAssessableClass implements ImmutabilitySelfAssessable
			{
				@Override public boolean isImmutable() { throw new AssertionError(); /* we do not expect this to be invoked. */ }
			}

			@Override public void run()
			{
				var exception = MyTestKit.expect( SelfAssessableClassMustBeNonImmutableException.class, () -> //
					assess( assessor, ImmutableSelfAssessableClass.class ) );
				assert exception.jvmClass == ImmutableSelfAssessableClass.class;
			}
		}.run();
	}

	@Test public void immutable_class_with_supertype_field_is_immutable()
	{
		new Runnable()
		{
			static final class DerivedClass extends SuperClass
			{ }

			static class SuperClass
			{
				@SuppressWarnings( "unused" ) public final DerivedClass derivedClassField = null;
			}

			@Override public void run()
			{
				TypeImmutabilityAssessment assessment = assess( assessor, SuperClass.class );
				assert assessment instanceof IsExtensibleProvisoryTypeImmutabilityAssessment;
			}
		}.run();
	}
}
