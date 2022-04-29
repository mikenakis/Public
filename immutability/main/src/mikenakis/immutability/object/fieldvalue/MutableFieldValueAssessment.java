package mikenakis.immutability.object.fieldvalue;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Represents a 'mutable' assessment on the value of a field.
 */
public final class MutableFieldValueAssessment extends Assessment
{
	public final ProvisoryFieldAssessment provisoryFieldAssessment;
	public final Object fieldValue;
	public final MutableObjectAssessment mutableObjectAssessment;

	public MutableFieldValueAssessment( Stringizer stringizer, ProvisoryFieldAssessment provisoryFieldAssessment, Object fieldValue, MutableObjectAssessment mutableObjectAssessment )
	{
		super( stringizer );
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValue = fieldValue;
		this.mutableObjectAssessment = mutableObjectAssessment;
	}
	@Override public Iterable<? extends Assessment> children() { return List.of( provisoryFieldAssessment, mutableObjectAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		Field field = provisoryFieldAssessment.field;
		stringBuilder.append( "value of field " ).append( stringizer.stringizeFieldName( field ) );
		stringBuilder.append( " (of advertised provisory type " ).append( stringizer.stringizeClassName( field.getType() ) ).append( ")" );
		stringBuilder.append( " is of mutable type " ).append( stringizer.stringizeClassName( fieldValue.getClass() ) );
	}
}
