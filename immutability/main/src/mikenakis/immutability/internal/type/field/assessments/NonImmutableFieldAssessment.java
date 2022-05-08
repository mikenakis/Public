package mikenakis.immutability.internal.type.field.assessments;

import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.reflect.Field;

public abstract class NonImmutableFieldAssessment extends FieldAssessment
{
	public final Field field;

	protected NonImmutableFieldAssessment( Stringizer stringizer, Field field )
	{
		super( stringizer );
		this.field = field;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "field " ).append( Stringizer.stringizeFieldName( field) );
	}
}
