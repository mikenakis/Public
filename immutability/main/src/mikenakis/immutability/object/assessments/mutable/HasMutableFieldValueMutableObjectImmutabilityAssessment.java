package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.ImmutabilityAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectImmutabilityAssessment;
import mikenakis.immutability.type.field.assessments.provisory.ProvisoryFieldImmutabilityAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it contains a provisory field which has mutable value.
 */
public final class HasMutableFieldValueMutableObjectImmutabilityAssessment extends MutableObjectImmutabilityAssessment
{
	public final ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment;
	public final MutableObjectImmutabilityAssessment fieldValueAssessment;

	public HasMutableFieldValueMutableObjectImmutabilityAssessment( Stringizer stringizer, Object object, ProvisoryFieldImmutabilityAssessment provisoryFieldAssessment, MutableObjectImmutabilityAssessment fieldValueAssessment )
	{
		super( stringizer, object );
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValueAssessment = fieldValueAssessment;
	}

	@Override public Iterable<? extends ImmutabilityAssessment> children() { return List.of( provisoryFieldAssessment, fieldValueAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because field " ).append( Stringizer.stringizeFieldName( provisoryFieldAssessment.field ) ).append( " has a mutable value." );
	}
}
