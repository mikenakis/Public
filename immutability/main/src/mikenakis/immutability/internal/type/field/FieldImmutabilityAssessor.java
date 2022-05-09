package mikenakis.immutability.internal.type.field;

import mikenakis.immutability.annotations.Invariable;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.internal.type.TypeImmutabilityAssessor;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.internal.type.exceptions.AnnotatedInvariableArrayFieldMustBePrivateException;
import mikenakis.immutability.internal.type.exceptions.AnnotatedInvariableFieldMayNotAlreadyBeInvariableException;
import mikenakis.immutability.internal.type.exceptions.AnnotatedInvariableFieldMustBePrivateException;
import mikenakis.immutability.internal.type.exceptions.NonArrayFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.internal.type.exceptions.VariableFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.internal.type.field.assessments.FieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.ImmutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.UnderAssessmentFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.ArrayMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.InvariableArrayOfMutableElementTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.VariableMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldTypeProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldImmutabilityAssessor extends Stringizable
{
	private final TypeImmutabilityAssessor typeImmutabilityAssessor;
	private final UnderAssessmentFieldAssessment underAssessmentFieldAssessment = new UnderAssessmentFieldAssessment( stringizer );
	private final ImmutableFieldAssessment immutableFieldAssessment = new ImmutableFieldAssessment( stringizer );

	public FieldImmutabilityAssessor( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		super( typeImmutabilityAssessor.stringizer );
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
	}

	public FieldAssessment assessField( Field field )
	{
		assert !Modifier.isStatic( field.getModifiers() );
		boolean isInvariableField = isInvariableField( field );
		boolean isArray = field.getType().isArray();
		boolean isInvariableArray = field.isAnnotationPresent( InvariableArray.class );
		if( !isArray && isInvariableArray )
			throw new NonArrayFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField && isInvariableArray )
			throw new VariableFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField )
			return new VariableMutableFieldAssessment( stringizer, field );
		else if( isArray && !isInvariableArray )
			return new ArrayMutableFieldAssessment( stringizer, field );
		else if( isArray )
		{
			assert isArray && isInvariableArray;
			if( !Modifier.isPrivate( field.getModifiers() ) )
				throw new AnnotatedInvariableArrayFieldMustBePrivateException( field );
			TypeAssessment arrayElementTypeAssessment = typeImmutabilityAssessor.assess( field.getType().getComponentType() );
			return switch( arrayElementTypeAssessment )
				{
					case UnderAssessmentTypeAssessment ignore -> underAssessmentFieldAssessment;
					case ProvisoryTypeAssessment provisoryTypeAssessment -> new InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment( stringizer, field, provisoryTypeAssessment );
					case ImmutableTypeAssessment ignore -> immutableFieldAssessment;
					case MutableTypeAssessment mutableTypeAssessment -> new InvariableArrayOfMutableElementTypeMutableFieldAssessment( stringizer, field, mutableTypeAssessment );
					//DoNotCover
					default -> throw new AssertionError( arrayElementTypeAssessment );
				};
		}
		TypeAssessment fieldTypeAssessment = typeImmutabilityAssessor.assess( field.getType() );
		return switch( fieldTypeAssessment )
			{
				case UnderAssessmentTypeAssessment ignore -> underAssessmentFieldAssessment;
				case ProvisoryTypeAssessment provisoryTypeAssessment -> new ProvisoryFieldTypeProvisoryFieldAssessment( stringizer, field, provisoryTypeAssessment );
				case ImmutableTypeAssessment ignore -> immutableFieldAssessment;
				case MutableTypeAssessment mutableTypeAssessment -> new MutableFieldTypeMutableFieldAssessment( stringizer, field, mutableTypeAssessment );
				//DoNotCover
				default -> throw new AssertionError( fieldTypeAssessment );
			};
	}

	public static boolean isInvariableField( Field field )
	{
		int fieldModifiers = field.getModifiers();
		assert !Modifier.isStatic( fieldModifiers );
		boolean isAnnotatedInvariable = field.isAnnotationPresent( Invariable.class );
		boolean isPrivate = Modifier.isPrivate( fieldModifiers );
		if( isAnnotatedInvariable && !isPrivate )
			throw new AnnotatedInvariableFieldMustBePrivateException( field );
		boolean isDeclaredInvariable = Modifier.isFinal( fieldModifiers );
		if( isDeclaredInvariable && isAnnotatedInvariable )
			throw new AnnotatedInvariableFieldMayNotAlreadyBeInvariableException( field );
		return isDeclaredInvariable || isAnnotatedInvariable;
	}
}