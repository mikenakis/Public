package mikenakis.immutability.print;

import mikenakis.immutability.annotations.Invariable;
import mikenakis.immutability.annotations.InvariableArray;
import mikenakis.immutability.internal.assessments.Assessment;
import mikenakis.immutability.internal.assessments.ImmutableObjectAssessment;
import mikenakis.immutability.internal.assessments.MutableObjectAssessment;
import mikenakis.immutability.internal.assessments.ObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableSuperclassMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableArrayElementMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableComponentMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableFieldValueMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.NonEmptyArrayMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.MutableClassMutableObjectAssessment;
import mikenakis.immutability.internal.assessments.mutable.SelfAssessedMutableObjectAssessment;
import mikenakis.immutability.internal.mykit.MyKit;
import mikenakis.immutability.internal.type.assessments.ImmutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.TypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.ArrayMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.MultiReasonMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.MutableFieldMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.MutableSuperclassMutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.mutable.MutableTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.CompositeProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ExtensibleProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.InterfaceProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.MultiReasonProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisoryFieldProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisorySuperclassProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.ProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.assessments.nonimmutable.provisory.SelfAssessableProvisoryTypeAssessment;
import mikenakis.immutability.internal.type.field.assessments.FieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.ArrayMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.InvariableArrayOfMutableElementTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.MutableFieldTypeMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.mutable.VariableMutableFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldAssessment;
import mikenakis.immutability.internal.type.field.assessments.provisory.ProvisoryFieldTypeProvisoryFieldAssessment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates human-readable text for assessments.
 *
 * @author michael.gr
 */
public final class AssessmentPrinter
{
	public static List<String> getObjectAssessmentTextTree( ObjectAssessment assessment )
	{
		List<String> lines = new ArrayList<>();
		TextTree.tree( assessment, Assessment::children, AssessmentPrinter::getAssessmentText, s -> lines.add( s ) );
		return lines;
	}

	private static String getAssessmentText( Assessment unknownAssessment )
	{
		AssessmentPrinter assessmentPrinter = new AssessmentPrinter();
		switch( unknownAssessment )
		{
			case FieldAssessment assessment -> assessmentPrinter.getFieldAssessmentText( assessment );
			case TypeAssessment assessment -> assessmentPrinter.getTypeAssessmentText( assessment );
			case ObjectAssessment assessment -> assessmentPrinter.getObjectAssessmentText( assessment );
			default -> throw new AssertionError( unknownAssessment );
		}
		assessmentPrinter.append( ". (" + unknownAssessment.getClass().getSimpleName() + ")" );
		return assessmentPrinter.stringBuilder.toString();
	}

	private final StringBuilder stringBuilder = new StringBuilder();

	private AssessmentPrinter()
	{
	}

	private void append( String text )
	{
		stringBuilder.append( text );
	}

	private void getFieldAssessmentText( FieldAssessment fieldAssessment )
	{
		switch( fieldAssessment )
		{
			case MutableFieldAssessment assessment -> getMutableFieldAssessmentText( assessment );
			case ProvisoryFieldAssessment assessment -> getProvisoryFieldAssessmentText( assessment );
			default -> throw new AssertionError( fieldAssessment );
		}
	}

	private void getMutableFieldAssessmentText( MutableFieldAssessment mutableFieldAssessment )
	{
		append( "field " + stringFromFieldName( mutableFieldAssessment.field ) + " is mutable" );
		switch( mutableFieldAssessment )
		{
			case ArrayMutableFieldAssessment ignored -> //
				append( " because is an array and it has not been annotated with @" + InvariableArray.class.getSimpleName() );
			case InvariableArrayOfMutableElementTypeMutableFieldAssessment assessment -> //
				append( " because it is an array of mutable element type " + stringFromClassName( assessment.arrayElementTypeAssessment.type ) );
			case MutableFieldTypeMutableFieldAssessment assessment -> //
				append( " because it is of mutable type " + stringFromClassName( assessment.fieldTypeAssessment.type ) );
			case VariableMutableFieldAssessment ignored -> //
				append( " because it is not final and it has not been annotated with @" + Invariable.class.getSimpleName() );
			default -> throw new AssertionError( mutableFieldAssessment );
		}
	}

	private void getProvisoryFieldAssessmentText( ProvisoryFieldAssessment provisoryFieldAssessment )
	{
		append( "field " + stringFromFieldName( provisoryFieldAssessment.field ) + " is provisory" );
		switch( provisoryFieldAssessment )
		{
			case InvariableArrayOfProvisoryElementTypeProvisoryFieldAssessment assessment -> //
				append( " because it is an invariable array of provisory element type " + stringFromClassName( assessment.arrayElementTypeAssessment.type ) );
			case ProvisoryFieldTypeProvisoryFieldAssessment assessment -> //
				append( " because it is of provisory type " + stringFromClassName( assessment.field.getType() ) );
			default -> throw new AssertionError( provisoryFieldAssessment );
		}
	}

