package mikenakis.bytecode.test;

import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.attributes.CodeAttribute;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import mikenakis.java_type_model.PrimitiveTypeDescriptor;
import mikenakis.java_type_model.TerminalTypeDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.Kit;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Generator
{
	public static ByteCodeType generateByteCodeType()
	{
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract ), //
			TerminalTypeDescriptor.ofTypeName( "test.HelloWorld" ), Optional.of( TerminalTypeDescriptor.of( Object.class ) ) );
		{
			ByteCodeMethod initMethod = byteCodeType.addMethod( ByteCodeMethod.of( //
				ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
				MethodPrototype.of( "<init>", PrimitiveTypeDescriptor.Void ) ) );
			CodeAttribute codeAttribute = initMethod.attributeSet.addAttribute( CodeAttribute.of( 1, 1 ) );
			codeAttribute.addALoad( 0 );
			codeAttribute.addInvokeSpecial( MethodReferenceKind.Plain, MethodReference.of( TypeDescriptor.of( Object.class ), //
				MethodPrototype.of( "<init>", PrimitiveTypeDescriptor.Void ) ) );
			codeAttribute.addReturn();
		}

		{
			ByteCodeMethod mainMethod = byteCodeType.addMethod( ByteCodeMethod.of( //
				ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public, ByteCodeMethod.Modifier.Static ), //
				MethodPrototype.of( "main", PrimitiveTypeDescriptor.Void, TypeDescriptor.of( String[].class ) ) ) );
			CodeAttribute codeAttribute = mainMethod.attributeSet.addAttribute( CodeAttribute.of( 2, 1 ) );
			codeAttribute.addGetStatic( FieldReference.of( TypeDescriptor.of( System.class ), FieldPrototype.of( "out", PrintStream.class ) ) );
			codeAttribute.addLdc( "Hello, world!\n" );
			codeAttribute.addInvokeVirtual( MethodReferenceKind.Plain, MethodReference.of( TypeDescriptor.of( PrintStream.class ), //
				MethodPrototype.of( "print", PrimitiveTypeDescriptor.Void, TypeDescriptor.of( String.class ) ) ) );
			codeAttribute.addReturn();
		}

		return byteCodeType;
	}

	public static void testGeneratedByteCodeType( Class<?> javaClass )
	{
		Method mainMethod = Kit.unchecked( () -> javaClass.getDeclaredMethod( "main", String[].class ) );
		//Object instance = Kit.newInstance( javaClass );
		String output = Kit.testing.withCapturedOutputStream( //
			() -> Kit.unchecked( () -> mainMethod.invoke( null/*instance*/, (Object)(new String[0]) ) ) );
		assert output.equals( "Hello, world!\n" );
	}
}
