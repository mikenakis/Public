package mikenakis.immutability;

import mikenakis.immutability.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableArrayElementMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableClassMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableComponentMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableSuperclassMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.NonEmptyArrayMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.internal.helpers.IterableOnArrayObject;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.collections.IdentityLinkedHashSet;
import mikenakis.immutability.internal.type.TypeImmutabilityAssessor;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.ArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.CompositeProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisorySuperclassProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldTypeProvisoryFieldAssessment;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * Assesses the immutability of objects.
 *
 * @author michael.gr
 */
public final class ObjectImmutabilityAssessor
{
	public static final ObjectImmutabilityAssessor instance = new ObjectImmutabilityAssessor();

	private final TypeImmutabilityAssessor typeImmutabilityAssessor = TypeImmutabilityAssessor.create();

	private ObjectImmutabilityAssessor()
	{
	}

	public void addImmutablePreassessment( Class<?> jvmClass )
	{
		typeImmutabilityAssessor.addImmutablePreassessment( jvmClass );
	}

	public boolean mustBeImmutableAssertion( Object object )
	{
		Set<Object> visitedValues = new IdentityLinkedHashSet<>();
		ObjectAssessment assessment = assessRecursively( object, visitedValues );
		if( assessment instanceof MutableObjectAssessment mutableObjectAssessment )
			throw new ObjectMustBeImmutableException( mutableObjectAssessment );
		assert assessment instanceof ImmutableObjectAssessment;
		return true;
	}

	private <T> ObjectAssessment assessRecursively( T object, Set<Object> visitedValues )
	{
		if( object == null )
			return ImmutableObjectAssessment.instance;
		if( visitedValues.contains( object ) )
			return ImmutableObjectAssessment.instance;
		visitedValues.add( object );
		Class<T> declaredClass = MyKit.getClass( object );
		TypeAssessment typeAssessment = typeImmutabilityAssessor.assess( declaredClass );
		return assessRecursively( object, typeAssessment, visitedValues );
	}

	private <T> ObjectAssessment assessRecursively( T object, TypeAssessment typeAssessment, Set<Object> visitedValues )
	{
		return switch( typeAssessment )
			{
				case ImmutableTypeAssessment ignore -> ImmutableObjectAssessment.instance;
				//Class is extensible but otherwise immutable, and object is of this exact class and not of a further derived class, so object is immutable.
				case ExtensibleProvisoryTypeAssessment ignore -> ImmutableObjectAssessment.instance;
				case CompositeProvisoryTypeAssessment<?,?> provisoryCompositeAssessment ->
					assessComposite( object, provisoryCompositeAssessment, visitedValues );
				case SelfAssessableProvisoryTypeAssessment selfAssessableAssessment ->
					assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
				case MultiReasonProvisoryTypeAssessment multiReasonAssessment -> assessMultiReason( object, multiReasonAssessment, visitedValues );
				case ProvisorySuperclassProvisoryTypeAssessment provisorySuperclassAssessment ->
					assessSuperclass( object, provisorySuperclassAssessment, provisorySuperclassAssessment.superclassAssessment, visitedValues );
				case ProvisoryFieldProvisoryTypeAssessment provisoryFieldAssessment ->
					assessField( object, provisoryFieldAssessment, provisoryFieldAssessment.fieldAssessment, visitedValues );
				case ArrayMutableTypeAssessment arrayAssessment -> assessArray( object, arrayAssessment );
				case MutableTypeAssessment mutableTypeAssessment -> new MutableClassMutableObjectAssessment( object, mutableTypeAssessment );
				default -> throw new AssertionError( typeAssessment );
			};
	}

	private ObjectAssessment assessArray( Object arrayObject, ArrayMutableTypeAssessment mutableArrayAssessment )
	{
		if( Array.getLength( arrayObject ) == 0 )
			return ImmutableObjectAssessment.instance;
		return new NonEmptyArrayMutableObjectAssessment( arrayObject, mutableArrayAssessment );
	}

