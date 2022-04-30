package mikenakis_immutability_test;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.functional.Function0;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableArrayAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableFieldsAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableSuperclassAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.InterfaceAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableArrayFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMayNotAlreadyBeInvariableException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.NonArrayFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotAlreadyBeImmutableException;
import mikenakis.immutability.type.exceptions.PreassessedClassMustNotBePreviouslyAssessedException;
import mikenakis.immutability.type.exceptions.PreassessedTypeMustBeClassException;
import mikenakis.immutability.type.exceptions.SelfAssessableClassMustBeNonImmutableException;
import mikenakis.immutability.type.exceptions.VariableFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldTypeMutableFieldAssessment;
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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Test.
 * <p>
 * NOTE: the {@code new Runnable().run()} business is a trick for creating multiple local namespaces within a single java source file.
 * <p>
 * NOTE: due to a bug either in testana or in the intellij idea debugger, breakpoints inside these runnables do not hit when running from within testana.
 * See Stackoverflow: "Intellij Idea breakpoints do not hit in anonymous inner class" https://stackoverflow.com/q/70949498/773113
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

	private static TypeAssessment assess( TypeImmutabilityAssessor assessor, Class<?> type )
	{
		TypeAssessment assessment = assessor.assess( type );
		System.out.println( "assessment for type " + TestStringizer.instance.stringizeClassName( type ) + ":" );
		MyKit.<Assessment>tree( assessment, a -> a.children(), a -> a.toString(), s -> System.out.println( "    " + s ) );
		return assessment;
	}

	private final TypeImmutabilityAssessor assessor = newAssessor();

	@Test public void primitive_classes_are_immutable()
	{
		for( Class<?> primitiveClass : getAllPrimitives() )
		{
			TypeAssessment assessment = assess( assessor, primitiveClass );
			assert assessment instanceof ImmutableTypeAssessment : assessment;
		}
	}

	@Test public void primitive_wrapper_classes_are_immutable()
	{
		for( Class<?> primitiveWrapperClass : getAllPrimitiveWrappers() )
		{
			TypeAssessment assessment = assess( assessor, primitiveWrapperClass );
			assert assessment instanceof ImmutableTypeAssessment : assessment;
		}
	}

	@Test public void array_class_is_mutable()
	{
		TypeAssessment assessment = assess( assessor, Integer[].class );
		assert assessment instanceof MutableArrayAssessment : assessment;
	}

	@Test public void java_lang_object_is_extensible()
	{
		TypeAssessment assessment = assess( assessor, Object.class );
		assert assessment instanceof ExtensibleAssessment : assessment;
	}

	@Test public void preassessed_provisory_classes_are_provisory()
	{
		List<Class<?>> classes = List.of( BigDecimal.class, BigInteger.class, InetAddress.class, //
			List.of().getClass(), List.of( 1 ).getClass(), List.of( 2 ).getClass(), Map.of().getClass(), Map.of( 1, 0 ).getClass(), Map.of( 2, 0 ).getClass() );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ProvisoryTypeAssessment : assessment;
		}
	}

	@Test public void preassessed_immutable_classes_are_immutable()
	{
		List<Class<?>> classes = List.of( Class.class, String.class, Method.class, Constructor.class, URI.class, URL.class, Locale.class, //
			StackTraceElement.class, File.class, Inet4Address.class, Inet6Address.class, InetSocketAddress.class );
		for( Class<?> jvmClass : classes )
		{
			TypeAssessment assessment = assess( assessor, jvmClass );
			assert assessment instanceof ImmutableTypeAssessment : assessment;
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
				TypeAssessment assessment = assess( assessor, EmptyInterface.class );
				assert assessment instanceof InterfaceAssessment : assessment;
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
				TypeAssessment assessment = assess( assessor, EmptyInextensibleClass.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void immutable_class_with_invariable_circular_reference_is_immutable()
	{
		new Runnable()
		{
			static final class ImmutableClassWithInvariableCircularReference
			{
				public final ImmutableClassWithInvariableCircularReference circularReference = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ImmutableClassWithInvariableCircularReference.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void extensible_class_with_invariable_circular_reference_is_extensible()
	{
		new Runnable()
		{
			static class ExtensibleClassWithInvariableCircularReference
			{
				public final ExtensibleClassWithInvariableCircularReference circularReference = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ExtensibleClassWithInvariableCircularReference.class );
				assert assessment instanceof ExtensibleAssessment : assessment;
			}
		}.run();
	}

	@Test public void immutable_class_with_mutable_static_field_is_immutable()
	{
		new Runnable()
		{
			static final class ImmutableClassWithStaticMutableField
			{
				private static int staticMutableField = 0;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ImmutableClassWithStaticMutableField.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_mutable_type_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfMutableType
			{
				private final ArrayList<String> invariableFieldOfMutableType = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableFieldOfMutableType.class );
				assert assessment instanceof MutableFieldsAssessment : assessment;
				MutableFieldsAssessment mutableFieldsAssessment = (MutableFieldsAssessment)assessment;
				assert mutableFieldsAssessment.type == ClassWithInvariableFieldOfMutableType.class;
				assert mutableFieldsAssessment.mutableFieldAssessments.size() == 1;
				MutableFieldAssessment mutableFieldAssessment = mutableFieldsAssessment.mutableFieldAssessments.get( 0 );
				assert mutableFieldAssessment.field.getName().equals( "invariableFieldOfMutableType" );
				assert mutableFieldAssessment instanceof MutableFieldTypeMutableFieldAssessment;
				MutableFieldTypeMutableFieldAssessment finalButOfMutableTypeMutableFieldAssessment = (MutableFieldTypeMutableFieldAssessment)mutableFieldAssessment;
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
				private final String invariableFieldOfImmutableType = "";
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableFieldOfImmutableType.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
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
				assert assess( newAssessor(), ExtensibleProvisoryClass.class ) instanceof ExtensibleAssessment;
				TypeAssessment assessment = assess( assessor, InextensibleProvisoryClass.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_extending_mutable_class_is_mutable()
	{
		new Runnable()
		{
			static class MutableClass
			{
				private final ArrayList<String> mutableField = null;
			}

			static final class ClassExtendingMutableClass extends MutableClass
			{ }

			@Override public void run()
			{
				assert assess( newAssessor(), MutableClass.class ) instanceof MutableFieldsAssessment;
				TypeAssessment assessment = assess( assessor, ClassExtendingMutableClass.class );
				assert assessment instanceof MutableSuperclassAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_variable_field_of_immutable_type_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithVariableFieldOfImmutableType
			{
				private int variableFieldOfPrimitiveType = 0;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithVariableFieldOfImmutableType.class );
				assert assessment instanceof MutableFieldsAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_interface_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfInterfaceType
			{
				private final List<String> invariableFieldOfInterfaceType = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableFieldOfInterfaceType.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_field_of_provisory_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableFieldOfProvisoryType
			{
				private final Object invariableFieldOfProvisoryType = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableFieldOfProvisoryType.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_annotated_invariable_field_of_immutable_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithAnnotatedInvariableFieldOfImmutableType
			{
				@InvariableField private int annotatedInvariableFieldOfImmutableType = 0;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableFieldOfImmutableType.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_annotated_invariable_field_of_provisory_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithAnnotatedInvariableFieldOfProvisoryType
			{
				@InvariableField private List<Integer> stableField = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableFieldOfProvisoryType.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
			}
		}.run();
	}

	@Test public void invariable_annotation_on_public_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPublicFieldAnnotatedInvariable
			{
				@InvariableField public Integer publicFieldAnnotatedInvariable;
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
				@InvariableField protected Integer protectedFieldAnnotatedInvariable;
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
				@InvariableField Integer packagePrivateFieldAnnotatedInvariable;
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
				@InvariableField private ClassWithAnnotatedInvariableSelfReference annotatedInvariableSelfReference = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithAnnotatedInvariableSelfReference.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void invariable_annotation_on_already_invariable_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithInvariableFieldAnnotatedInvariable
			{
				@InvariableField private final Integer invariableFieldAnnotatedInvariable = Integer.MAX_VALUE;
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
				@InvariableField private ArrayList<Integer> mutableFieldAnnotatedInvariable = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithMutableFieldAnnotatedInvariable.class );
				assert assessment instanceof MutableTypeAssessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_provisory_element_type_is_provisory()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfProvisoryElementType
			{
				@InvariableArray private final Object[] array = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfProvisoryElementType.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_immutable_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfImmutableElementType
			{
				@InvariableArray private final int[] array = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfImmutableElementType.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_invariable_field_of_immutable_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayInvariableFieldOfImmutableElementType
			{
				@InvariableField @InvariableArray private int[] array = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableArrayInvariableFieldOfImmutableElementType.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void invariable_array_annotation_on_public_field_is_caught()
	{
		new Runnable()
		{
			static class ClassWithPublicFieldAnnotatedInvariableArray
			{
				@InvariableArray public final int[] publicFieldAnnotatedInvariableArray = null;
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
				@InvariableArray protected final int[] protectedFieldAnnotatedInvariableArray = null;
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
				@InvariableArray final int[] packagePrivateFieldAnnotatedInvariableArray = null;
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
				@InvariableArray private Object nonArrayField = null;
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
				@InvariableArray private Object[] variableField = null;
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
				private final String immutableField = "";
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
				TypeAssessment assessment = assess( assessor, ImmutableClass.class );
				assert assessment instanceof ImmutableTypeAssessment;
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
				private final ArrayList<String> mutableField = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithMutableField.class );
				assert assessment instanceof MutableFieldsAssessment;
				MutableFieldsAssessment mutableFieldsAssessment = (MutableFieldsAssessment)assessment;
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
				private int mutableField;
			}

			@Override public void run()
			{
				assert newAssessor().assess( MutableClass.class ) instanceof MutableTypeAssessment;
				assessor.addImmutablePreassessment( MutableClass.class );
				TypeAssessment assessment = assess( assessor, MutableClass.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void provisory_class_preassessed_as_immutable_is_immutable()
	{
		new Runnable()
		{
			static final class ProvisoryClass
			{
				private final Function0<Integer> provisoryField = null;
			}

			@Override public void run()
			{
				assert newAssessor().assess( ProvisoryClass.class ) instanceof ProvisoryTypeAssessment;
				assessor.addImmutablePreassessment( ProvisoryClass.class );
				TypeAssessment assessment = assess( assessor, ProvisoryClass.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_extending_class_with_reference_to_descendant_is_correctly_assessed()
	{
		new Runnable()
		{
			static class ClassWithReferenceToDescendant
			{
				private final ClassExtendingClassWithReferenceToDescendant descendant = null;
			}

			static final class ClassExtendingClassWithReferenceToDescendant extends ClassWithReferenceToDescendant
			{ }

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassExtendingClassWithReferenceToDescendant.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
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
				private final SomeInterface someInterface = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInterfaceMember.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
				ProvisoryContentAssessment provisoryContentAssessment = (ProvisoryContentAssessment)assessment;
				assert provisoryContentAssessment.ancestorAssessment.isEmpty();
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
				private final SomeInterface someInterface = null;
			}

			static final class ClassExtendingProvisoryClass extends ProvisoryClass
			{ }

			@Override public void run()
			{
				assert assess( newAssessor(), ProvisoryClass.class ) instanceof ProvisoryContentAssessment;
				TypeAssessment assessment = assess( assessor, ClassExtendingProvisoryClass.class );
				assert assessment instanceof ProvisoryContentAssessment : assessment;
				ProvisoryContentAssessment provisoryContentAssessment = (ProvisoryContentAssessment)assessment;
				assert provisoryContentAssessment.ancestorAssessment.isPresent();
			}
		}.run();
	}

	@Test public void enums_are_normally_immutable()
	{
		new Runnable()
		{
			enum E
			{
				A //this is essentially a static field, so it does not affect the assessment.
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, E.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
			}
		}.run();
	}

	@Test public void enums_with_variable_fields_are_mutable()
	{
		new Runnable()
		{
			enum EnumWithVariableField
			{
				A;
				int a;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, EnumWithVariableField.class );
				assert assessment instanceof MutableFieldsAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_array_field_is_mutable()
	{
		new Runnable()
		{
			static final class ClassWithArrayField
			{
				public final int[] array = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithArrayField.class );
				assert assessment instanceof MutableFieldsAssessment : assessment;
			}
		}.run();
	}

	@Test public void class_with_invariable_array_field_of_circular_reference_element_type_is_immutable()
	{
		new Runnable()
		{
			static final class ClassWithInvariableArrayFieldOfCircularReferenceElementType
			{
				@InvariableArray private final ClassWithInvariableArrayFieldOfCircularReferenceElementType[] array = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableArrayFieldOfCircularReferenceElementType.class );
				assert assessment instanceof ImmutableTypeAssessment : assessment;
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
				@InvariableArray private final ArrayList<Integer>[] arrayOfMutableElements = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, ClassWithInvariableArrayInvariableFieldOfMutableElementType.class );
				assert assessment instanceof MutableTypeAssessment;
			}
		}.run();
	}

	@Test public void mutable_self_assessable_class_is_self_assessable_provisory()
	{
		new Runnable()
		{
			static final class MutableSelfAssessableClass implements ImmutabilitySelfAssessable
			{
				public int mutableField;
				@Override public boolean isImmutable() { throw new AssertionError(); /* we do not expect this to be invoked. */ }
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, MutableSelfAssessableClass.class );
				assert assessment instanceof SelfAssessableAssessment;
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
				public final DerivedClass derivedClassField = null;
			}

			@Override public void run()
			{
				TypeAssessment assessment = assess( assessor, SuperClass.class );
				assert assessment instanceof ExtensibleAssessment;
			}
		}.run();
	}

	private static final class PrimitiveInfo<T>
	{
		final Class<T> primitiveClass;
		final Class<T> wrapperClass;

		private PrimitiveInfo( Class<T> primitiveClass, Class<T> wrapperClass )
		{
			this.primitiveClass = primitiveClass;
			this.wrapperClass = wrapperClass;
		}
	}

	private static final List<PrimitiveInfo<?>> primitiveTypeInfo = List.of( //
		new PrimitiveInfo<>( boolean.class /**/, Boolean.class ), //
		new PrimitiveInfo<>( char.class    /**/, Character.class ), //
		new PrimitiveInfo<>( byte.class    /**/, Byte.class ), //
		new PrimitiveInfo<>( short.class   /**/, Short.class ), //
		new PrimitiveInfo<>( int.class     /**/, Integer.class ), //
		new PrimitiveInfo<>( long.class    /**/, Long.class ), //
		new PrimitiveInfo<>( float.class   /**/, Float.class ), //
		new PrimitiveInfo<>( double.class  /**/, Double.class ), //
		new PrimitiveInfo<>( void.class    /**/, Void.class ) );

	/**
	 * Gets all java primitive types.
	 */
	public static Collection<Class<?>> getAllPrimitives()
	{
		return primitiveTypeInfo.stream().<Class<?>>map( i -> i.primitiveClass ).toList();
	}

	/**
	 * Gets all java primitive wrappers.
	 */
	public static Collection<Class<?>> getAllPrimitiveWrappers()
	{
		return primitiveTypeInfo.stream().<Class<?>>map( i -> i.wrapperClass ).toList();
	}
}