package mikenakis.immutability.type;

import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.NonImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.IsArrayMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.HasMutableFieldsMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.mutable.HasMutableSuperclassMutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsExtensibleProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsInterfaceProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.HasProvisoryContentProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.provisory.IsSelfAssessableProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.exceptions.SelfAssessableAnnotationIsOnlyApplicableToClassException;
import mikenakis.immutability.type.exceptions.SelfAssessableClassMustBeNonImmutableException;
import mikenakis.immutability.type.field.FieldImmutabilityAssessor;
import mikenakis.immutability.type.field.assessments.FieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.ImmutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.UnderAssessmentFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;

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

	TypeImmutabilityAssessment assess( Class<?> type )
	{
		TypeImmutabilityAssessment assessment = assess0( type );
		if( ImmutabilitySelfAssessable.class.isAssignableFrom( type ) )
		{
			assert Helpers.isClass( type ) : new SelfAssessableAnnotationIsOnlyApplicableToClassException( type );
			//FIXME XXX TODO assert !Helpers.isExtensible( type  ) : new SelfAssessableClassMustBeInextensibleException( type );
			assert assessment instanceof NonImmutableTypeImmutabilityAssessment : new SelfAssessableClassMustBeNonImmutableException( type );
			return new IsSelfAssessableProvisoryTypeImmutabilityAssessment( stringizer, type );
		}
		return assessment;
	}

	private TypeImmutabilityAssessment assess0( Class<?> type )
	{
		if( type.isArray() )
			return new IsArrayMutableTypeImmutabilityAssessment( stringizer, type );
		if( type.isInterface() )
			return new IsInterfaceProvisoryTypeImmutabilityAssessment( stringizer, type );

		Optional<ProvisoryTypeImmutabilityAssessment> provisorySuperclassAssessment = Optional.empty();
		Class<?> superclass = type.getSuperclass();
		if( superclass != null )
		{
			TypeImmutabilityAssessment superclassAssessment = typeImmutabilityAssessor.assess( superclass );
			switch( superclassAssessment )
			{
				case MutableTypeImmutabilityAssessment mutableTypeAssessment:
					return new HasMutableSuperclassMutableTypeImmutabilityAssessment( stringizer, type, mutableTypeAssessment );
				case IsExtensibleProvisoryTypeImmutabilityAssessment ignore:
					//This means that the supertype is immutable in all aspects except that it is extensible, so the supertype is not preventing us from being immutable.
					break;
				case HasProvisoryContentProvisoryTypeImmutabilityAssessment provisoryContentAssessment:
					provisorySuperclassAssessment = Optional.of( provisoryContentAssessment );
					break;
				case UnderAssessmentTypeImmutabilityAssessment ignore:
					break;
				case ImmutableTypeImmutabilityAssessment immutableTypeAssessment:
					//Cannot happen, because the superclass has obviously been extended, so it is extensible, so it can be either provisory or mutable, but not immutable.
					//DoNotCover
					throw new AssertionError( immutableTypeAssessment );
				case IsInterfaceProvisoryTypeImmutabilityAssessment interfaceAssessment:
					//Cannot happen, because the supertype of a class cannot be an interface.
					//DoNotCover
					throw new AssertionError( interfaceAssessment );
				case IsSelfAssessableProvisoryTypeImmutabilityAssessment selfAssessableAssessment:
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

		List<ProvisoryFieldImmutabilityAssessment> provisoryFieldAssessments = new ArrayList<>();
		List<MutableFieldImmutabilityAssessment> mutableFieldAssessments = new ArrayList<>();
		for( Field field : type.getDeclaredFields() )
		{
			FieldImmutabilityAssessment fieldAssessment = fieldImmutabilityAssessor.assessField( field );
			switch( fieldAssessment )
			{
				case null:
					break;
				case UnderAssessmentFieldImmutabilityAssessment ignore:
					break;
				case ImmutableFieldImmutabilityAssessment ignore:
					break;
				case ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment:
				{
					provisoryFieldAssessments.add( provisoryFieldAssessment );
					break;
				}
				case MutableFieldImmutabilityAssessment mutableFieldAssessment:
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
			return new HasMutableFieldsMutableTypeImmutabilityAssessment( stringizer, type, mutableFieldAssessments );

		if( provisorySuperclassAssessment.isPresent() || !provisoryFieldAssessments.isEmpty() )
			return new HasProvisoryContentProvisoryTypeImmutabilityAssessment( stringizer, type, provisorySuperclassAssessment, provisoryFieldAssessments );

		if( Helpers.isExtensible( type ) )
			return new IsExtensibleProvisoryTypeImmutabilityAssessment( stringizer, TypeImmutabilityAssessment.Mode.Assessed, type );

		return typeImmutabilityAssessor.immutableClassAssessmentInstance;
	}
}