	private <T, E> ObjectAssessment assessComposite( T compositeObject, CompositeProvisoryTypeAssessment<?,?> wildcardTypeAssessment, Set<Object> visitedValues )
	{
		@SuppressWarnings( "unchecked" ) CompositeProvisoryTypeAssessment<T,E> typeAssessment = (CompositeProvisoryTypeAssessment<T,E>)wildcardTypeAssessment;
		Iterable<E> iterableObject = typeAssessment.decomposer.decompose( compositeObject );
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assessRecursively( element, visitedValues );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableComponentMutableObjectAssessment<>( compositeObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return ImmutableObjectAssessment.instance;
	}

	private ObjectAssessment assessInvariableArray( Object array, InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment arrayAssessment, Set<Object> visitedValues )
	{
		Iterable<Object> arrayAsIterable = new IterableOnArrayObject( array );
		int index = 0;
		for( Object element : arrayAsIterable )
		{
			ObjectAssessment elementAssessment = assessRecursively( element, visitedValues );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableArrayElementMutableObjectAssessment<>( arrayAsIterable, arrayAssessment, index, element, mutableObjectAssessment );
			index++;
		}
		return ImmutableObjectAssessment.instance;
	}

	private static ObjectAssessment assessSelfAssessable( SelfAssessableProvisoryTypeAssessment typeAssessment, ImmutabilitySelfAssessable selfAssessableObject )
	{
		if( selfAssessableObject.isImmutable() )
			return ImmutableObjectAssessment.instance;
		return new SelfAssessedMutableObjectAssessment( typeAssessment, selfAssessableObject );
	}

	private ObjectAssessment assessMultiReason( Object object, MultiReasonProvisoryTypeAssessment multiReasonProvisoryTypeAssessment, Set<Object> visitedValues )
	{
		for( ProvisoryTypeAssessment provisoryReason : multiReasonProvisoryTypeAssessment.provisoryReasons )
		{
			ObjectAssessment objectAssessment = switch( provisoryReason )
				{
					case ProvisorySuperclassProvisoryTypeAssessment provisorySuperclassAssessment ->
						assessSuperclass( object, multiReasonProvisoryTypeAssessment, provisorySuperclassAssessment.superclassAssessment, visitedValues );
					case ProvisoryFieldProvisoryTypeAssessment provisoryFieldAssessment ->
						assessField( object, multiReasonProvisoryTypeAssessment, provisoryFieldAssessment.fieldAssessment, visitedValues );
					default -> throw new AssertionError( provisoryReason );
				};
			if( !(objectAssessment instanceof ImmutableObjectAssessment) )
				return objectAssessment;
		}
		return ImmutableObjectAssessment.instance;
	}

	private ObjectAssessment assessSuperclass( Object object, ProvisoryTypeAssessment provisoryTypeAssessment, ProvisoryTypeAssessment superTypeAssessment, Set<Object> visitedValues )
	{
		ObjectAssessment superObjectAssessment = assessRecursively( object, superTypeAssessment, visitedValues );
		if( superObjectAssessment instanceof MutableObjectAssessment mutableSuperObjectAssessment )
			return new MutableSuperclassMutableObjectAssessment( object, provisoryTypeAssessment, mutableSuperObjectAssessment );
		assert superObjectAssessment instanceof ImmutableObjectAssessment;
		return superObjectAssessment;
	}

	private ObjectAssessment assessField( Object object, ProvisoryTypeAssessment provisoryTypeAssessment, ProvisoryFieldAssessment provisoryFieldAssessment, Set<Object> visitedValues )
	{
		Object fieldValue = getFieldValue( object, provisoryFieldAssessment.field );
		ObjectAssessment fieldValueAssessment = switch( provisoryFieldAssessment )
			{
				case InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment invariableArrayFieldAssessment ->
					assessInvariableArray( fieldValue, invariableArrayFieldAssessment, visitedValues );
				case ProvisoryFieldTypeProvisoryFieldAssessment ignore -> assessRecursively( fieldValue, visitedValues );
				default -> throw new AssertionError(); //TODO: make assessments sealed, so that we do not need default clauses!
			};
		if( fieldValueAssessment instanceof MutableObjectAssessment mutableFieldValueAssessment )
			return new MutableFieldValueMutableObjectAssessment( object, provisoryTypeAssessment, provisoryFieldAssessment, mutableFieldValueAssessment );
		assert fieldValueAssessment instanceof ImmutableObjectAssessment;
		return ImmutableObjectAssessment.instance;
	}

	private static Object getFieldValue( Object object, Field field )
	{
		if( !field.canAccess( object ) ) //TODO: assess whether performing this check saves any time (as opposed to always invoking setAccessible without the check.)
			field.setAccessible( true );
		assert field.canAccess( object );
		Object fieldValue;
		try
		{
			fieldValue = field.get( object );
		}
		catch( IllegalAccessException e )
		{
			throw new RuntimeException( e );
		}
		return fieldValue;
	}
}