	private void getTypeAssessmentText( TypeAssessment typeAssessment )
	{
		switch( typeAssessment )
		{
			case MutableTypeAssessment assessment -> getMutableTypeAssessmentText( assessment );
			case ProvisoryTypeAssessment assessment -> getProvisoryTypeAssessmentText( assessment );
			case ImmutableTypeAssessment ignore -> append( "immutable" );
			default -> throw new AssertionError( typeAssessment );
		}
	}

	private void getMutableTypeAssessmentText( MutableTypeAssessment mutableTypeAssessment )
	{
		append( "class " + stringFromClassName( mutableTypeAssessment.type ) + " is mutable" );
		switch( mutableTypeAssessment )
		{
			case ArrayMutableTypeAssessment ignore -> append( " because it is an array class" );
			case MultiReasonMutableTypeAssessment ignore -> append( " due to multiple reasons" );
			case MutableFieldMutableTypeAssessment mutableFieldMutableTypeAssessment ->
				append( " because field " + stringFromFieldName( mutableFieldMutableTypeAssessment.mutableFieldAssessment.field ) + " is mutable" );
			case MutableSuperclassMutableTypeAssessment assessment ->
				append( " because it extends mutable class " + stringFromClassName( assessment.superclassAssessment.type ) );
			default -> throw new AssertionError( mutableTypeAssessment );
		}
	}

	private void getProvisoryTypeAssessmentText( ProvisoryTypeAssessment provisoryTypeAssessment )
	{
		append( "class " + stringFromClassName( provisoryTypeAssessment.type ) + " is provisory" );
		switch( provisoryTypeAssessment )
		{
			case CompositeProvisoryTypeAssessment<?,?> assessment -> append( " because it " + getTypeAssessmentModeName( assessment.mode ) + " composite" );
			case ExtensibleProvisoryTypeAssessment assessment ->
				append( " because it " + getTypeAssessmentModeName( assessment.mode ) + " an extensible class" );
			case InterfaceProvisoryTypeAssessment ignore -> append( " because it is an interface" );
			case MultiReasonProvisoryTypeAssessment ignore -> append( " due to multiple reasons" );
			case ProvisorySuperclassProvisoryTypeAssessment assessment ->
				append( " because it extends provisory type " + stringFromClassName( assessment.superclassAssessment.type ) );
			case ProvisoryFieldProvisoryTypeAssessment assessment ->
				append( " because field " + stringFromFieldName( assessment.fieldAssessment.field ) + " is provisory" );
			case SelfAssessableProvisoryTypeAssessment ignore -> append( " because instances of this type are self-assessable" );
			default -> throw new AssertionError( provisoryTypeAssessment );
		}
	}

	private void getObjectAssessmentText( ObjectAssessment objectAssessment )
	{
		switch( objectAssessment )
		{
			case MutableObjectAssessment assessment -> getMutableObjectAssessmentText( assessment );
			case ImmutableObjectAssessment ignore -> append( "immutable" );
			default -> throw new AssertionError( objectAssessment );
		}
	}

	private void getMutableObjectAssessmentText( MutableObjectAssessment mutableObjectAssessment )
	{
		append( "object " + stringFromObjectIdentity( mutableObjectAssessment.object() ) + " is mutable" );
		switch( mutableObjectAssessment )
		{
			case MutableSuperclassMutableObjectAssessment ignore -> append( " because its superclass is mutable" );
			case MutableArrayElementMutableObjectAssessment assessment ->
				append( " because it is an invariable array, and element " + stringFromObjectIdentity( assessment.elementAssessment.object() ) + " at index " + assessment.mutableElementIndex + " is mutable" );
			case MutableComponentMutableObjectAssessment<?,?> assessment ->
				append( " because it is a composite, and element " + stringFromObjectIdentity( assessment.mutableElement ) + " at index " + assessment.mutableElementIndex + " is mutable" );
			case MutableFieldValueMutableObjectAssessment assessment ->
				append( " because it is of provisory type " + stringFromClassName( assessment.typeAssessment().type ) + " and field " + stringFromFieldName( assessment.provisoryFieldAssessment.field ) + " has mutable value." );
			case NonEmptyArrayMutableObjectAssessment ignore -> append( " because it is a non-empty array" );
			case MutableClassMutableObjectAssessment ignore -> append( " because it is of a mutable class" );
			case SelfAssessedMutableObjectAssessment ignore -> append( " because it assessed itself as mutable" );
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

	private static String stringFromClassName( Class<?> jvmClass )
	{
		return "'" + MyKit.getClassName( jvmClass ) + "'";
	}

	private static String stringFromFieldName( @SuppressWarnings( "TypeMayBeWeakened" ) Field field )
	{
		return "'" + field.getName() + "'";
	}

	public static String stringFromObjectIdentity( Object object )
	{
		assert !(object instanceof Class<?>);
		return MyKit.identityString( object );
	}
}
