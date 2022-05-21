package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.ByteCodeField;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.constants.value.Mutf8ValueConstant;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * Represents the "Signature" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeType}
 * {@link ByteCodeField}
 * {@link ByteCodeMethod}
 *
 * @author michael.gr
 */
public final class SignatureAttribute extends KnownAttribute
{
	public static SignatureAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		Mutf8ValueConstant signatureConstant = constantPool.getConstant( bufferReader.readUnsignedShort() ).asMutf8ValueConstant();
		return of( signatureConstant );
	}

	public static SignatureAttribute of( String signature )
	{
		return of( Mutf8ValueConstant.of( signature ) );
	}

	public static SignatureAttribute of( Mutf8ValueConstant signatureConstant )
	{
		return new SignatureAttribute( signatureConstant );
	}

	private final Mutf8ValueConstant signatureConstant;

	private SignatureAttribute( Mutf8ValueConstant signatureConstant )
	{
		super( tag_Signature );
		this.signatureConstant = signatureConstant;
	}

	public String signatureString() { return signatureConstant.stringValue(); }
	@Deprecated @Override public SignatureAttribute asSignatureAttribute() { return this; }
	@Override public boolean isOptional() { return true; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "signature = " + signatureConstant.stringValue(); }

	@Override public void intern( Interner interner )
	{
		signatureConstant.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( signatureConstant ) );
	}
}
