package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.AttributeSet;
import mikenakis.bytecode.model.ByteCodeMethod;
import mikenakis.bytecode.model.attributes.code.Instruction;
import mikenakis.bytecode.model.attributes.code.InstructionList;
import mikenakis.bytecode.model.attributes.code.OpCode;
import mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.descriptors.FieldReference;
import mikenakis.bytecode.model.descriptors.MethodReference;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.reading.ReadingLocationMap;
import mikenakis.bytecode.writing.FakeInstructionWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.RealInstructionWriter;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.bytecode.writing.WritingLocationMap;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the "Code" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeMethod}
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( { "NewMethodNamingConvention", "SpellCheckingInspection" } )
public final class CodeAttribute extends KnownAttribute
{
	public static CodeAttribute read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		int maxStack = bufferReader.readUnsignedShort();
		int maxLocals = bufferReader.readUnsignedShort();
		int codeLength = bufferReader.readInt();
		Buffer codeBuffer = bufferReader.readBuffer( codeLength );
		BufferReader codeBufferReader = BufferReader.of( codeBuffer );

		ReadingLocationMap locationMap = new ReadingLocationMap( codeBuffer.length() );

		List<Instruction> instructions = new ArrayList<>();
		while( !codeBufferReader.isAtEnd() )
		{
			int startLocation = codeBufferReader.getPosition();
			Instruction instruction = Instruction.read( codeBufferReader, constantPool, locationMap );
			int endLocation = codeBufferReader.getPosition();
			locationMap.add( startLocation, instruction, endLocation - startLocation );
			instructions.add( instruction );
		}
		locationMap.runFixUps();

