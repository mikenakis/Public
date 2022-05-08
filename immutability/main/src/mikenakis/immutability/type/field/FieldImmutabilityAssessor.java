package mikenakis.immutability.type.field;

import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.assessments.ImmutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.MutableTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.TypeImmutabilityAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeImmutabilityAssessment;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableArrayFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMayNotAlreadyBeInvariableException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.NonArrayFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.exceptions.VariableFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.field.assessments.FieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.ImmutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.UnderAssessmentFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.ArrayMutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.OfMutableFieldTypeMutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.mutable.VariableMutableFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.IsInvariableArrayProvisoryFieldImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.OfProvisoryTypeProvisoryFieldImmutabilityAssessment;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldImmutabilityAssessor extends Stringizable
{
	private final TypeImmutabilityAssessor typeImmutabilityAssessor;
	private final UnderAssessmentFieldImmutabilityAssessment underAssessmentFieldAssessment = new UnderAssessmentFieldImmutabilityAssessment( stringizer );
	private final ImmutableFieldImmutabilityAssessment immutableFieldAssessment = new ImmutableFieldImmutabilityAssessment( stringizer );

	public FieldImmutabilityAssessor( TypeImmutabilityAssessor typeImmutabilityAssessor )
	{
		super( typeImmutabilityAssessor.stringizer );
		this.typeImmutabilityAssessor = typeImmutabilityAssessor;
	}

	public FieldImmutabilityAssessment assessField( Field field )
	{
		if( Modifier.isStatic( field.getModifiers() ) )
			return immutableFieldAssessment;
		boolean isInvariableField = isInvariableField( field );
		boolean isArray = field.getType().isArray();
		boolean isInvariableArray = field.isAnnotationPresent( InvariableArray.class );
		if( isInvariableField && isArray )
		{
			if( isInvariableArray )
			{
				if( !Modifier.isPrivate( field.getModifiers() ) )
					throw new AnnotatedInvariableArrayFieldMustBePrivateException( field );
				//TODO: merge this block of code with the very similar block of code further down this method.
				TypeImmutabilityAssessment arrayElementTypeAssessment = typeImmutabilityAssessor.assess( field.getType().getComponentType() );
				return switch( arrayElementTypeAssessment )
					{
						case UnderAssessmentTypeImmutabilityAssessment ignore -> //
							underAssessmentFieldAssessment;
						case ProvisoryTypeImmutabilityAssessment provisoryTypeAssessment -> //
							new IsInvariableArrayProvisoryFieldImmutabilityAssessment( stringizer, field, provisoryTypeAssessment );
						case ImmutableTypeImmutabilityAssessment ignore -> //
							immutableFieldAssessment;
						case MutableTypeImmutabilityAssessment mutableTypeAssessment -> //
							new OfMutableFieldTypeMutableFieldImmutabilityAssessment( stringizer, field, mutableTypeAssessment );
						default -> //
							//DoNotCover
							throw new AssertionError( arrayElementTypeAssessment );
					};
			}
			else
				return new ArrayMutableFieldImmutabilityAssessment( stringizer, field );
		}
		else if( !isArray && isInvariableArray )
			throw new NonArrayFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField && isInvariableArray )
			throw new VariableFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField )
			return new VariableMutableFieldImmutabilityAssessment( stringizer, field );
		TypeImmutabilityAssessment fieldTypeAssessment = typeImmutabilityAssessor.assess( field.getType() );
		return switch( fieldTypeAssessment )
			{
				case UnderAssessmentTypeImmutabilityAssessment ignore -> //
					underAssessmentFieldAssessment;
				case ProvisoryTypeImmutabilityAssessment provisoryTypeAssessment -> //
					new OfProvisoryTypeProvisoryFieldImmutabilityAssessment( stringizer, field, provisoryTypeAssessment );
				case ImmutableTypeImmutabilityAssessment ignore -> //
					immutableFieldAssessment;
				case MutableTypeImmutabilityAssessment mutableTypeAssessment -> //
					new OfMutableFieldTypeMutableFieldImmutabilityAssessment( stringizer, field, mutableTypeAssessment );
				default -> //
					//DoNotCover
					throw new AssertionError( fieldTypeAssessment );
			};
	}

	public static boolean isInvariableField( Field field )
	{
		int fieldModifiers = field.getModifiers();
		assert !Modifier.isStatic( fieldModifiers );
		boolean isAnnotatedInvariable = field.isAnnotationPresent( InvariableField.class );
		boolean isPrivate = Modifier.isPrivate( fieldModifiers );
		if( isAnnotatedInvariable && !isPrivate )
			throw new AnnotatedInvariableFieldMustBePrivateException( field );
		boolean isDeclaredInvariable = Modifier.isFinal( fieldModifiers );
		if( isDeclaredInvariable && isAnnotatedInvariable )
			throw new AnnotatedInvariableFieldMayNotAlreadyBeInvariableException( field );
		return isDeclaredInvariable || isAnnotatedInvariable;
	}
}
