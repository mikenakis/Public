package mikenakis.immutability.object;

import mikenakis.immutability.internal.helpers.IterableOnArrayObject;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.collections.IdentityLinkedHashSet;
import mikenakis.immutability.object.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.object.assessments.ObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableArrayElementAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableComponentElementAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableFieldValuesAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableIterableElementAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableSelfAssessment;
import mikenakis.immutability.object.assessments.mutable.NonEmptyMutableArrayAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeAssessment;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.object.fieldvalue.MutableFieldValueAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableArrayAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.IterableAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryCompositeAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import mikenakis.immutability.type.field.assessments.provisory.InvariableArrayFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldTypeAssessment;

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
	public final ImmutableObjectAssessment immutableObjectAssessmentInstance = new ImmutableObjectAssessment( stringizer );

	public ObjectImmutabilityAssessor( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		super( typeImmutabilityAssessor.stringizer );
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
	}

	public boolean mustBeImmutableAssertion( Object object )
	{
		ObjectAssessment assessment = assess( object );
		if( assessment instanceof MutableObjectAssessment mutableObjectAssessment )
		{
			mutableObjectAssessment.assessmentTextLines().forEach( line -> System.out.println( line ) );
			throw new ObjectMustBeImmutableException( mutableObjectAssessment );
		}
		assert assessment instanceof ImmutableObjectAssessment;
		return true;
	}

	public ObjectAssessment assess( Object object )
	{
		Set<Object> visitedValues = new IdentityLinkedHashSet<>();
		ObjectAssessment result = assessRecursively( object, visitedValues );
		assert result != null;
		return result;
	}

	private <T> ObjectAssessment assessRecursively( T object, Set<Object> visitedValues )
	{
		if( object == null )
			return immutableObjectAssessmentInstance;
		if( visitedValues.contains( object ) )
			return null;
		visitedValues.add( object );
		Class<T> declaredClass = MyKit.getClass( object );
		TypeAssessment typeAssessment = typeImmutabilityAssessor.assess( declaredClass );
		return switch( typeAssessment )
			{
				case ImmutableTypeAssessment ignore -> immutableObjectAssessmentInstance;
				//Class is extensible but otherwise immutable, and object is of this exact class and not of a further derived class, so object is immutable.
				case ExtensibleAssessment ignore -> immutableObjectAssessmentInstance;
				case ProvisoryCompositeAssessment<?,?> provisoryCompositeAssessment -> assessComposite( object, provisoryCompositeAssessment );
				case IterableAssessment iterableAssessment -> assessIterable( (Iterable<?>)object, iterableAssessment );
				case SelfAssessableAssessment selfAssessableAssessment -> assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
				case ProvisoryContentAssessment provisoryContentAssessment -> assessProvisoryContent( object, provisoryContentAssessment, visitedValues );
				case MutableArrayAssessment arrayAssessment -> assessMutableArray( object, arrayAssessment );
				case MutableTypeAssessment mutableTypeAssessment -> new OfMutableTypeAssessment( stringizer, object, mutableTypeAssessment );
				default -> throw new AssertionError( typeAssessment );
			};
	}

	private ObjectAssessment assessMutableArray( Object arrayObject, MutableArrayAssessment mutableArrayAssessment )
	{
		if( Array.getLength( arrayObject ) == 0 )
			return immutableObjectAssessmentInstance;
		return new NonEmptyMutableArrayAssessment( stringizer, arrayObject, mutableArrayAssessment );
	}

	private <C> ObjectAssessment assessIterable( Iterable<C> iterableObject, IterableAssessment typeAssessment )
	{
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableIterableElementAssessment<>( stringizer, iterableObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private <T, E> ObjectAssessment assessComposite( T compositeObject, ProvisoryCompositeAssessment<?,?> wildcardTypeAssessment )
	{
		@SuppressWarnings( "unchecked" ) ProvisoryCompositeAssessment<T,E> typeAssessment = (ProvisoryCompositeAssessment<T,E>)wildcardTypeAssessment;
		Iterable<E> iterableObject = typeAssessment.decomposer.decompose( compositeObject );
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableComponentElementAssessment<>( stringizer, compositeObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectAssessment assessInvariableArray( Object array, Set<Object> visitedValues, InvariableArrayFieldAssessment arrayAssessment )
	{
		Iterable<Object> arrayAsIterable = new IterableOnArrayObject( array );
		int index = 0;
		for( Object element : arrayAsIterable )
		{
			ObjectAssessment elementAssessment = assessRecursively( element, visitedValues );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableArrayElementAssessment<>( stringizer, arrayAsIterable, arrayAssessment, index, element, mutableObjectAssessment );
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectAssessment assessSelfAssessable( SelfAssessableAssessment typeAssessment, ImmutabilitySelfAssessable selfAssessableObject )
	{
		if( selfAssessableObject.isImmutable() )
			return immutableObjectAssessmentInstance;
		return new MutableSelfAssessment( stringizer, typeAssessment, selfAssessableObject );
	}

	private ObjectAssessment assessProvisoryContent( Object object, ProvisoryContentAssessment typeAssessment, Set<Object> visitedValues )
	{
		List<MutableFieldValueAssessment> mutableFieldValueAssessments = new ArrayList<>();
		collectFieldValueAssessmentsRecursively( mutableFieldValueAssessments, object, typeAssessment, visitedValues );
		if( !mutableFieldValueAssessments.isEmpty() )
			return new MutableFieldValuesAssessment( stringizer, object, typeAssessment, mutableFieldValueAssessments );
		return immutableObjectAssessmentInstance;
	}

	private void collectFieldValueAssessmentsRecursively( List<MutableFieldValueAssessment> fieldValueAssessments, Object object, //
		ProvisoryContentAssessment typeAssessment, Set<Object> visitedValues )
	{
		for( ProvisoryFieldAssessment provisoryFieldAssessment : typeAssessment.fieldAssessments )
		{
			Field field = provisoryFieldAssessment.field;
			Object fieldValue = getFieldValue( object, field );
			ObjectAssessment objectAssessment = switch( provisoryFieldAssessment )
			{
				case InvariableArrayFieldAssessment invariableArrayFieldAssessment -> assessInvariableArray( fieldValue, visitedValues, invariableArrayFieldAssessment );
				case ProvisoryFieldTypeAssessment provisoryFieldTypeAssessment -> assessRecursively( fieldValue, visitedValues );
				default -> throw new AssertionError(); //TODO: make assessments sealed, so that we do not need default clauses!
			};
			if( objectAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
			{
				MutableFieldValueAssessment fieldValueAssessment = new MutableFieldValueAssessment( stringizer, provisoryFieldAssessment, fieldValue, mutableObjectAssessment );
				fieldValueAssessments.add( fieldValueAssessment );
				continue;
			}
			assert objectAssessment == null || objectAssessment instanceof ImmutableObjectAssessment;
		}
		typeAssessment.ancestorAssessment.ifPresent( ancestorAssessment -> //
		{
			switch( ancestorAssessment )
			{
				case ProvisoryContentAssessment provisoryContentAssessment: //
					collectFieldValueAssessmentsRecursively( fieldValueAssessments, object, provisoryContentAssessment, visitedValues );
					break;
				case SelfAssessableAssessment selfAssessableAssessment:
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
