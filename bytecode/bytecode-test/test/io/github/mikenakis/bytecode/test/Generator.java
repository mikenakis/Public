package io.github.mikenakis.bytecode.test;

import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.ByteCodeType;
import io.github.mikenakis.bytecode.model.attributes.CodeAttribute;
import io.github.mikenakis.bytecode.model.descriptors.FieldPrototype;
import io.github.mikenakis.bytecode.model.descriptors.FieldReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodPrototype;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodReferenceKind;
import io.github.mikenakis.java_type_model.MethodDescriptor;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * test.
 *
 * @author michael.gr
 */
public class Generator
{
	public static ByteCodeType generateByteCodeType()
	{
		ByteCodeType byteCodeType = ByteCodeType.of( //
			ByteCodeType.modifierEnum.of( ByteCodeType.Modifier.Public, ByteCodeType.Modifier.Super, ByteCodeType.Modifier.Abstract ), //
			TerminalTypeDescriptor.of( "test.HelloWorld" ), Optional.of( TerminalTypeDescriptor.of( Object.class ) ), List.of() );
		{
			MethodPrototype parameterlessConstructorPrototype = MethodPrototype.of( "<init>", MethodDescriptor.of( void.class ) );
			ByteCodeMethod initMethod = byteCodeType.addMethod( ByteCodeMethod.of( //
				ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public ), //
				parameterlessConstructorPrototype ) );
			CodeAttribute codeAttribute = initMethod.attributeSet.addAttribute( CodeAttribute.of( 1, 1 ) );
			codeAttribute.ALOAD( 0 );
			codeAttribute.INVOKESPECIAL( MethodReference.of( MethodReferenceKind.Plain, TypeDescriptor.of( Object.class ), //
				parameterlessConstructorPrototype ) );
			codeAttribute.RETURN();
		}

		{
			ByteCodeMethod mainMethod = byteCodeType.addMethod( ByteCodeMethod.of( //
				ByteCodeMethod.modifierEnum.of( ByteCodeMethod.Modifier.Public, ByteCodeMethod.Modifier.Static ), //
				MethodPrototype.of( "main", MethodDescriptor.of( void.class, String[].class ) ) ) );
			CodeAttribute codeAttribute = mainMethod.attributeSet.addAttribute( CodeAttribute.of( 2, 1 ) );
			codeAttribute.GETSTATIC( FieldReference.of( TypeDescriptor.of( System.class ), FieldPrototype.of( "out", PrintStream.class ) ) );
			codeAttribute.LDC( "Hello, world!\n" );
			codeAttribute.INVOKEVIRTUAL( MethodReference.of( MethodReferenceKind.Plain, TypeDescriptor.of( PrintStream.class ), //
				MethodPrototype.of( "print", MethodDescriptor.of( void.class, String.class ) ) ) );
			codeAttribute.RETURN();
		}

		return byteCodeType;
	}

	public static void testGeneratedByteCodeType( Class<?> javaClass )
	{
		Method mainMethod = Kit.unchecked( () -> javaClass.getDeclaredMethod( "main", String[].class ) );
		//Object instance = Kit.newInstance( javaClass );
		String output = TestKit.withCapturedOutputStream( //
			() -> Kit.unchecked( () -> mainMethod.invoke( null/*instance*/, (Object)(new String[0]) ) ) );
		assert output.equals( "Hello, world!\n" );
	}
}
