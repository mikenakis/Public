package mikenakis.immutability.internal.type;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.ArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MultiReasonMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableFieldMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableSuperclassMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.InterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryAncestorProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.exceptions.SelfAssessableAnnotationIsOnlyApplicableToClassException;
import mikenakis.immutability.internal.type.exceptions.SelfAssessableClassMustNotBeImmutableException;
import mikenakis.immutability.internal.type.field.FieldImmutabilityAssessor;
import mikenakis.immutability.internal.type.field.assessments.FieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.ImmutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.UnderAssessmentFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Assesses immutability using reflection.
 *
 * @author michael.gr
 */
final class Reflector
{
	private final TypeImmutabilityAssessor typeImmutabilityAssessor;
	private final FieldImmutabilityAssessor fieldImmutabilityAssessor;

	Reflector( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
		fieldImmutabilityAssessor = new FieldImmutabilityAssessor( typeImmutabilityAssessor );
	}

	TypeAssessment assess( Class<?> type )
	{
		TypeAssessment assessment = assess0( type );
		if( ImmutabilitySelfAssessable.class.isAssignableFrom( type ) )
		{
			assert Helpers.isClass( type ) : new SelfAssessableAnnotationIsOnlyApplicableToClassException( type );
			assert !(assessment instanceof ImmutableTypeAssessment) : new SelfAssessableClassMustNotBeImmutableException( type );
			return new SelfAssessableProvisoryTypeAssessment( type );
		}
		return assessment;
	}

	private TypeAssessment assess0( Class<?> type )
	{
		if( type.isArray() )
			return new ArrayMutableTypeAssessment( type );
		if( type.isInterface() )
			return new InterfaceProvisoryTypeAssessment( type );

		List<ProvisoryTypeAssessment> provisoryReasons = new ArrayList<>();
		List<MutableTypeAssessment> mutableReasons = new ArrayList<>();
		Class<?> superclass = type.getSuperclass();
		if( superclass != null )
		{
			TypeAssessment superclassAssessment = assessSuperclass( superclass );
			switch( superclassAssessment )
			{
				case MutableTypeAssessment mutableTypeAssessment:
					mutableReasons.add( new MutableSuperclassMutableTypeAssessment( type, mutableTypeAssessment ) );
					break;
				case ProvisoryTypeAssessment provisoryTypeAssessment:
					provisoryReasons.add( new ProvisoryAncestorProvisoryTypeAssessment( type, provisoryTypeAssessment ) );
					break;
				default:
					assert superclassAssessment instanceof ImmutableTypeAssessment || superclassAssessment instanceof UnderAssessmentTypeAssessment;
			}
		}

		for( Field field : type.getDeclaredFields() )
		{
			if( Modifier.isStatic( field.getModifiers() ) )
				continue;
			FieldAssessment fieldAssessment = fieldImmutabilityAssessor.assessField( field );
			switch( fieldAssessment )
			{
				case ProvisoryFieldAssessment provisoryFieldAssessment:
					provisoryReasons.add( new ProvisoryFieldProvisoryTypeAssessment( type, provisoryFieldAssessment ) );
					break;
				case MutableFieldAssessment mutableFieldAssessment:
					mutableReasons.add( new MutableFieldMutableTypeAssessment( type, mutableFieldAssessment ) );
					break;
				default:
					assert fieldAssessment instanceof ImmutableFieldAssessment || fieldAssessment instanceof UnderAssessmentFieldAssessment;
			}
		}

		if( !mutableReasons.isEmpty() )
			return mutableReasons.size() == 1 ? mutableReasons.get( 0 ) : new MultiReasonMutableTypeAssessment( type, mutableReasons );

		if( !provisoryReasons.isEmpty() )
			return provisoryReasons.size() == 1 ? provisoryReasons.get( 0 ) : new MultiReasonProvisoryTypeAssessment( type, provisoryReasons );

		if( Helpers.isExtensible( type ) )
			return new ExtensibleProvisoryTypeAssessment( TypeAssessment.Mode.Assessed, type );

		return typeImmutabilityAssessor.immutableClassAssessmentInstance;
	}

	private TypeAssessment assessSuperclass( Class<?> superclass )
	{
		TypeAssessment superclassAssessment = typeImmutabilityAssessor.assess( superclass );
		return switch( superclassAssessment )
			{
				//DoNotCover
				case ImmutableTypeAssessment immutableTypeAssessment ->
					throw new AssertionError( immutableTypeAssessment ); //Cannot happen, because the superclass has obviously been extended, so it is extensible, so it can not be immutable.
				//DoNotCover
				case InterfaceProvisoryTypeAssessment interfaceAssessment ->
					throw new AssertionError( interfaceAssessment ); //Cannot happen, because the supertype of a class cannot be an interface.
				case UnderAssessmentTypeAssessment underAssessmentTypeAssessment -> underAssessmentTypeAssessment;
				case MutableTypeAssessment mutableTypeAssessment -> mutableTypeAssessment;
				case ExtensibleProvisoryTypeAssessment ignore ->
					typeImmutabilityAssessor.immutableClassAssessmentInstance; //This means that the supertype is immutable in all aspects except that it is extensible, so the supertype is not preventing us from being immutable.
				case ProvisoryTypeAssessment provisoryTypeAssessment -> provisoryTypeAssessment;
				default ->
					//DoNotCover
					throw new AssertionError( superclassAssessment );
			};
	}
}
