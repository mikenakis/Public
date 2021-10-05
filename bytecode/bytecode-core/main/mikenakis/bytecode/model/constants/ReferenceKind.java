package mikenakis.bytecode.model.constants;

import mikenakis.kit.Kit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
