package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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

	public static MethodHandleConstant of( ReferenceKind referenceKind, ReferenceConstant referenceConstant )
	{
		return new MethodHandleConstant( referenceKind, referenceConstant );
	}

	public static final int TAG = 15; // JVMS::CONSTANT_MethodHandle_info
	public static final String tagName = "MethodHandle";

	private final ReferenceKind referenceKind;
	private final ReferenceConstant referenceConstant;

	private MethodHandleConstant( ReferenceKind referenceKind, ReferenceConstant referenceConstant )
	{
		super( TAG );
		//FIXME XXX TODO how come this used to work but now fails? assert referenceConstant.kind == PlainMethodReferenceConstant.KIND || referenceConstant.kind == InterfaceMethodReferenceConstant.KIND;
		this.referenceKind = referenceKind;
		this.referenceConstant = referenceConstant;
	}

	public ReferenceKind referenceKind() { return referenceKind; }
	public ReferenceConstant referenceConstant() { return referenceConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "kind = " + referenceKind.name() + ", referenceConstant = " + referenceConstant;
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
		return Objects.hash( tag, referenceKind, referenceConstant );
	}
}
