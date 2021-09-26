package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodHandleDesc;
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

		public final int number;

		ReferenceKind( int number )
		{
			this.number = number;
		}
	}

	private final ReferenceKind referenceKind;
	private ReferenceConstant referenceConstant; //null means it has not been set yet.

	public MethodHandleConstant( ReferenceKind referenceKind )
	{
		super( tag_MethodHandle );
		this.referenceKind = referenceKind;
	}

	public ReferenceKind referenceKind() { return referenceKind; }

	public ReferenceConstant getReferenceConstant()
	{
		assert referenceConstant != null;
		return referenceConstant;
	}

	public void setReferenceConstant( ReferenceConstant referenceConstant )
	{
		assert this.referenceConstant == null;
		assert referenceConstant != null;
		assert referenceConstant.tag == tag_FieldReference || referenceConstant.tag == tag_MethodReference || referenceConstant.tag == tag_InterfaceMethodReference;
		this.referenceConstant = referenceConstant;
	}

	public ConstantDesc constantDescriptor() { return descriptor(); }

	public DirectMethodHandleDesc descriptor()
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = referenceConstant.getNameAndDescriptorConstant();
		//I do not know why the isInterface parameter is needed, but it does not work otherwise.
		return MethodHandleDesc.of( DirectMethodHandleDesc.Kind.valueOf( referenceKind.number, referenceKind == ReferenceKind.InvokeInterface ), //
			referenceConstant.getDeclaringTypeConstant().classDesc(), //
			nameAndDescriptorConstant.getNameConstant().stringValue(), //
			nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "kind = " + referenceKind.name() + ", referenceConstant = " + referenceConstant;
	}

	@Deprecated @Override public MethodHandleConstant asMethodHandleConstant()
	{
		return this;
	}

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof MethodHandleConstant otherMethodHandleConstant )
			return equals( otherMethodHandleConstant );
		return false;
	}

	public boolean equals( MethodHandleConstant other )
	{
		if( referenceKind != other.referenceKind )
			return false;
		if( !referenceConstant.equalsReferenceConstant( other.referenceConstant ) )
			return false;
		return true;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, referenceKind, referenceConstant );
	}
}
