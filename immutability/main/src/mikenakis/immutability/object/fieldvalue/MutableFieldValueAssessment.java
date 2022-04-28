package mikenakis.immutability.object.fieldvalue;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

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
		stringBuilder.append( "value of field '" );
		if( MyKit.get( false ) )
			stringBuilder.append( stringizer.stringize( provisoryFieldAssessment.field.getDeclaringClass() ) ).append( "." );
		stringBuilder.append( provisoryFieldAssessment.field.getName() ).append( "'" );
		stringBuilder.append( " (of advertised type '" ).append( stringizer.stringize( provisoryFieldAssessment.field.getType() ) ).append( "', which is provisory)" );
		stringBuilder.append( " is of type '" ).append( stringizer.stringize( fieldValue.getClass() ) ).append( "', which is mutable" );
	}
}
