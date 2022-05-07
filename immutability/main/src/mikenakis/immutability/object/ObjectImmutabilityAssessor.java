package mikenakis.immutability.object;

import mikenakis.immutability.internal.helpers.IterableOnArrayObject;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.collections.IdentityLinkedHashSet;
import mikenakis.immutability.object.assessments.ImmutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.ObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableArrayElementMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableComponentMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableFieldValuesObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableIterableElementMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.SelfAssessedMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.IsNonEmptyArrayMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeMutableObjectImmutabilityAssessment;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.object.fieldvalue.MutableFieldValueAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.IsArrayMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsExtensibleProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsIterableProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsCompositeProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryContentProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.IsInvariableArrayProvisoryFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.OfProvisoryTypeProvisoryFieldImmutabilityAssessment;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Assesses the immutability of objects.
 *
 * @author michael.gr
 */
public final class ObjectImmutabilityAssessor extends Stringizable
{
	public static final ObjectImmutabilityAssessor instance = new ObjectImmutabilityAssessor( TypeImmutabilityAssessor.instance );

	public final TypeImmutabilityAssessor typeImmutabilityAssessor;
	public final ImmutableObjectImmutabilityAssessment immutableObjectAssessmentInstance = new ImmutableObjectImmutabilityAssessment( stringizer );

	public ObjectImmutabilityAssessor( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		super( typeImmutabilityAssessor.stringizer );
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
	}

	public boolean mustBeImmutableAssertion( Object object )
	{
		ObjectImmutabilityAssessment assessment = assess( object );
		if( assessment instanceof MutableObjectImmutabilityAssessment mutableObjectAssessment )
		{
			mutableObjectAssessment.assessmentTextLines().forEach( line -> System.out.println( line ) );
			throw new ObjectMustBeImmutableException( mutableObjectAssessment );
		}
		assert assessment instanceof ImmutableObjectImmutabilityAssessment;
		return true;
	}

	public ObjectImmutabilityAssessment assess( Object object )
	{
		Set<Object> visitedValues = new IdentityLinkedHashSet<>();
		ObjectImmutabilityAssessment result = assessRecursively( object, visitedValues );
		assert result != null;
		return result;
	}

	private <T> ObjectImmutabilityAssessment assessRecursively( T object, Set<Object> visitedValues )
	{
		if( object == null )
			return immutableObjectAssessmentInstance;
		if( visitedValues.contains( object ) )
			return null;
		visitedValues.add( object );
		Class<T> declaredClass = MyKit.getClass( object );
		TypeImmutabilityAssessment typeAssessment = typeImmutabilityAssessor.assess( declaredClass );
		return switch( typeAssessment )
			{
				case ImmutableTypeImmutabilityAssessment ignore -> immutableObjectAssessmentInstance;
				//Class is extensible but otherwise immutable, and object is of this exact class and not of a further derived class, so object is immutable.
				case IsExtensibleProvisoryTypeImmutabilityAssessment ignore -> immutableObjectAssessmentInstance;
				case IsCompositeProvisoryTypeImmutabilityAssessment<?,?> provisoryCompositeAssessment -> assessComposite( object, provisoryCompositeAssessment );
				case IsIterableProvisoryTypeImmutabilityAssessment iterableAssessment -> assessIterable( (Iterable<?>)object, iterableAssessment );
				case IsSelfAssessableProvisoryTypeImmutabilityAssessment selfAssessableAssessment -> assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
				case HasProvisoryContentProvisoryTypeImmutabilityAssessment provisoryContentAssessment -> assessProvisoryContent( object, provisoryContentAssessment, visitedValues );
				case IsArrayMutableTypeImmutabilityAssessment arrayAssessment -> assessMutableArray( object, arrayAssessment );
				case MutableTypeImmutabilityAssessment mutableTypeAssessment -> new OfMutableTypeMutableObjectImmutabilityAssessment( stringizer, object, mutableTypeAssessment );
				default -> throw new AssertionError( typeAssessment );
			};
	}

	private ObjectImmutabilityAssessment assessMutableArray( Object arrayObject, IsArrayMutableTypeImmutabilityAssessment mutableArrayAssessment )
	{
		if( Array.getLength( arrayObject ) == 0 )
			return immutableObjectAssessmentInstance;
		return new IsNonEmptyArrayMutableObjectImmutabilityAssessment( stringizer, arrayObject, mutableArrayAssessment );
	}

