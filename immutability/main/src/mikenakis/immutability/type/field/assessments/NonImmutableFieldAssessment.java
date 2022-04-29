package mikenakis.immutability.type.field.assessments;

import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;

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
		stringBuilder.append( "field " ).append( stringizer.stringizeFieldName( field) );
	}
}
