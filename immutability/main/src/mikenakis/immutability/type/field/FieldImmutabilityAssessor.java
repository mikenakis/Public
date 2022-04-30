package mikenakis.immutability.type.field;

import mikenakis.immutability.internal.helpers.Stringizable;
import mikenakis.immutability.type.TypeImmutabilityAssessor;
import mikenakis.immutability.type.field.annotations.InvariableArray;
import mikenakis.immutability.type.field.annotations.InvariableField;
import mikenakis.immutability.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.type.assessments.TypeAssessment;
import mikenakis.immutability.type.assessments.UnderAssessmentTypeAssessment;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableArrayFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMayNotAlreadyBeInvariableException;
import mikenakis.immutability.type.exceptions.AnnotatedInvariableFieldMustBePrivateException;
import mikenakis.immutability.type.exceptions.NonArrayFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.exceptions.VariableFieldMayNotBeAnnotatedInvariableArrayException;
import mikenakis.immutability.type.field.assessments.FieldAssessment;
import mikenakis.immutability.type.field.assessments.ImmutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.UnderAssessmentFieldAssessment;
import mikenakis.immutability.type.field.assessments.mutable.ArrayMutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.mutable.MutableFieldTypeMutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.mutable.VariableMutableFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.InvariableArrayFieldAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldTypeAssessment;

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
		if( Modifier.isStatic( field.getModifiers() ) )
			return null;
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
				TypeAssessment arrayElementTypeAssessment = typeImmutabilityAssessor.assess( field.getType().getComponentType() );
				return switch( arrayElementTypeAssessment )
					{
						case UnderAssessmentTypeAssessment ignore -> //
							underAssessmentFieldAssessment;
						case ProvisoryTypeAssessment provisoryTypeAssessment -> //
							new InvariableArrayFieldAssessment( stringizer, field, provisoryTypeAssessment );
						case ImmutableTypeAssessment ignore -> //
							immutableFieldAssessment;
						case MutableTypeAssessment mutableTypeAssessment -> //
							new MutableFieldTypeMutableFieldAssessment( stringizer, field, mutableTypeAssessment );
						default -> //
							//DoNotCover
							throw new AssertionError( arrayElementTypeAssessment );
					};
			}
			else
				return new ArrayMutableFieldAssessment( stringizer, field );
		}
		else if( !isArray && isInvariableArray )
			throw new NonArrayFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField && isInvariableArray )
			throw new VariableFieldMayNotBeAnnotatedInvariableArrayException( field );
		else if( !isInvariableField )
			return new VariableMutableFieldAssessment( stringizer, field );
		TypeAssessment fieldTypeAssessment = typeImmutabilityAssessor.assess( field.getType() );
		return switch( fieldTypeAssessment )
			{
				case UnderAssessmentTypeAssessment ignore -> //
					underAssessmentFieldAssessment;
				case ProvisoryTypeAssessment provisoryTypeAssessment -> //
					new ProvisoryFieldTypeAssessment( stringizer, field, provisoryTypeAssessment );
				case ImmutableTypeAssessment ignore -> //
					immutableFieldAssessment;
				case MutableTypeAssessment mutableTypeAssessment -> //
					new MutableFieldTypeMutableFieldAssessment( stringizer, field, mutableTypeAssessment );
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
