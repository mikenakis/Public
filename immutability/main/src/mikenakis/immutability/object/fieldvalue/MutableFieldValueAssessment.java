package mikenakis.immutability.object.fieldvalue;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Represents a 'mutable' assessment on the value of a field.
 */
public final class MutableFieldValueAssessment extends ImmutabilityAssessment
{
	public final ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment;
	public final Object fieldValue;
	public final MutableObjectImmutabilityAssessment mutableObjectAssessment;

	public MutableFieldValueAssessment( Stringizer stringizer, ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment, Object fieldValue, MutableObjectImmutabilityAssessment mutableObjectAssessment )
	{
		super( stringizer );
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValue = fieldValue;
		this.mutableObjectAssessment = mutableObjectAssessment;
	}
	@Override public Iterable<? extends ImmutabilityAssessment> children() { return List.of( provisoryFieldAssessment, mutableObjectAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		Field field = provisoryFieldAssessment.field;
		stringBuilder.append( "value of field " ).append( Stringizer.stringizeFieldName( field ) );
		stringBuilder.append( " (of provisory advertised type " ).append( stringizer.stringizeClassName( field.getType() ) ).append( ")" );
		stringBuilder.append( " is mutable object " ).append( stringizer.stringizeObjectIdentity( fieldValue ) );
	}
}
