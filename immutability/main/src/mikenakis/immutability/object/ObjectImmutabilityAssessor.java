package mikenakis.immutability.object;

import mikenakis.immutability.internal.helpers.IterableOnArrayObject;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.mykit.collections.IdentityLinkedHashSet;
import mikenakis.immutability.object.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.object.assessments.ObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableArrayElementMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableComponentMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.MultiReasonMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.HasMutableIterableElementMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.IsNonEmptyArrayMutableObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeMutableObjectAssessment;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.object.assessments.mutable.HasMutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.mutable.IsArrayMutableTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.IsExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.IsIterableProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.IsCompositeProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryAncestorProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.type.field.assessments.provisory.IsInvariableArrayProvisoryFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.OfProvisoryTypeProvisoryFieldAssessment;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
		return assessRecursively( object, typeAssessment, visitedValues );
	}

	private <T> ObjectAssessment assessRecursively( T object, TypeAssessment typeAssessment, Set<Object> visitedValues )
	{
		return switch( typeAssessment )
			{
				case ImmutableTypeAssessment ignore -> immutableObjectAssessmentInstance;
				//Class is extensible but otherwise immutable, and object is of this exact class and not of a further derived class, so object is immutable.
				case IsExtensibleProvisoryTypeAssessment ignore -> immutableObjectAssessmentInstance;
				case IsCompositeProvisoryTypeAssessment<?,?> provisoryCompositeAssessment -> assessComposite( object, provisoryCompositeAssessment );
				case IsIterableProvisoryTypeAssessment iterableAssessment -> assessIterable( (Iterable<?>)object, iterableAssessment );
				case IsSelfAssessableProvisoryTypeAssessment selfAssessableAssessment -> assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
				case MultiReasonProvisoryTypeAssessment multiReasonAssessment -> assessMultiReasonProvisoryType( object, multiReasonAssessment, visitedValues );
				case IsArrayMutableTypeAssessment arrayAssessment -> assessMutableArray( object, arrayAssessment );
				case MutableTypeAssessment mutableTypeAssessment -> new OfMutableTypeMutableObjectAssessment( stringizer, object, mutableTypeAssessment );
				default -> throw new AssertionError( typeAssessment );
			};
	}

	private ObjectAssessment assessMutableArray( Object arrayObject, IsArrayMutableTypeAssessment mutableArrayAssessment )
	{
		if( Array.getLength( arrayObject ) == 0 )
			return immutableObjectAssessmentInstance;
		return new IsNonEmptyArrayMutableObjectAssessment( stringizer, arrayObject, mutableArrayAssessment );
	}

	private <C> ObjectAssessment assessIterable( Iterable<C> iterableObject, IsIterableProvisoryTypeAssessment typeAssessment )
	{
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new HasMutableIterableElementMutableObjectAssessment<>( stringizer, iterableObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private <T, E> ObjectAssessment assessComposite( T compositeObject, IsCompositeProvisoryTypeAssessment<?,?> wildcardTypeAssessment )
	{
		@SuppressWarnings( "unchecked" ) IsCompositeProvisoryTypeAssessment<T,E> typeAssessment = (IsCompositeProvisoryTypeAssessment<T,E>)wildcardTypeAssessment;
		Iterable<E> iterableObject = typeAssessment.decomposer.decompose( compositeObject );
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new HasMutableComponentMutableObjectAssessment<>( stringizer, compositeObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectAssessment assessInvariableArray( Object array, IsInvariableArrayProvisoryFieldAssessment arrayAssessment, Set<Object> visitedValues )
	{
		Iterable<Object> arrayAsIterable = new IterableOnArrayObject( array );
		int index = 0;
		for( Object element : arrayAsIterable )
		{
			ObjectAssessment elementAssessment = assessRecursively( element, visitedValues );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new HasMutableArrayElementMutableObjectAssessment<>( stringizer, arrayAsIterable, arrayAssessment, index, element, mutableObjectAssessment );
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private ObjectAssessment assessSelfAssessable( IsSelfAssessableProvisoryTypeAssessment typeAssessment, ImmutabilitySelfAssessable selfAssessableObject )
	{
		if( selfAssessableObject.isImmutable() )
			return immutableObjectAssessmentInstance;
		return new SelfAssessedMutableObjectAssessment( stringizer, typeAssessment, selfAssessableObject );
	}

	private ObjectAssessment assessMultiReasonProvisoryType( Object object, MultiReasonProvisoryTypeAssessment multiReasonProvisoryTypeAssessment, Set<Object> visitedValues )
	{
		List<MutableObjectAssessment> assessments = new ArrayList<>();
		for( ProvisoryTypeAssessment reason : multiReasonProvisoryTypeAssessment.reasons )
		{
			Optional<MutableObjectAssessment> reasonMutableObjectAssessment = switch( reason )
			{
				case HasProvisoryAncestorProvisoryTypeAssessment hasProvisoryAncestorReasonAssessment -> assessAncestor( object, hasProvisoryAncestorReasonAssessment.ancestorAssessment, visitedValues );
				case HasProvisoryFieldProvisoryTypeAssessment hasProvisoryFieldReasonAssessment -> assessField( object, hasProvisoryFieldReasonAssessment.fieldAssessment, visitedValues );
				default -> throw new IllegalStateException( "Unexpected value: " + reason );
			};
			reasonMutableObjectAssessment.ifPresent( r -> assessments.add( r ) );
		}
		if( !assessments.isEmpty() )
			return new MultiReasonMutableObjectAssessment( stringizer, object, multiReasonProvisoryTypeAssessment, assessments );
		return immutableObjectAssessmentInstance;
	}

	private Optional<MutableObjectAssessment> assessAncestor( Object object, ProvisoryTypeAssessment ancestorTypeAssessment,
		Set<Object> visitedValues )
	{
		ObjectAssessment ancestorObjectAssessment = assessRecursively( object, ancestorTypeAssessment, visitedValues );
		if( ancestorObjectAssessment instanceof MutableObjectAssessment mutableAncestorAssessment )
			return Optional.of( mutableAncestorAssessment );
		assert ancestorObjectAssessment instanceof ImmutableObjectAssessment;
		return Optional.empty();
	}

	private Optional<MutableObjectAssessment> assessField( Object object, ProvisoryFieldAssessment provisoryFieldAssessment,
		Set<Object> visitedValues )
	{
		Object fieldValue = getFieldValue( object, provisoryFieldAssessment.field );
		ObjectAssessment fieldValueAssessment = switch( provisoryFieldAssessment )
		{
			case IsInvariableArrayProvisoryFieldAssessment invariableArrayFieldAssessment -> assessInvariableArray( fieldValue, invariableArrayFieldAssessment, visitedValues );
			case OfProvisoryTypeProvisoryFieldAssessment ignore -> assessRecursively( fieldValue, visitedValues );
			default -> throw new AssertionError(); //TODO: make assessments sealed, so that we do not need default clauses!
		};
		if( fieldValueAssessment instanceof MutableObjectAssessment mutableFieldValueAssessment )
			return Optional.of( new HasMutableFieldValueMutableObjectAssessment( stringizer, object, provisoryFieldAssessment, mutableFieldValueAssessment ) );
		assert fieldValueAssessment == null || fieldValueAssessment instanceof ImmutableObjectAssessment;
		return Optional.empty();
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
