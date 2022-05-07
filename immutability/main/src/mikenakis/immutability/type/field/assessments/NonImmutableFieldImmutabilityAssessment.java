package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.reflect.Field;

public abstract class NonImmutableFieldImmutabilityAssessment extends FieldImmutabilityAssessment
{
	public final Field field;

	protected NonImmutableFieldImmutabilityAssessment( Stringizer stringizer, Field field )
	{
		super( stringizer );
		this.field = field;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "field " ).append( Stringizer.stringizeFieldName( field) );
	}
}
