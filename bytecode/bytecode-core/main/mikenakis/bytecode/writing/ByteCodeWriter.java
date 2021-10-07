package mikenakis.bytecode.writing;

import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.attributes.KnownAttribute;
import mikenakis.bytecode.model.attributes.code.Instruction;

public class ByteCodeWriter
{
	public static byte[] write( ByteCodeType byteCodeType )
	{
		ByteCodeWriter byteCodeWriter = new ByteCodeWriter();

		/* first intern all bootstrap instructions to collect all bootstrap methods */
		for( ByteCodeMethod method : byteCodeType.methods )
		{
			method.attributeSet.tryGetKnownAttributeByTag( KnownAttribute.tag_Code ) //
				.map( attribute -> attribute.asCodeAttribute() ) //
				.ifPresent( codeAttribute -> //
				{
					for( Instruction instruction : codeAttribute.instructions.all() )
						if( instruction.groupTag == Instruction.groupTag_InvokeDynamic )
							byteCodeWriter.bootstrapPool.intern( instruction.asInvokeDynamicInstruction().bootstrapMethod() );
				} );
		}

		/* if any bootstrap methods were collected, add the bootstrap methods attribute */
		if( !byteCodeWriter.bootstrapPool.bootstrapMethods().isEmpty() )
		{
			BootstrapMethodsAttribute bootstrapMethodsAttribute = BootstrapMethodsAttribute.of();
			bootstrapMethodsAttribute.bootstrapMethods.addAll( byteCodeWriter.bootstrapPool.bootstrapMethods() ); // TODO perhaps replace the bootstrapPool with the bootstrapMethodsAttribute
			byteCodeType.attributeSet.addOrReplaceAttribute( bootstrapMethodsAttribute );
		}

		/* now intern everything, including the attributes, including the newly added bootstrap methods attribute */
		byteCodeType.intern( byteCodeWriter.constantPool );

		//TODO: optimize the constant pool by moving the constants most frequently used by the IndirectLoadConstantInstruction to the first 256 entries!
		byteCodeWriter.bufferWriter.writeInt( ByteCodeType.MAGIC );
		byteCodeWriter.bufferWriter.writeUnsignedShort( byteCodeType.version.minor() );
		byteCodeWriter.bufferWriter.writeUnsignedShort( byteCodeType.version.major() );
		byteCodeWriter.bufferWriter.writeUnsignedShort( byteCodeWriter.constantPool.size() );
		ConstantWriter constantWriter = new ConstantWriter( byteCodeWriter.bufferWriter, byteCodeWriter.constantPool, byteCodeWriter.bootstrapPool );
		for( Constant constant : byteCodeWriter.constantPool.constants() )
			constant.write( constantWriter );
		byteCodeType.write( constantWriter );
		return byteCodeWriter.bytes();
	}

	private final BufferWriter bufferWriter = new BufferWriter();
	private final ConstantPool constantPool = new ConstantPool();
	private final BootstrapPool bootstrapPool = new BootstrapPool();

	private ByteCodeWriter()
	{
	}

	private byte[] bytes()
	{
		return bufferWriter.toBytes();
	}
}
