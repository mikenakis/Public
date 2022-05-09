package mikenakis.immutability.internal.type;

import mikenakis.immutability.ImmutabilitySelfAssessable;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableFieldsMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableSuperclassMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.ArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryAncestorProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.InterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
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
			assert !(assessment instanceof ImmutableTypeAssessment) : new SelfAssessableClassMustNotBeImmutableException( type );
			return new SelfAssessableProvisoryTypeAssessment( stringizer, type );
		}
		return assessment;
	}

	private TypeAssessment assess0( Class<?> type )
	{
		if( type.isArray() )
			return new ArrayMutableTypeAssessment( stringizer, type );
		if( type.isInterface() )
			return new InterfaceProvisoryTypeAssessment( stringizer, type );

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
					provisoryReasons.add( new ProvisoryFieldProvisoryTypeAssessment( stringizer, type, provisoryFieldAssessment ) );
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
			return new MutableFieldsMutableTypeAssessment( stringizer, type, mutableFieldAssessments );

		if( !provisoryReasons.isEmpty() )
			return new MultiReasonProvisoryTypeAssessment( stringizer, type, provisoryReasons );

		if( Helpers.isExtensible( type ) )
			return new ExtensibleProvisoryTypeAssessment( stringizer, TypeAssessment.Mode.Assessed, type );

		return typeImmutabilityAssessor.immutableClassAssessmentInstance;
	}

	private TypeAssessment assessSuperclass( Class<?> type, Class<?> superclass )
	{
		TypeAssessment superclassAssessment = typeImmutabilityAssessor.assess( superclass );
		return switch( superclassAssessment )
		{
			case MutableTypeAssessment mutableTypeAssessment -> new MutableSuperclassMutableTypeAssessment( stringizer, type, mutableTypeAssessment );
			case ExtensibleProvisoryTypeAssessment ignore -> typeImmutabilityAssessor.immutableClassAssessmentInstance; //This means that the supertype is immutable in all aspects except that it is extensible, so the supertype is not preventing us from being immutable.
			case MultiReasonProvisoryTypeAssessment multiReasonAssessment -> new ProvisoryAncestorProvisoryTypeAssessment( stringizer, type, multiReasonAssessment );
			case UnderAssessmentTypeAssessment ignore -> ignore;
			case ImmutableTypeAssessment immutableTypeAssessment ->
				//Cannot happen, because the superclass has obviously been extended, so it is extensible, so it can not be immutable.
				//DoNotCover
				throw new AssertionError( immutableTypeAssessment );
			case InterfaceProvisoryTypeAssessment interfaceAssessment ->
				//Cannot happen, because the supertype of a class cannot be an interface.
				//DoNotCover
				throw new AssertionError( interfaceAssessment );
			case SelfAssessableProvisoryTypeAssessment selfAssessableAssessment -> new ProvisoryAncestorProvisoryTypeAssessment( stringizer, type, selfAssessableAssessment );
			default ->
				//DoNotCover
				throw new AssertionError( superclassAssessment );
		};
	}
}