	private <C> ObjectImmutabilityAssessment assessIterable( Iterable<C> iterableObject, IsIterableProvisoryTypeImmutabilityAssessment typeAssessment )
	{
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectImmutabilityAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectImmutabilityAssessment mutableObjectAssessment )
				return new HasMutableIterableElementMutableObjectImmutabilityAssessment<>( stringizer, iterableObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectImmutabilityAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private <T, E> ObjectImmutabilityAssessment assessComposite( T compositeObject, IsCompositeProvisoryTypeImmutabilityAssessment<?,?> wildcardTypeAssessment )
	{
		@SuppressWarnings( "unchecked" ) IsCompositeProvisoryTypeImmutabilityAssessment<T,E> typeAssessment = (IsCompositeProvisoryTypeImmutabilityAssessment<T,E>)wildcardTypeAssessment;
		Iterable<E> iterableObject = typeAssessment.decomposer.decompose( compositeObject );
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectImmutabilityAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectImmutabilityAssessment mutableObjectAssessment )
				return new HasMutableComponentMutableObjectImmutabilityAssessment<>( stringizer, compositeObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectImmutabilityAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectImmutabilityAssessment assessInvariableArray( Object array, Set<Object> visitedValues, IsInvariableArrayProvisoryFieldImmutabilityAssessment arrayAssessment )
	{
		Iterable<Object> arrayAsIterable = new IterableOnArrayObject( array );
		int index = 0;
		for( Object element : arrayAsIterable )
		{
			ObjectImmutabilityAssessment elementAssessment = assessRecursively( element, visitedValues );
			if( elementAssessment instanceof MutableObjectImmutabilityAssessment mutableObjectAssessment )
				return new HasMutableArrayElementMutableObjectImmutabilityAssessment<>( stringizer, arrayAsIterable, arrayAssessment, index, element, mutableObjectAssessment );
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectImmutabilityAssessment assessSelfAssessable( IsSelfAssessableProvisoryTypeImmutabilityAssessment typeAssessment, ImmutabilitySelfAssessable selfAssessableObject )
	{
		if( selfAssessableObject.isImmutable() )
			return immutableObjectAssessmentInstance;
		return new SelfAssessedMutableObjectImmutabilityAssessment( stringizer, typeAssessment, selfAssessableObject );
	}

	private ObjectImmutabilityAssessment assessProvisoryContent( Object object, HasProvisoryContentProvisoryTypeImmutabilityAssessment typeAssessment, Set<Object> visitedValues )
	{
		List<MutableFieldValueAssessment> mutableFieldValueAssessments = new ArrayList<>();
		collectFieldValueAssessmentsRecursively( mutableFieldValueAssessments, object, typeAssessment, visitedValues );
		if( !mutableFieldValueAssessments.isEmpty() )
			return new HasMutableFieldValuesObjectImmutabilityAssessment( stringizer, object, typeAssessment, mutableFieldValueAssessments );
		return immutableObjectAssessmentInstance;
	}

	private void collectFieldValueAssessmentsRecursively( List<MutableFieldValueAssessment> fieldValueAssessments, Object object, //
		HasProvisoryContentProvisoryTypeImmutabilityAssessment typeAssessment, Set<Object> visitedValues )
	{
		for( ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment : typeAssessment.fieldAssessments )
		{
			Field field = provisoryFieldAssessment.field;
			Object fieldValue = getFieldValue( object, field );
			ObjectImmutabilityAssessment objectAssessment = switch( provisoryFieldAssessment )
			{
				case IsInvariableArrayProvisoryFieldImmutabilityAssessment invariableArrayFieldAssessment -> assessInvariableArray( fieldValue, visitedValues, invariableArrayFieldAssessment );
				case OfProvisoryTypeProvisoryFieldImmutabilityAssessment provisoryFieldTypeAssessment -> assessRecursively( fieldValue, visitedValues );
				default -> throw new AssertionError(); //TODO: make assessments sealed, so that we do not need default clauses!
			};
			if( objectAssessment instanceof MutableObjectImmutabilityAssessment mutableObjectAssessment )
			{
				MutableFieldValueAssessment fieldValueAssessment = new MutableFieldValueAssessment( stringizer, provisoryFieldAssessment, fieldValue, mutableObjectAssessment );
				fieldValueAssessments.add( fieldValueAssessment );
				continue;
			}
			assert objectAssessment == null || objectAssessment instanceof ImmutableObjectImmutabilityAssessment;
		}
		typeAssessment.ancestorAssessment.ifPresent( ancestorAssessment -> //
		{
			switch( ancestorAssessment )
			{
				case HasProvisoryContentProvisoryTypeImmutabilityAssessment provisoryContentAssessment: //
					collectFieldValueAssessmentsRecursively( fieldValueAssessments, object, provisoryContentAssessment, visitedValues );
					break;
				case IsSelfAssessableProvisoryTypeImmutabilityAssessment selfAssessableAssessment:
					assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
					break;
				default:
					throw new AssertionError();
			}
		} );
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
