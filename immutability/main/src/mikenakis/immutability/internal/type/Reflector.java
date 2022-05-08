package mikenakis.immutability.internal.type;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.NonImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.HasMutableFieldsMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.HasMutableSuperclassMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.IsArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.IsExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.IsInterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.IsSelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.exceptions.SelfAssessableAnnotationIsOnlyApplicableToClassException;
import mikenakis.immutability.internal.type.exceptions.SelfAssessableClassMustBeNonImmutableException;
import mikenakis.immutability.internal.type.field.FieldImmutabilityAssessor;
import mikenakis.immutability.internal.type.field.assessments.FieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.ImmutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.UnderAssessmentFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.HasProvisoryAncestorProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.HasProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
			assert assessment instanceof NonImmutableTypeAssessment : new SelfAssessableClassMustBeNonImmutableException( type );
			return new IsSelfAssessableProvisoryTypeAssessment( stringizer, type );
		}
		return assessment;
	}

	private TypeAssessment assess0( Class<?> type )
	{
		if( type.isArray() )
			return new IsArrayMutableTypeAssessment( stringizer, type );
		if( type.isInterface() )
			return new IsInterfaceProvisoryTypeAssessment( stringizer, type );

		List<ProvisoryTypeAssessment> provisoryReasons = new ArrayList<>();
		Class<?> superclass = type.getSuperclass();
		if( superclass != null )
		{
			TypeAssessment superclassAssessment = assessSuperclass( type, superclass );
			switch( superclassAssessment )
			{
				case MutableTypeAssessment mutableTypeAssessment:
					return mutableTypeAssessment;
				case ProvisoryTypeAssessment provisoryTypeAssessment:
					provisoryReasons.add( provisoryTypeAssessment );
					break;
				default:
					assert superclassAssessment instanceof ImmutableTypeAssessment || superclassAssessment instanceof UnderAssessmentTypeAssessment;
			}
		}

		List<MutableFieldAssessment> mutableFieldAssessments = new ArrayList<>();
		for( Field field : type.getDeclaredFields() )
		{
			if( Modifier.isStatic( field.getModifiers() ) )
				continue;
			FieldAssessment fieldAssessment = fieldImmutabilityAssessor.assessField( field );
			switch( fieldAssessment )
			{
				case ProvisoryFieldAssessment provisoryFieldAssessment:
				{
					provisoryReasons.add( new HasProvisoryFieldProvisoryTypeAssessment( stringizer, type, provisoryFieldAssessment ) );
					break;
				}
				case MutableFieldAssessment mutableFieldAssessment:
				{
					mutableFieldAssessments.add( mutableFieldAssessment );
					break;
				}
				default:
					assert fieldAssessment instanceof UnderAssessmentFieldAssessment || fieldAssessment instanceof ImmutableFieldAssessment;
			}
		}

		if( !mutableFieldAssessments.isEmpty() )
			return new HasMutableFieldsMutableTypeAssessment( stringizer, type, mutableFieldAssessments );

		if( !provisoryReasons.isEmpty() )
			return new MultiReasonProvisoryTypeAssessment( stringizer, type, provisoryReasons );

		if( Helpers.isExtensible( type ) )
			return new IsExtensibleProvisoryTypeAssessment( stringizer, TypeAssessment.Mode.Assessed, type );

		return typeImmutabilityAssessor.immutableClassAssessmentInstance;
	}

	private TypeAssessment assessSuperclass( Class<?> type, Class<?> superclass )
	{
		TypeAssessment superclassAssessment = typeImmutabilityAssessor.assess( superclass );
		return switch( superclassAssessment )
		{
			case MutableTypeAssessment mutableTypeAssessment -> new HasMutableSuperclassMutableTypeAssessment( stringizer, type, mutableTypeAssessment );
			case IsExtensibleProvisoryTypeAssessment ignore -> typeImmutabilityAssessor.immutableClassAssessmentInstance; //This means that the supertype is immutable in all aspects except that it is extensible, so the supertype is not preventing us from being immutable.
			case MultiReasonProvisoryTypeAssessment multiReasonAssessment -> new HasProvisoryAncestorProvisoryTypeAssessment( stringizer, type, multiReasonAssessment );
			case UnderAssessmentTypeAssessment ignore -> ignore;
			case ImmutableTypeAssessment immutableTypeAssessment ->
				//Cannot happen, because the superclass has obviously been extended, so it is extensible, so it can not be immutable.
				//DoNotCover
				throw new AssertionError( immutableTypeAssessment );
			case IsInterfaceProvisoryTypeAssessment interfaceAssessment ->
				//Cannot happen, because the supertype of a class cannot be an interface.
				//DoNotCover
				throw new AssertionError( interfaceAssessment );
			case IsSelfAssessableProvisoryTypeAssessment selfAssessableAssessment -> new HasProvisoryAncestorProvisoryTypeAssessment( stringizer, type, selfAssessableAssessment );
			default ->
				//DoNotCover
				throw new AssertionError( superclassAssessment );
		};
	}
}
