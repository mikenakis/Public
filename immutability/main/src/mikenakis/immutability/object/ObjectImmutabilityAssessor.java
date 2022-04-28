package mikenakis.immutability.object;

import mikenakis.immutability.helpers.Stringizable;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.object.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.object.assessments.ObjectAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableComponentAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableElementAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableFieldValuesAssessment;
import mikenakis.immutability.object.assessments.mutable.MutableSelfAssessment;
import mikenakis.immutability.object.assessments.mutable.OfMutableTypeAssessment;
import mikenakis.immutability.object.exceptions.ObjectMustBeImmutableException;
import mikenakis.immutability.object.fieldvalue.MutableFieldValueAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.IterableAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryCompositeAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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
			throw new ObjectMustBeImmutableException( mutableObjectAssessment );
		assert assessment instanceof ImmutableObjectAssessment;
		return true;
	}

	public ObjectAssessment assess( Object object )
	{
		Set<Object> visitedValues = new LinkedHashSet<>();
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
		TypeAssessment assessment = typeImmutabilityAssessor.assess( declaredClass );
		return switch( assessment )
			{
				case MutableTypeAssessment mutableTypeAssessment -> //
					new OfMutableTypeAssessment( stringizer, object, mutableTypeAssessment );
				case ImmutableTypeAssessment ignore -> //
					immutableObjectAssessmentInstance;
				case ExtensibleAssessment ignore -> //
					//Class is extensible but otherwise immutable, and object is of this exact class and not of a further derived class, so object is immutable.
					immutableObjectAssessmentInstance;
				case ProvisoryCompositeAssessment<?,?> provisoryCompositeAssessment -> //
					assessComposite0( object, provisoryCompositeAssessment );
				case IterableAssessment iterableAssessment -> //
					assessIterable( (Iterable<?>)object, iterableAssessment );
				case SelfAssessableAssessment selfAssessableAssessment -> //
					assessSelfAssessable( selfAssessableAssessment, (ImmutabilitySelfAssessable)object );
				case ProvisoryContentAssessment provisoryContentAssessment -> //
					assessProvisoryContent( object, provisoryContentAssessment, visitedValues );
				default -> //
					throw new AssertionError( assessment );
			};
	}

	private <C> ObjectAssessment assessIterable( Iterable<C> iterableObject, IterableAssessment typeAssessment )
	{
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableElementAssessment<>( stringizer, iterableObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
			index++;
		}
		return immutableObjectAssessmentInstance;
	}

	private <T,E> ObjectAssessment assessComposite0( T compositeObject, ProvisoryCompositeAssessment<?,?> typeAssessment )
	{
		@SuppressWarnings( "unchecked" ) ProvisoryCompositeAssessment<T,E> castAssessment = (ProvisoryCompositeAssessment<T,E>)typeAssessment;
		return assessComposite( compositeObject, castAssessment );
	}

	private <T,E> ObjectAssessment assessComposite( T compositeObject, ProvisoryCompositeAssessment<T,E> typeAssessment )
	{
		Iterable<E> iterableObject = typeAssessment.decomposer.decompose( compositeObject );
		int index = 0;
		for( var element : iterableObject )
		{
			ObjectAssessment elementAssessment = assess( element );
			if( elementAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
				return new MutableComponentAssessment<>( stringizer, compositeObject, typeAssessment, index, element, mutableObjectAssessment );
			assert elementAssessment instanceof ImmutableObjectAssessment;
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
		collectMutableFieldValueAssessmentsRecursively( mutableFieldValueAssessments, object, typeAssessment, visitedValues );
		if( mutableFieldValueAssessments.isEmpty() )
			return immutableObjectAssessmentInstance;
		return new MutableFieldValuesAssessment( stringizer, object, typeAssessment, mutableFieldValueAssessments );
	}

	private void collectMutableFieldValueAssessmentsRecursively( List<MutableFieldValueAssessment> fieldValueAssessments, Object object, //
		ProvisoryContentAssessment typeAssessment, Set<Object> visitedValues )
	{
		for( ProvisoryFieldAssessment provisoryFieldAssessment : typeAssessment.fieldAssessments )
		{
			Field field = provisoryFieldAssessment.field;
			field.setAccessible( true );
			Object fieldValue;
			try
			{
				fieldValue = field.get( object );
			}
			catch( IllegalAccessException e )
			{
				throw new RuntimeException( e );
			}
			ObjectAssessment objectAssessment = assessRecursively( fieldValue, visitedValues );
			if( objectAssessment == null )
				continue;
			if( objectAssessment instanceof MutableObjectAssessment mutableObjectAssessment )
			{
				MutableFieldValueAssessment fieldValueAssessment = new MutableFieldValueAssessment( stringizer, provisoryFieldAssessment, fieldValue, mutableObjectAssessment );
				fieldValueAssessments.add( fieldValueAssessment );
				continue;
			}
			assert objectAssessment instanceof ImmutableObjectAssessment;
		}
		typeAssessment.ancestorAssessment.ifPresent( ancestorAssessment -> //
			collectMutableFieldValueAssessmentsRecursively( fieldValueAssessments, object, ancestorAssessment, visitedValues ) );
	}
}
