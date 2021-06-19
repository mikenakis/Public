package mikenakis.bytecode.constants;

import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantKind;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the JVMS::CONSTANT_MethodHandle_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class MethodHandleConstant extends Constant
{
	@SuppressWarnings( "SpellCheckingInspection" )
	public enum ReferenceKind
	{
		GetField          /**/( 1 ), /* 1 JVMS::REF_getField         getfield C.f:T                               */
		GetStatic         /**/( 2 ), /* 2 JVMS::REF_getStatic        getstatic C.f:T                              */
		PutField          /**/( 3 ), /* 3 JVMS::REF_putField         putfield C.f:T                               */
		PutStatic         /**/( 4 ), /* 4 JVMS::REF_putStatic        putstatic C.f:T                              */
		InvokeVirtual     /**/( 5 ), /* 5 JVMS::REF_invokeVirtual    invokevirtual C.m:(A*)T                      */
		InvokeStatic      /**/( 6 ), /* 6 JVMS::REF_invokeStatic     invokestatic C.m:(A*)T                       */
		InvokeSpecial     /**/( 7 ), /* 7 JVMS::REF_invokeSpecial    invokespecial C.m:(A*)T                      */
		NewInvokeSpecial  /**/( 8 ), /* 8 JVMS::REF_newInvokeSpecial new C; dup; invokespecial C.<init>:(A*)void  */
		InvokeInterface   /**/( 9 ); /* 9 JVMS::REF_invokeInterface  invokeinterface C.m:(A*)T                    */

		public static final List<ReferenceKind> valueList = List.of( values() );
		private static final Map<Integer,ReferenceKind> valuesFromNumbers = valueList.stream().collect( Collectors.toMap( v -> v.number, v -> v ) );

		public static Optional<ReferenceKind> tryFromNumber( int number )
		{
			return Optional.ofNullable( Kit.map.tryGet( valuesFromNumbers, number ) );
		}

		public static ReferenceKind fromNumber( int number )
		{
			return tryFromNumber( number ).orElseThrow();
		}

		public final int number;

		ReferenceKind( int number )
		{
			this.number = number;
		}
	}

	public static final int TAG = 15; // JVMS::CONSTANT_MethodHandle_info
	public static final ConstantKind KIND = new ConstantKind( TAG, "MethodHandle" )
	{
		@Override public Buffer readBuffer( BufferReader bufferReader )
		{
			return bufferReader.readBuffer( 3 );
		}

		@Override public Constant parse( ConstantPool constantPool, BufferReader bufferReader )
		{
			return new MethodHandleConstant( constantPool, bufferReader );
		}
	};

	public final int referenceKind;
	public final ReferenceConstant referenceConstant;

	public MethodHandleConstant( ReferenceKind referenceKind, ReferenceConstant referenceConstant )
	{
		super( KIND );
		assert referenceConstant.kind == PlainMethodReferenceConstant.KIND || referenceConstant.kind == InterfaceMethodReferenceConstant.KIND;
		this.referenceKind = referenceKind.number;
		this.referenceConstant = referenceConstant;
	}

	private MethodHandleConstant( ConstantPool constantPool, BufferReader bufferReader )
	{
		super( KIND );
		referenceKind = bufferReader.readUnsignedByte();
		assert ReferenceKind.tryFromNumber( referenceKind ).isPresent();
		referenceConstant = constantPool.readIndexAndGetConstant( bufferReader ).asReferenceConstant();
		assert referenceConstant.kind == PlainMethodReferenceConstant.KIND || referenceConstant.kind == InterfaceMethodReferenceConstant.KIND ||
			referenceConstant.kind == FieldReferenceConstant.KIND;
	}

	@Override public void intern( ConstantPool constantPool )
	{
		referenceConstant.intern( constantPool );
		super.intern( constantPool );
	}

	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
	{
		bufferWriter.writeUnsignedByte( referenceKind );
		referenceConstant.writeIndex( constantPool, bufferWriter );
	}

	@Override public void toStringBuilder( StringBuilder builder )
	{
		builder.append( "kind = " ).append( ReferenceKind.fromNumber( referenceKind ).name() );
		builder.append( ", referenceConstant = " );
		referenceConstant.toStringBuilder( builder );
	}

	@Deprecated @Override public MethodHandleConstant asMethodHandleConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof MethodHandleConstant methodHandleConstant )
		{
			if( referenceKind != methodHandleConstant.referenceKind )
				return false;
			if( !referenceConstant.equalsReferenceConstant( methodHandleConstant.referenceConstant ) )
				return false;
			return true;
		}
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( kind, referenceKind, referenceConstant );
	}
}
