package mikenakis.immutability.type;

import mikenakis.immutability.helpers.Helpers;
import mikenakis.immutability.helpers.Stringizable;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.NonImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableArrayAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableFieldsAssessment;
import mikenakis.immutability.type.assessments.mutable.MutableSuperclassAssessment;
import mikenakis.immutability.type.assessments.provisory.ExtensibleAssessment;
import mikenakis.immutability.type.assessments.provisory.InterfaceAssessment;
import mikenakis.immutability.type.assessments.provisory.ProvisoryContentAssessment;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;
import mikenakis.immutability.type.exceptions.SelfAssessableAnnotationIsOnlyApplicableToClassException;
import mikenakis.immutability.type.exceptions.SelfAssessableClassMustBeNonImmutableException;
import mikenakis.immutability.type.field.FieldImmutabilityAssessor;
import mikenakis.immutability.type.field.assessments.FieldAssessment;
import mikenakis.immutability.type.field.assessments.ImmutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.UnderAssessmentFieldAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Assesses immutability using reflection.
 *
 * @author michael.gr
 */
final class Reflector extends Stringizable
{
	private final TypeImmutabilityAssessor typeImmutabilityAssessor;
	private final FieldImmutabilityAssessor fieldImmutabilityAssessor;

	Reflector( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		super( typeImmutabilityAssessor.stringizer );
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
		fieldImmutabilityAssessor = new FieldImmutabilityAssessor( typeImmutabilityAssessor );
	}

	TypeAssessment assess( Class<?> type )
	{
		TypeAssessment assessment = assess0( type );
		if( ImmutabilitySelfAssessable.class.isAssignableFrom( type ) )
		{
			assert Helpers.isClass( type ) : new SelfAssessableAnnotationIsOnlyApplicableToClassException( type );
			//FIXME XXX TODO assert !Helpers.isExtensible( type  ) : new SelfAssessableClassMustBeInextensibleException( type );
			assert assessment instanceof NonImmutableTypeAssessment : new SelfAssessableClassMustBeNonImmutableException( type );
			return new SelfAssessableAssessment( stringizer, type );
		}
		return assessment;
	}

	private TypeAssessment assess0( Class<?> type )
	{
		if( type.isArray() )
			return new MutableArrayAssessment( stringizer, type );
		if( type.isInterface() )
			return new InterfaceAssessment( stringizer, type );

		Optional<ProvisoryTypeAssessment> provisorySuperclassAssessment = Optional.empty();
		Class<?> superclass = type.getSuperclass();
		if( superclass != null )
		{
			TypeAssessment superclassAssessment = typeImmutabilityAssessor.assess( superclass );
			switch( superclassAssessment )
			{
				case MutableTypeAssessment mutableTypeAssessment:
					return new MutableSuperclassAssessment( stringizer, type, mutableTypeAssessment );
				case ExtensibleAssessment ignore:
					//This means that the supertype is immutable in all aspects except that it is extensible, so the supertype is not preventing us from being immutable.
					break;
				case ProvisoryContentAssessment provisoryContentAssessment:
					provisorySuperclassAssessment = Optional.of( provisoryContentAssessment );
					break;
				case UnderAssessmentTypeAssessment ignore:
					break;
				case ImmutableTypeAssessment immutableTypeAssessment:
					//Cannot happen, because the superclass has obviously been extended, so it is extensible, so it can be either provisory or mutable, but not immutable.
					//DoNotCover
					throw new AssertionError( immutableTypeAssessment );
				case InterfaceAssessment interfaceAssessment:
					//Cannot happen, because the supertype of a class cannot be an interface.
					//DoNotCover
					throw new AssertionError( interfaceAssessment );
				case SelfAssessableAssessment selfAssessableAssessment:
					provisorySuperclassAssessment = Optional.of( selfAssessableAssessment );
					break;
//					//Cannot happen, because self-assessable objects are required to be inextensible, so there can be no self-assessable superclass.
//					//DoNotCover
//					throw new AssertionError( selfAssessableAssessment );
				default:
					//DoNotCover
					throw new AssertionError( superclassAssessment );
			}
		}

		List<ProvisoryFieldAssessment> provisoryFieldAssessments = new ArrayList<>();
		List<MutableFieldAssessment> mutableFieldAssessments = new ArrayList<>();
		for( Field field : type.getDeclaredFields() )
		{
			FieldAssessment fieldAssessment = fieldImmutabilityAssessor.assessField( field );
			switch( fieldAssessment )
			{
				case null:
					break;
				case UnderAssessmentFieldAssessment ignore:
					break;
				case ImmutableFieldAssessment ignore:
					break;
				case ProvisoryFieldAssessment provisoryFieldAssessment:
				{
					provisoryFieldAssessments.add( provisoryFieldAssessment );
					break;
				}
				case MutableFieldAssessment mutableFieldAssessment:
				{
					mutableFieldAssessments.add( mutableFieldAssessment );
					break;
				}
				default:
					//DoNotCover
					throw new AssertionError( fieldAssessment );
			}
		}

		if( !mutableFieldAssessments.isEmpty() )
			return new MutableFieldsAssessment( stringizer, type, mutableFieldAssessments );

		if( provisorySuperclassAssessment.isPresent() || !provisoryFieldAssessments.isEmpty() )
			return new ProvisoryContentAssessment( stringizer, type, provisorySuperclassAssessment, provisoryFieldAssessments );

		if( Helpers.isExtensible( type ) )
			return new ExtensibleAssessment( stringizer, TypeAssessment.Mode.Assessed, type );

		return typeImmutabilityAssessor.immutableClassAssessmentInstance;
	}
}
