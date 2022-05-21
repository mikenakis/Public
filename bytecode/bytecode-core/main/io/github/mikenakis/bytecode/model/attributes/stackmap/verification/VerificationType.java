package io.github.mikenakis.bytecode.model.attributes.stackmap.verification;

import io.github.mikenakis.bytecode.exceptions.InvalidVerificationTypeTagException;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Base class for verification types.  See JVMS §4.10.1.2. "Verification Type System"
 *
 * @author michael.gr
 */
public abstract class VerificationType
{
	public static VerificationType read( BufferReader bufferReader, ReadingConstantPool constantPool, ReadingLocationMap locationMap  )
	{
		int verificationTypeTag = bufferReader.readUnsignedByte();
		return switch( verificationTypeTag )
			{
				case VerificationType.tag_Top, VerificationType.tag_Integer, VerificationType.tag_Float, //
					VerificationType.tag_Double, VerificationType.tag_Long, VerificationType.tag_Null, //
					VerificationType.tag_UninitializedThis -> new SimpleVerificationType( verificationTypeTag );
				case VerificationType.tag_Object -> ObjectVerificationType.read( bufferReader, constantPool );
				case VerificationType.tag_Uninitialized -> UninitializedVerificationType.read( bufferReader, locationMap );
				default -> throw new InvalidVerificationTypeTagException( verificationTypeTag );
			};
	}

	public static final int tag_Top               /**/ = 0;
	public static final int tag_Integer           /**/ = 1;
	public static final int tag_Float             /**/ = 2;
	public static final int tag_Double            /**/ = 3;
	public static final int tag_Long              /**/ = 4;
	public static final int tag_Null              /**/ = 5;
	public static final int tag_UninitializedThis /**/ = 6;
	public static final int tag_Object            /**/ = 7;
	public static final int tag_Uninitialized     /**/ = 8;

	public static String tagName( int verificationTypeTag )
	{
		return switch( verificationTypeTag )
		{
			case tag_Top               /**/ -> "Top";
			case tag_Integer           /**/ -> "Integer";
			case tag_Float             /**/ -> "Float";
			case tag_Double            /**/ -> "Double";
			case tag_Long              /**/ -> "Long";
			case tag_Null              /**/ -> "Null";
			case tag_UninitializedThis /**/ -> "UninitializedThis";
			case tag_Object            /**/ -> "Object";
			case tag_Uninitialized     /**/ -> "Uninitialized";
			default -> throw new InvalidVerificationTypeTagException( verificationTypeTag );
		};
	}

	public final int tag;

	protected VerificationType( int tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ObjectVerificationType asObjectVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public SimpleVerificationType asSimpleVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public UninitializedVerificationType asUninitializedVerificationType() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport @Override @OverridingMethodsMustInvokeSuper public String toString() { return "tag = " + tagName( tag ); }

	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool, WritingLocationMap locationMap );
}
