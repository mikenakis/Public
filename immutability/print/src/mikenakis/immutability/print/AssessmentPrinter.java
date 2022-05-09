package mikenakis.immutability.print;

import mikenakis.immutability.annotations.Invariable;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableAncestorMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableArrayElementMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableComponentMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.NonEmptyArrayMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.OfMutableTypeMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.ArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableFieldsMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.mutable.MutableSuperclassMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.CompositeProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.InterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryAncestorProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.FieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.ArrayMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.InvariableArrayOfMutableElementTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.VariableMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldTypeProvisoryFieldAssessment;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates human-readable text for assessments.
 *
 * @author michael.gr
 */
public final class AssessmentPrinter
{
	public static final AssessmentPrinter instance = new AssessmentPrinter( Stringizer.defaultInstance );

	private final Stringizer stringizer;

	/**
	 * This constructor is public only for use by the tests. For regular usage, invoke the static {@link #instance}.
	 */
	public AssessmentPrinter( Stringizer stringizer )
	{
		this.stringizer = stringizer;
	}

	public List<String> getAssessmentTextTree( Assessment assessment )
	{
		List<String> lines = new ArrayList<>();
		TextTree.tree( assessment, this::getAssessmentChildren, this::getAssessmentText, s -> lines.add( s ) );
		return lines;
	}

	public Iterable<? extends Assessment> getAssessmentChildren( Assessment assessment )
	{
		return switch( assessment )
			{
				default -> assessment.children();
			};
	}

	public String getAssessmentText( Assessment unknownAssessment )
	{
		StringBuilder stringBuilder = new StringBuilder();
		switch( unknownAssessment )
		{
			case FieldAssessment assessment -> getFieldAssessmentText( stringBuilder, assessment );
			case TypeAssessment assessment -> getTypeAssessmentText( stringBuilder, assessment );
			case ObjectAssessment assessment -> getObjectAssessmentText( stringBuilder, assessment );
			default -> throw new AssertionError( unknownAssessment );
		}
		return stringBuilder.toString();
	}

	private void getFieldAssessmentText( StringBuilder stringBuilder, FieldAssessment fieldAssessment )
	{
		switch( fieldAssessment )
		{
			case MutableFieldAssessment assessment -> getMutableFieldAssessmentText( stringBuilder, assessment );
			case ProvisoryFieldAssessment assessment -> getProvisoryFieldAssessmentText( stringBuilder, assessment );
			default -> throw new AssertionError( fieldAssessment );
		}
	}

	private void getMutableFieldAssessmentText( StringBuilder stringBuilder, MutableFieldAssessment mutableFieldAssessment )
	{
		stringBuilder.append( "field " ).append( Stringizer.stringizeFieldName( mutableFieldAssessment.field ) ).append( " is mutable" );
		switch( mutableFieldAssessment )
		{
			case ArrayMutableFieldAssessment ignored -> //
				stringBuilder.append( " because is an array and it has not been annotated with @" ).append( InvariableArray.class.getSimpleName() );
			case InvariableArrayOfMutableElementTypeMutableFieldAssessment assessment -> //
				stringBuilder.append( " because it is an invariable array of mutable element type " ).append( stringizer.stringizeClassName( assessment.arrayElementTypeAssessment.type ) );
			case MutableFieldTypeMutableFieldAssessment assessment -> //
				stringBuilder.append( " because it is of mutable type " ).append( stringizer.stringizeClassName( assessment.fieldTypeAssessment.type ) );
			case VariableMutableFieldAssessment ignored -> //
				stringBuilder.append( " because it is not final and it has not been annotated with @" ).append( Invariable.class.getSimpleName() );
			default -> throw new AssertionError( mutableFieldAssessment );
		}
	}