		assert codeBufferReader.isAtEnd();
		int count = bufferReader.readUnsignedShort();
		List<ExceptionInfo> exceptionInfos = new ArrayList<>( count );
		for( int i = 0; i < count; i++ )
			exceptionInfos.add( ExceptionInfo.read( bufferReader, constantPool, locationMap ) );
		AttributeSet attributes = AttributeSet.read( bufferReader, constantPool, Optional.of( locationMap ) );
		return of( maxStack, maxLocals, instructions, exceptionInfos, attributes );
	}

	public static final String END_LABEL = "@end";

	public static CodeAttribute of( int maxStack, int maxLocals )
	{
		return of( maxStack, maxLocals, new ArrayList<>(), new ArrayList<>(), AttributeSet.of() );
	}

	public static CodeAttribute of( int maxStack, int maxLocals, List<Instruction> instructions, List<ExceptionInfo> exceptionInfos, AttributeSet attributeSet )
	{
		return new CodeAttribute( maxStack, maxLocals, instructions, exceptionInfos, attributeSet );
	}

	private int maxStack;
	private int maxLocals;
	public final List<ExceptionInfo> exceptionInfos;
	public final AttributeSet attributeSet;
	public final InstructionList instructions;

	private CodeAttribute( int maxStack, int maxLocals, List<Instruction> instructions, List<ExceptionInfo> exceptionInfos, AttributeSet attributeSet )
	{
		super( tag_Code );
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.instructions = InstructionList.of( instructions );
		this.exceptionInfos = exceptionInfos;
		this.attributeSet = attributeSet;
	}

	public int getMaxStack()
	{
		return maxStack;
	}

	public void setMaxStack( int value )
	{
		maxStack = value;
	}

	public int getMaxLocals()
	{
		return maxLocals;
	}

	public void setMaxLocals( int value )
	{
		maxLocals = value;
	}

	@Deprecated @Override public CodeAttribute asCodeAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "maxStack = " + maxStack + ", maxLocals = " + maxLocals + ", " + instructions.size() + " instructions, " + //
			exceptionInfos.size() + " exceptionInfos, " + attributeSet.size() + " attributes";
	}

	public InvokeInterfaceInstruction   /**/ INVOKEINTERFACE   /**/ ( MethodReference methodReference, int argumentCount )  /**/ { return add( InvokeInterfaceInstruction.of( methodReference, argumentCount ) ); }
	public InvokeDynamicInstruction     /**/ INVOKEDYNAMIC     /**/ ( InvokeDynamicConstant constant )                      /**/ { return add( InvokeDynamicInstruction.of( constant ) ); }
	public MultiANewArrayInstruction    /**/ MULTIANEWARRAY    /**/ ( ClassConstant constant, int dimensionCount )          /**/ { return add( MultiANewArrayInstruction.of( constant, dimensionCount ) ); }
	public FieldReferencingInstruction  /**/ GETSTATIC         /**/ ( FieldReference fieldReference )                       /**/ { return add( FieldReferencingInstruction.of ( OpCode.GETSTATIC, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ PUTSTATIC         /**/ ( FieldReference fieldReference )                       /**/ { return add( FieldReferencingInstruction.of ( OpCode.PUTSTATIC, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ GETFIELD          /**/ ( FieldReference fieldReference )                       /**/ { return add( FieldReferencingInstruction.of ( OpCode.GETFIELD, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ PUTFIELD          /**/ ( FieldReference fieldReference )                       /**/ { return add( FieldReferencingInstruction.of ( OpCode.PUTFIELD, fieldReference ) ); }
	public MethodReferencingInstruction /**/ INVOKEVIRTUAL     /**/ ( MethodReference methodReference )                     /**/ { return add( MethodReferencingInstruction.of ( OpCode.INVOKEVIRTUAL, methodReference ) ); }
	public MethodReferencingInstruction /**/ INVOKESPECIAL     /**/ ( MethodReference methodReference )                     /**/ { return add( MethodReferencingInstruction.of ( OpCode.INVOKESPECIAL, methodReference ) ); }
	public MethodReferencingInstruction /**/ INVOKESTATIC      /**/ ( MethodReference methodReference )                     /**/ { return add( MethodReferencingInstruction.of ( OpCode.INVOKESTATIC, methodReference ) ); }
	public ClassReferencingInstruction  /**/ NEW               /**/ ( TypeDescriptor typeDescriptor )                       /**/ { return add( ClassReferencingInstruction.of ( OpCode.NEW, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ ANEWARRAY         /**/ ( TypeDescriptor typeDescriptor )                       /**/ { return add( ClassReferencingInstruction.of ( OpCode.ANEWARRAY, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ CHECKCAST         /**/ ( TypeDescriptor typeDescriptor )                       /**/ { return add( ClassReferencingInstruction.of ( OpCode.CHECKCAST, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ INSTANCEOF        /**/ ( TypeDescriptor typeDescriptor )                       /**/ { return add( ClassReferencingInstruction.of ( OpCode.INSTANCEOF, typeDescriptor ) ); }
	public LocalVariableInstruction     /**/ ILOAD             /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.ILOAD, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ LLOAD             /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.LLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ FLOAD             /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.FLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ DLOAD             /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.DLOAD, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ALOAD             /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.ALOAD, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ISTORE            /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.ISTORE, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ LSTORE            /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.LSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ FSTORE            /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.FSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ DSTORE            /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.DSTORE, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ASTORE            /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.ASTORE, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ RET               /**/ ( int localVariableIndex )                              /**/ { return add( LocalVariableInstruction.of( OpCode.RET, localVariableIndex ) ); }
	public IIncInstruction              /**/ IINC              /**/ ( int localVariableIndex, int delta )                   /**/ { return add( IIncInstruction.of( localVariableIndex, delta ) ); }
	public LoadConstantInstruction      /**/ LDC               /**/ ( boolean constant )                                    /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC               /**/ ( int constant )                                        /**/ { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction      /**/ LDC               /**/ ( long constant )                                       /**/ { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction      /**/ LDC               /**/ ( float constant )                                      /**/ { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction      /**/ LDC               /**/ ( double constant )                                     /**/ { return add( LoadConstantInstruction.of( constant ) );	}
	public LoadConstantInstruction      /**/ LDC               /**/ ( String constant )                                     /**/ { return add( LoadConstantInstruction.of( constant ) );	}
	public ConditionalBranchInstruction /**/ IFEQ              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFEQ ) ); }
	public ConditionalBranchInstruction /**/ IFNE              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNE ) ); }
	public ConditionalBranchInstruction /**/ IFLT              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFLT ) ); }
	public ConditionalBranchInstruction /**/ IFGE              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFGE ) ); }
	public ConditionalBranchInstruction /**/ IFGT              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFGT ) ); }
	public ConditionalBranchInstruction /**/ IFLE              /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFLE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPEQ          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPEQ ) ); }
	public ConditionalBranchInstruction /**/ IFICMPNE          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPNE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPLT          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLT ) ); }
	public ConditionalBranchInstruction /**/ IFICMPGE          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPGT          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGT ) ); }
	public ConditionalBranchInstruction /**/ IFICMPLE          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLE ) ); }
	public ConditionalBranchInstruction /**/ IFNULL            /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNULL ) ); }
	public ConditionalBranchInstruction /**/ IFNONNULL         /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNONNULL ) ); }
	public ConditionalBranchInstruction /**/ IFACMPEQ          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPEQ ) ); }
	public ConditionalBranchInstruction /**/ IFACMPNE          /**/ ()                                                      /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPNE ) ); }
	public BranchInstruction            /**/ GOTO              /**/ ()                                                      /**/ { return add( BranchInstruction.of( OpCode.GOTO ) ); }
	public BranchInstruction            /**/ JSR               /**/ ()                                                      /**/ { return add( BranchInstruction.of( OpCode.JSR ) ); }
	public LookupSwitchInstruction      /**/ LOOKUPSWITCH      /**/ ()                                                      /**/ { return add( LookupSwitchInstruction.of() ); }
	public TableSwitchInstruction       /**/ TABLESWITCH       /**/ ( int lowValue )                                        /**/ { return add( TableSwitchInstruction.of( 0, lowValue ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_boolean  /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.BOOLEAN ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_char     /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.CHAR ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_float    /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.FLOAT  ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_double   /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.DOUBLE ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_byte     /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.BYTE ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_short    /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.SHORT ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_int      /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.INT ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY_long     /**/ ()                                                      /**/ { return add( NewPrimitiveArrayInstruction.of( NewPrimitiveArrayInstruction.Type.LONG ) ); }
	public OperandlessInstruction       /**/ NOP               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.NOP ) ); }
	public OperandlessInstruction       /**/ ACONST_NULL       /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ACONST_NULL ) ); }
	public OperandlessInstruction       /**/ IALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IALOAD ) ); }
	public OperandlessInstruction       /**/ LALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LALOAD ) ); }
	public OperandlessInstruction       /**/ FALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FALOAD ) ); }
	public OperandlessInstruction       /**/ DALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DALOAD ) ); }
	public OperandlessInstruction       /**/ AALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.AALOAD ) ); }
	public OperandlessInstruction       /**/ BALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.BALOAD ) ); }
	public OperandlessInstruction       /**/ CALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.CALOAD ) ); }
	public OperandlessInstruction       /**/ SALOAD            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.SALOAD ) ); }
	public OperandlessInstruction       /**/ IASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IASTORE ) ); }
	public OperandlessInstruction       /**/ LASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LASTORE ) ); }
	public OperandlessInstruction       /**/ FASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FASTORE ) ); }
	public OperandlessInstruction       /**/ DASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DASTORE ) ); }
	public OperandlessInstruction       /**/ AASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.AASTORE ) ); }
	public OperandlessInstruction       /**/ BASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.BASTORE ) ); }
	public OperandlessInstruction       /**/ CASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.CASTORE ) ); }
	public OperandlessInstruction       /**/ SASTORE           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.SASTORE ) ); }
	public OperandlessInstruction       /**/ POP               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.POP ) ); }
	public OperandlessInstruction       /**/ POP2                 /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.POP2 ) ); }
	public OperandlessInstruction       /**/ DUP               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP ) ); }
	public OperandlessInstruction       /**/ DUPX1             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP_X1 ) ); }
	public OperandlessInstruction       /**/ DUPX2             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP_X2 ) ); }
	public OperandlessInstruction       /**/ DUP2              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP2 ) ); }
	public OperandlessInstruction       /**/ DUP2X1            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP2_X1 ) ); }
	public OperandlessInstruction       /**/ DUP2X2            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DUP2_X2 ) ); }
	public OperandlessInstruction       /**/ SWAP              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.SWAP ) ); }
	public OperandlessInstruction       /**/ IADD              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IADD ) ); }
	public OperandlessInstruction       /**/ LADD              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LADD ) ); }
	public OperandlessInstruction       /**/ FADD              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FADD ) ); }
	public OperandlessInstruction       /**/ DADD              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DADD ) ); }
	public OperandlessInstruction       /**/ ISUB              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ISUB ) ); }
	public OperandlessInstruction       /**/ LSUB              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LSUB ) ); }
	public OperandlessInstruction       /**/ FSUB              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FSUB ) ); }
	public OperandlessInstruction       /**/ DSUB              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DSUB ) ); }
	public OperandlessInstruction       /**/ IMUL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IMUL ) ); }
	public OperandlessInstruction       /**/ LMUL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LMUL ) ); }
	public OperandlessInstruction       /**/ FMUL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FMUL ) ); }
	public OperandlessInstruction       /**/ DMUL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DMUL ) ); }
	public OperandlessInstruction       /**/ IDIV              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IDIV ) ); }
	public OperandlessInstruction       /**/ LDIV              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LDIV ) ); }
	public OperandlessInstruction       /**/ FDIV              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FDIV ) ); }
	public OperandlessInstruction       /**/ DDIV              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DDIV ) ); }
	public OperandlessInstruction       /**/ IREM              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IREM ) ); }
	public OperandlessInstruction       /**/ LREM              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LREM ) ); }
	public OperandlessInstruction       /**/ FREM              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FREM ) ); }
	public OperandlessInstruction       /**/ DREM              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DREM ) ); }
	public OperandlessInstruction       /**/ INEG              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.INEG ) ); }
	public OperandlessInstruction       /**/ LNEG              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LNEG ) ); }
	public OperandlessInstruction       /**/ FNEG              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FNEG ) ); }
	public OperandlessInstruction       /**/ DNEG              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DNEG ) ); }
	public OperandlessInstruction       /**/ ISHL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ISHL ) ); }
	public OperandlessInstruction       /**/ LSHL              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LSHL ) ); }
	public OperandlessInstruction       /**/ ISHR              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ISHR ) ); }
	public OperandlessInstruction       /**/ LSHR              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LSHR ) ); }
	public OperandlessInstruction       /**/ IUSHR             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IUSHR ) ); }
	public OperandlessInstruction       /**/ LUSHR             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LUSHR ) ); }
	public OperandlessInstruction       /**/ IAND              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IAND ) ); }
	public OperandlessInstruction       /**/ LAND              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LAND ) ); }
	public OperandlessInstruction       /**/ IOR               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IOR ) ); }
	public OperandlessInstruction       /**/ LOR               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LOR ) ); }
	public OperandlessInstruction       /**/ IXOR              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IXOR ) ); }
	public OperandlessInstruction       /**/ LXOR              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LXOR ) ); }
	public OperandlessInstruction       /**/ I2L               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2L ) ); }
	public OperandlessInstruction       /**/ I2F               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2F ) ); }
	public OperandlessInstruction       /**/ I2D               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2D ) ); }
	public OperandlessInstruction       /**/ L2I               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.L2I ) ); }
	public OperandlessInstruction       /**/ L2F               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.L2F ) ); }
	public OperandlessInstruction       /**/ L2D               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.L2D ) ); }
	public OperandlessInstruction       /**/ F2I               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.F2I ) ); }
	public OperandlessInstruction       /**/ F2L               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.F2L ) ); }
	public OperandlessInstruction       /**/ F2D               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.F2D ) ); }
	public OperandlessInstruction       /**/ D2I               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.D2I ) ); }
	public OperandlessInstruction       /**/ D2L               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.D2L ) ); }
	public OperandlessInstruction       /**/ D2F               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.D2F ) ); }
	public OperandlessInstruction       /**/ I2B               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2B ) ); }
	public OperandlessInstruction       /**/ I2C               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2C ) ); }
	public OperandlessInstruction       /**/ I2S               /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.I2S ) ); }
	public OperandlessInstruction       /**/ LCMP              /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LCMP ) ); }
	public OperandlessInstruction       /**/ FCMPL             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FCMPL ) ); }
	public OperandlessInstruction       /**/ FCMPG             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FCMPG ) ); }
	public OperandlessInstruction       /**/ DCMPL             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DCMPL ) ); }
	public OperandlessInstruction       /**/ DCMPG             /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DCMPG ) ); }
	public OperandlessInstruction       /**/ IRETURN           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.IRETURN ) ); }
	public OperandlessInstruction       /**/ LRETURN           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.LRETURN ) ); }
	public OperandlessInstruction       /**/ FRETURN           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.FRETURN ) ); }
	public OperandlessInstruction       /**/ DRETURN           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.DRETURN ) ); }
	public OperandlessInstruction       /**/ ARETURN           /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ARETURN ) ); }
	public OperandlessInstruction       /**/ RETURN            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.RETURN ) ); }
	public OperandlessInstruction       /**/ ARRAYLENGTH       /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ARRAYLENGTH ) ); }
	public OperandlessInstruction       /**/ ATHROW            /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.ATHROW ) ); }
	public OperandlessInstruction       /**/ MONITORENTER      /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.MONITORENTER ) ); }
	public OperandlessInstruction       /**/ MONITOREXIT       /**/ ()                                                      /**/ { return add( OperandlessInstruction.of( OpCode.MONITOREXIT ) ); }

	private <T extends Instruction> T add( T instruction )
	{
		instructions.add( instruction );
		return instruction;
	}

	@Override public void intern( Interner interner )
	{
		for( ExceptionInfo exceptionInfo : exceptionInfos )
			exceptionInfo.intern( interner );

		attributeSet.intern( interner );

		for( Instruction instruction : instructions.all() )
			instruction.intern( interner );
	}

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool, Optional<WritingLocationMap> locationMap )
	{
		assert locationMap.isEmpty();

		bufferWriter.writeUnsignedShort( maxStack );
		bufferWriter.writeUnsignedShort( maxLocals );

		locationMap = Optional.of( getLocationMap( instructions.all(), constantPool ) );

		RealInstructionWriter instructionWriter = new RealInstructionWriter( locationMap.get(), constantPool );
		for( Instruction instruction : instructions.all() )
			instruction.write( instructionWriter );
		byte[] bytes = instructionWriter.toBytes();

		bufferWriter.writeInt( bytes.length );
		bufferWriter.writeBytes( bytes );

		bufferWriter.writeUnsignedShort( exceptionInfos.size() );
		for( ExceptionInfo exceptionInfo : exceptionInfos )
			exceptionInfo.write( bufferWriter, constantPool, locationMap.get() );

		attributeSet.write( bufferWriter, constantPool, locationMap );
	}

	private static WritingLocationMap getLocationMap( Iterable<Instruction> instructions, WritingConstantPool constantPool )
	{
		WritingLocationMap writingLocationMap = new WritingLocationMap();
		FakeInstructionWriter instructionWriter = new FakeInstructionWriter( constantPool, writingLocationMap );
		for( Instruction instruction : instructions )
		{
			int startLocation = instructionWriter.location;
			writingLocationMap.add( instruction );
			instruction.write( instructionWriter );
			int length = instructionWriter.location - startLocation;
			writingLocationMap.setLength( instruction, length );
		}

		for( ; ; )
		{
			boolean anyWorkDone = false;
			for( Instruction instruction : instructionWriter.sourceInstructions )
			{
				int location = writingLocationMap.getLocation( instruction );
				instructionWriter.location = location;
				int oldLength = writingLocationMap.getLength( instruction );
				instruction.write( instructionWriter );
				int newLength = instructionWriter.location - location;
				assert newLength <= oldLength;
				if( newLength == oldLength )
					continue;
				writingLocationMap.removeBytes( location + newLength, oldLength - newLength );
				anyWorkDone = true;
			}
			if( !anyWorkDone )
				break;
		}

		return writingLocationMap;
	}
}
