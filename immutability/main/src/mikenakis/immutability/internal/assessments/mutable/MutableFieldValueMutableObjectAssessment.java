package mikenakis.immutability.internal.assessments.mutable;

import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;

import java.util.List;

/**
 * Signifies that an object is mutable because it contains a provisory field which has mutable value.
 */
public final class MutableFieldValueMutableObjectAssessment extends MutableObjectAssessment
{
	public final ProvisoryTypeAssessment declaringTypeAssessment;
	public final ProvisoryFieldAssessment provisoryFieldAssessment;
	public final MutableObjectAssessment fieldValueAssessment;

	public MutableFieldValueMutableObjectAssessment( Stringizer stringizer, Object object, ProvisoryTypeAssessment declaringTypeAssessment, ProvisoryFieldAssessment provisoryFieldAssessment, MutableObjectAssessment fieldValueAssessment )
	{
		super( stringizer, object );
		this.declaringTypeAssessment = declaringTypeAssessment;
		this.provisoryFieldAssessment = provisoryFieldAssessment;
		this.fieldValueAssessment = fieldValueAssessment;
	}

	@Override public Iterable<? extends Assessment> children() { return List.of( declaringTypeAssessment, provisoryFieldAssessment, fieldValueAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( declaringTypeAssessment.type ) );
		stringBuilder.append( " and field " ).append( Stringizer.stringizeFieldName( provisoryFieldAssessment.field ) ).append( " has a mutable value." );
	}
}