	private void getProvisoryFieldAssessmentText( StringBuilder stringBuilder, ProvisoryFieldAssessment provisoryFieldAssessment )
	{
		stringBuilder.append( "field " ).append( Stringizer.stringizeFieldName( provisoryFieldAssessment.field ) ).append( " is provisory" );
		switch( provisoryFieldAssessment )
		{
			case InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment assessment -> //
				stringBuilder.append( " because it is an invariable array of provisory element type " ).append( stringizer.stringizeClassName( assessment.arrayElementTypeAssessment.type ) );
			case ProvisoryFieldTypeProvisoryFieldAssessment assessment -> //
				stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( assessment.field.getType() ) );
			default -> throw new AssertionError( provisoryFieldAssessment );
		}
	}

	private void getTypeAssessmentText( StringBuilder stringBuilder, TypeAssessment typeAssessment )
	{
		switch( typeAssessment )
		{
			case MutableTypeAssessment assessment -> getMutableTypeAssessmentText( stringBuilder, assessment );
			case ProvisoryTypeAssessment assessment -> getProvisoryTypeAssessmentText( stringBuilder, assessment );
			case ImmutableTypeAssessment ignore -> stringBuilder.append( "immutable" );
			default -> throw new AssertionError( typeAssessment );
		}
	}

	private void getMutableTypeAssessmentText( StringBuilder stringBuilder, MutableTypeAssessment mutableTypeAssessment )
	{
		stringBuilder.append( "class " ).append( stringizer.stringizeClassName( mutableTypeAssessment.type ) ).append( " is mutable" );
		switch( mutableTypeAssessment )
		{
			case ArrayMutableTypeAssessment ignore -> stringBuilder.append( " because it is an array class" );
			case MutableFieldsMutableTypeAssessment ignore -> stringBuilder.append( " because it has mutable fields" );
			case MutableSuperclassMutableTypeAssessment assessment -> stringBuilder.append( " because it extends mutable class " ).append( stringizer.stringizeClassName( assessment.superclassAssessment.type ) );
			default -> throw new AssertionError( mutableTypeAssessment );
		}
	}

	private void getProvisoryTypeAssessmentText( StringBuilder stringBuilder, ProvisoryTypeAssessment provisoryTypeAssessment )
	{
		stringBuilder.append( "class " ).append( stringizer.stringizeClassName( provisoryTypeAssessment.type ) ).append( " is provisory" );
		switch( provisoryTypeAssessment )
		{
			case CompositeProvisoryTypeAssessment<?,?> assessment -> stringBuilder.append( " because it " ).append( getTypeAssessmentModeName( assessment.mode ) ).append( " composite" );
			case ExtensibleProvisoryTypeAssessment assessment -> stringBuilder.append( " because it " ).append( getTypeAssessmentModeName( assessment.mode ) ).append( " an extensible class" );
			case InterfaceProvisoryTypeAssessment ignore -> stringBuilder.append( " because it is an interface" );
			case MultiReasonProvisoryTypeAssessment ignore -> stringBuilder.append( " due to multiple reasons" );
			case ProvisoryAncestorProvisoryTypeAssessment assessment -> stringBuilder.append( " because it extends provisory type " ).append( stringizer.stringizeClassName( assessment.ancestorAssessment.type ) );
			case ProvisoryFieldProvisoryTypeAssessment assessment -> stringBuilder.append( " because field " ).append( Stringizer.stringizeFieldName( assessment.fieldAssessment.field ) ).append( " is provisory" );
			case SelfAssessableProvisoryTypeAssessment ignore -> stringBuilder.append( " because instances of this type are self-assessable" );
			default -> throw new AssertionError( provisoryTypeAssessment );
		}
	}

	private void getObjectAssessmentText( StringBuilder stringBuilder, ObjectAssessment objectAssessment )
	{
		switch( objectAssessment )
		{
			case MutableObjectAssessment assessment -> getMutableObjectAssessmentText( stringBuilder, assessment );
			case ImmutableObjectAssessment ignore -> stringBuilder.append( "immutable" );
			default -> throw new AssertionError( objectAssessment );
		}
	}

	private void getMutableObjectAssessmentText( StringBuilder stringBuilder, MutableObjectAssessment mutableObjectAssessment )
	{
		stringBuilder.append( "object " ).append( stringizer.stringizeObjectIdentity( mutableObjectAssessment.object ) ).append( " is mutable" );
		switch( mutableObjectAssessment )
		{
			case MutableAncestorMutableObjectAssessment ignore -> stringBuilder.append( " because it has a mutable ancestor" );
			case MutableArrayElementMutableObjectAssessment<?> assessment -> stringBuilder.append( " because it is an invariable array, and element " ).append( stringizer.stringizeObjectIdentity( assessment.mutableElement ) ).append( " at index " ).append( assessment.mutableElementIndex ).append( " is mutable" );
			case MutableComponentMutableObjectAssessment<?,?> assessment -> stringBuilder.append( " because it is a composite, and element " ).append( stringizer.stringizeObjectIdentity( assessment.mutableElement ) ).append( " at index " ).append( assessment.mutableElementIndex ).append( " is mutable" );
			case MutableFieldValueMutableObjectAssessment assessment -> stringBuilder.append( " because it is of provisory type " ).append( stringizer.stringizeClassName( assessment.declaringTypeAssessment.type ) ).append( " and field " ).append( Stringizer.stringizeFieldName( assessment.provisoryFieldAssessment.field ) ).append( " has mutable value." );
			case NonEmptyArrayMutableObjectAssessment ignore -> stringBuilder.append( " because it is a non-empty array" );
			case OfMutableTypeMutableObjectAssessment ignore -> stringBuilder.append( " because it is of mutable type" );
			case SelfAssessedMutableObjectAssessment ignore -> stringBuilder.append( " because it self-assessed itself as mutable" );
			default -> throw new AssertionError( mutableObjectAssessment );
		}
	}

	private static String getTypeAssessmentModeName( TypeAssessment.Mode typeAssessmentMode )
	{
		return switch( typeAssessmentMode )
			{
				case Assessed -> "is";
				case Preassessed -> "is preassessed as";
				case PreassessedByDefault -> "is preassessed by default as";
			};
	}
}
