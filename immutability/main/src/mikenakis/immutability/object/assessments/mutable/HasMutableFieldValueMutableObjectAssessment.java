package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it contains a provisory field which has mutable value.
 */
public final class HasMutableFieldValueMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryFieldAssessment provisoryFieldAssessment;
	public final MutableObjectAssessment fieldValueAssessment;

	public HasMutableFieldValueMutableObjectAssessment( Stringizer stringizer, Object object, ProvisoryFieldAssessment provisoryFieldAssessment, MutableObjectAssessment fieldValueAssessment )
	{
		super( stringizer, object );
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValueAssessment = fieldValueAssessment;
	}

	@Override public Iterable<? extends Assessment> children() { return List.of( provisoryFieldAssessment, fieldValueAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because field " ).append( Stringizer.stringizeFieldName( provisoryFieldAssessment.field ) ).append( " has a mutable value." );
	}
}
