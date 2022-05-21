package io.github.mikenakis.bytecode.model.attributes;

import io.github.mikenakis.bytecode.kit.Buffer;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.Attribute;
import io.github.mikenakis.bytecode.model.AttributeSet;
import io.github.mikenakis.bytecode.model.ByteCodeMethod;
import io.github.mikenakis.bytecode.model.attributes.code.Instruction;
import io.github.mikenakis.bytecode.model.attributes.code.InstructionList;
import io.github.mikenakis.bytecode.model.attributes.code.OpCode;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.BranchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.ClassReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.ConditionalBranchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.FieldReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.IIncInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.InvokeDynamicInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.InvokeInterfaceInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LoadConstantInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LocalVariableInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.LookupSwitchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.MethodReferencingInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.MultiANewArrayInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.NewPrimitiveArrayInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.OperandlessInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.TableSwitchInstruction;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.Type;
import io.github.mikenakis.bytecode.model.attributes.code.instructions.TypedOperandlessInstruction;
import io.github.mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import io.github.mikenakis.bytecode.model.descriptors.FieldReference;
import io.github.mikenakis.bytecode.model.descriptors.MethodReference;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.reading.ReadingLocationMap;
import io.github.mikenakis.bytecode.writing.FakeInstructionWriter;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.RealInstructionWriter;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.writing.WritingLocationMap;
import io.github.mikenakis.java_type_model.TerminalTypeDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
 * @author michael.gr
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

	public InvokeInterfaceInstruction   /**/ INVOKEINTERFACE /**/( MethodReference methodReference, int argumentCount )    /**/ { return add( InvokeInterfaceInstruction.of( methodReference, argumentCount ) ); }
	public InvokeDynamicInstruction     /**/ INVOKEDYNAMIC   /**/( InvokeDynamicConstant constant )                        /**/ { return add( InvokeDynamicInstruction.of( constant ) ); }
	public MultiANewArrayInstruction    /**/ MULTIANEWARRAY  /**/( TerminalTypeDescriptor typeDescriptor, int dimensions ) /**/ { return add( MultiANewArrayInstruction.of( typeDescriptor, dimensions ) ); }
	public FieldReferencingInstruction  /**/ GETSTATIC       /**/( FieldReference fieldReference )                         /**/ { return add( FieldReferencingInstruction.of( OpCode.GETSTATIC, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ PUTSTATIC       /**/( FieldReference fieldReference )                         /**/ { return add( FieldReferencingInstruction.of( OpCode.PUTSTATIC, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ GETFIELD        /**/( FieldReference fieldReference )                         /**/ { return add( FieldReferencingInstruction.of( OpCode.GETFIELD, fieldReference ) ); }
	public FieldReferencingInstruction  /**/ PUTFIELD        /**/( FieldReference fieldReference )                         /**/ { return add( FieldReferencingInstruction.of( OpCode.PUTFIELD, fieldReference ) ); }
	public MethodReferencingInstruction /**/ INVOKEVIRTUAL   /**/( MethodReference methodReference )                       /**/ { return add( MethodReferencingInstruction.of( OpCode.INVOKEVIRTUAL, methodReference ) ); }
	public MethodReferencingInstruction /**/ INVOKESPECIAL   /**/( MethodReference methodReference )                       /**/ { return add( MethodReferencingInstruction.of( OpCode.INVOKESPECIAL, methodReference ) ); }
	public MethodReferencingInstruction /**/ INVOKESTATIC    /**/( MethodReference methodReference )                       /**/ { return add( MethodReferencingInstruction.of( OpCode.INVOKESTATIC, methodReference ) ); }
	public ClassReferencingInstruction  /**/ NEW             /**/( TypeDescriptor typeDescriptor )                         /**/ { return add( ClassReferencingInstruction.of( OpCode.NEW, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ ANEWARRAY       /**/( TypeDescriptor typeDescriptor )                         /**/ { return add( ClassReferencingInstruction.of( OpCode.ANEWARRAY, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ CHECKCAST       /**/( TypeDescriptor typeDescriptor )                         /**/ { return add( ClassReferencingInstruction.of( OpCode.CHECKCAST, typeDescriptor ) ); }
	public ClassReferencingInstruction  /**/ INSTANCEOF      /**/( TypeDescriptor typeDescriptor )                         /**/ { return add( ClassReferencingInstruction.of( OpCode.INSTANCEOF, typeDescriptor ) ); }
	public LocalVariableInstruction     /**/ xLOAD           /**/( Type type, int localVariableIndex )                     /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, type         /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ILOAD           /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, Type.Int     /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ LLOAD           /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, Type.Long    /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ FLOAD           /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, Type.Float   /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ DLOAD           /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, Type.Double  /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ALOAD           /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Load  /**/, Type.Address /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ xSTORE          /**/( Type type, int localVariableIndex )                     /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, type         /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ISTORE          /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, Type.Int     /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ LSTORE          /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, Type.Long    /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ FSTORE          /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, Type.Float   /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ DSTORE          /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, Type.Double  /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ ASTORE          /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Store /**/, Type.Address /**/, localVariableIndex ) ); }
	public LocalVariableInstruction     /**/ RET             /**/( int localVariableIndex )                                /**/ { return add( LocalVariableInstruction.of( LocalVariableInstruction.Operation.Ret   /**/, Type.None    /**/, localVariableIndex ) ); }
	public IIncInstruction              /**/ IINC            /**/( int localVariableIndex, int delta )                     /**/ { return add( IIncInstruction.of( localVariableIndex, delta ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( boolean constant )                                      /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( int constant )                                          /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( long constant )                                         /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( float constant )                                        /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( double constant )                                       /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( String constant )                                       /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public LoadConstantInstruction      /**/ LDC             /**/( TypeDescriptor constant )                               /**/ { return add( LoadConstantInstruction.of( constant ) ); }
	public ConditionalBranchInstruction /**/ IFEQ            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFEQ ) ); }
	public ConditionalBranchInstruction /**/ IFNE            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNE ) ); }
	public ConditionalBranchInstruction /**/ IFLT            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFLT ) ); }
	public ConditionalBranchInstruction /**/ IFGE            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFGE ) ); }
	public ConditionalBranchInstruction /**/ IFGT            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFGT ) ); }
	public ConditionalBranchInstruction /**/ IFLE            /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFLE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPEQ        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPEQ ) ); }
	public ConditionalBranchInstruction /**/ IFICMPNE        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPNE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPLT        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLT ) ); }
	public ConditionalBranchInstruction /**/ IFICMPGE        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGE ) ); }
	public ConditionalBranchInstruction /**/ IFICMPGT        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPGT ) ); }
	public ConditionalBranchInstruction /**/ IFICMPLE        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ICMPLE ) ); }
	public ConditionalBranchInstruction /**/ IFNULL          /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNULL ) ); }
	public ConditionalBranchInstruction /**/ IFNONNULL       /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IFNONNULL ) ); }
	public ConditionalBranchInstruction /**/ IFACMPEQ        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPEQ ) ); }
	public ConditionalBranchInstruction /**/ IFACMPNE        /**/()                                                        /**/ { return add( ConditionalBranchInstruction.of( OpCode.IF_ACMPNE ) ); }
	public BranchInstruction            /**/ GOTO            /**/()                                                        /**/ { return add( BranchInstruction.of( OpCode.GOTO ) ); }
	public BranchInstruction            /**/ JSR             /**/()                                                        /**/ { return add( BranchInstruction.of( OpCode.JSR ) ); }
	public LookupSwitchInstruction      /**/ LOOKUPSWITCH    /**/()                                                        /**/ { return add( LookupSwitchInstruction.of() ); }
	public TableSwitchInstruction       /**/ TABLESWITCH     /**/( int lowValue )                                          /**/ { return add( TableSwitchInstruction.of( 0, lowValue ) ); }
	public NewPrimitiveArrayInstruction /**/ NEWARRAY        /**/( Type primitiveType )                                    /**/ { return add( NewPrimitiveArrayInstruction.of( primitiveType ) ); }
	public OperandlessInstruction       /**/ NOP             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.NOP ) ); }
	public OperandlessInstruction       /**/ ACONST_NULL     /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.ACONST_NULL ) ); }
	public TypedOperandlessInstruction  /**/ IALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Int     /**/ ) ); } //OpCode.IALOAD ) ); }
	public TypedOperandlessInstruction  /**/ LALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Long    /**/ ) ); } //OpCode.LALOAD ) ); }
	public TypedOperandlessInstruction  /**/ FALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Float   /**/ ) ); } //OpCode.FALOAD ) ); }
	public TypedOperandlessInstruction  /**/ DALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Double  /**/ ) ); } //OpCode.DALOAD ) ); }
	public TypedOperandlessInstruction  /**/ AALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Address /**/ ) ); } //OpCode.AALOAD ) ); }
	public TypedOperandlessInstruction  /**/ BALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Byte    /**/ ) ); } //OpCode.BALOAD ) ); }
	public TypedOperandlessInstruction  /**/ CALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Char    /**/ ) ); } //OpCode.CALOAD ) ); }
	public TypedOperandlessInstruction  /**/ SALOAD          /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, Type.Short   /**/ ) ); } //OpCode.SALOAD ) ); }
	public TypedOperandlessInstruction  /**/ xALOAD          /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Load  /**/, type         /**/ ) ); }
	public TypedOperandlessInstruction  /**/ IASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Int     /**/ ) ); } //OpCode.IASTORE ) ); }
	public TypedOperandlessInstruction  /**/ LASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Long    /**/ ) ); } //OpCode.LASTORE ) ); }
	public TypedOperandlessInstruction  /**/ FASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Float   /**/ ) ); } //OpCode.FASTORE ) ); }
	public TypedOperandlessInstruction  /**/ DASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Double  /**/ ) ); } //OpCode.DASTORE ) ); }
	public TypedOperandlessInstruction  /**/ AASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Address /**/ ) ); } //OpCode.AASTORE ) ); }
	public TypedOperandlessInstruction  /**/ BASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Byte    /**/ ) ); } //OpCode.BASTORE ) ); }
	public TypedOperandlessInstruction  /**/ CASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Char    /**/ ) ); } //OpCode.CASTORE ) ); }
	public TypedOperandlessInstruction  /**/ SASTORE         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, Type.Short   /**/ ) ); } //OpCode.SASTORE ) ); }
	public TypedOperandlessInstruction  /**/ xASTORE         /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Store /**/, type         /**/ ) ); }
	public OperandlessInstruction       /**/ POP             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.POP ) ); }
	public OperandlessInstruction       /**/ POP2            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.POP2 ) ); }
	public OperandlessInstruction       /**/ DUP             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP ) ); }
	public OperandlessInstruction       /**/ DUPX1           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP_X1 ) ); }
	public OperandlessInstruction       /**/ DUPX2           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP_X2 ) ); }
	public OperandlessInstruction       /**/ DUP2            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP2 ) ); }
	public OperandlessInstruction       /**/ DUP2X1          /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP2_X1 ) ); }
	public OperandlessInstruction       /**/ DUP2X2          /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DUP2_X2 ) ); }
	public OperandlessInstruction       /**/ SWAP            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.SWAP ) ); }
	public TypedOperandlessInstruction  /**/ IADD            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Add, Type.Int    /**/ ) ); } //OpCode.IADD ) ); }
	public TypedOperandlessInstruction  /**/ LADD            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Add, Type.Long   /**/ ) ); } //OpCode.LADD ) ); }
	public TypedOperandlessInstruction  /**/ FADD            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Add, Type.Float  /**/ ) ); } //OpCode.FADD ) ); }
	public TypedOperandlessInstruction  /**/ DADD            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Add, Type.Double /**/ ) ); } //OpCode.DADD ) ); }
	public TypedOperandlessInstruction  /**/ xADD            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Add, type        /**/ ) ); }
	public TypedOperandlessInstruction  /**/ ISUB            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Sub, Type.Int    /**/ ) ); } //OpCode.ISUB ) ); }
	public TypedOperandlessInstruction  /**/ LSUB            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Sub, Type.Long   /**/ ) ); } //OpCode.LSUB ) ); }
	public TypedOperandlessInstruction  /**/ FSUB            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Sub, Type.Float  /**/ ) ); } //OpCode.FSUB ) ); }
	public TypedOperandlessInstruction  /**/ DSUB            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Sub, Type.Double /**/ ) ); } //OpCode.DSUB ) ); }
	public TypedOperandlessInstruction  /**/ xSUB            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Sub, type        /**/ ) ); }
	public TypedOperandlessInstruction  /**/ IMUL            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Mul, Type.Int    /**/ ) ); } //OpCode.IMUL ) ); }
	public TypedOperandlessInstruction  /**/ LMUL            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Mul, Type.Long   /**/ ) ); } //OpCode.LMUL ) ); }
	public TypedOperandlessInstruction  /**/ FMUL            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Mul, Type.Float  /**/ ) ); } //OpCode.FMUL ) ); }
	public TypedOperandlessInstruction  /**/ DMUL            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Mul, Type.Double /**/ ) ); } //OpCode.DMUL ) ); }
	public TypedOperandlessInstruction  /**/ xMUL            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Mul, type        /**/ ) ); }
	public TypedOperandlessInstruction  /**/ IDIV            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Div, Type.Int    /**/ ) ); } //OpCode.IDIV ) ); }
	public TypedOperandlessInstruction  /**/ LDIV            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Div, Type.Long   /**/ ) ); } //OpCode.LDIV ) ); }
	public TypedOperandlessInstruction  /**/ FDIV            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Div, Type.Float  /**/ ) ); } //OpCode.FDIV ) ); }
	public TypedOperandlessInstruction  /**/ DDIV            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Div, Type.Double /**/ ) ); } //OpCode.DDIV ) ); }
	public TypedOperandlessInstruction  /**/ xDIV            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Div, type        /**/ ) ); }
	public TypedOperandlessInstruction  /**/ IREM            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Rem, Type.Int    /**/ ) ); } //OpCode.IREM ) ); }
	public TypedOperandlessInstruction  /**/ LREM            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Rem, Type.Long   /**/ ) ); } //OpCode.LREM ) ); }
	public TypedOperandlessInstruction  /**/ FREM            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Rem, Type.Float  /**/ ) ); } //OpCode.FREM ) ); }
	public TypedOperandlessInstruction  /**/ DREM            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Rem, Type.Double /**/ ) ); } //OpCode.DREM ) ); }
	public TypedOperandlessInstruction  /**/ xREM            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Rem, type        /**/ ) ); }
	public TypedOperandlessInstruction  /**/ INEG            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Neg, Type.Int    /**/ ) ); } //OpCode.INEG ) ); }
	public TypedOperandlessInstruction  /**/ LNEG            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Neg, Type.Long   /**/ ) ); } //OpCode.LNEG ) ); }
	public TypedOperandlessInstruction  /**/ FNEG            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Neg, Type.Float  /**/ ) ); } //OpCode.FNEG ) ); }
	public TypedOperandlessInstruction  /**/ DNEG            /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Neg, Type.Double /**/ ) ); } //OpCode.DNEG ) ); }
	public TypedOperandlessInstruction  /**/ xNEG            /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Neg, type        /**/ ) ); }
	public OperandlessInstruction       /**/ ISHL            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.ISHL ) ); }
	public OperandlessInstruction       /**/ LSHL            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LSHL ) ); }
	public OperandlessInstruction       /**/ ISHR            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.ISHR ) ); }
	public OperandlessInstruction       /**/ LSHR            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LSHR ) ); }
	public OperandlessInstruction       /**/ IUSHR           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.IUSHR ) ); }
	public OperandlessInstruction       /**/ LUSHR           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LUSHR ) ); }
	public OperandlessInstruction       /**/ IAND            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.IAND ) ); }
	public OperandlessInstruction       /**/ LAND            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LAND ) ); }
	public OperandlessInstruction       /**/ IOR             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.IOR ) ); }
	public OperandlessInstruction       /**/ LOR             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LOR ) ); }
	public OperandlessInstruction       /**/ IXOR            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.IXOR ) ); }
	public OperandlessInstruction       /**/ LXOR            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LXOR ) ); }
	public OperandlessInstruction       /**/ I2L             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2L ) ); }
	public OperandlessInstruction       /**/ I2F             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2F ) ); }
	public OperandlessInstruction       /**/ I2D             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2D ) ); }
	public OperandlessInstruction       /**/ L2I             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.L2I ) ); }
	public OperandlessInstruction       /**/ L2F             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.L2F ) ); }
	public OperandlessInstruction       /**/ L2D             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.L2D ) ); }
	public OperandlessInstruction       /**/ F2I             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.F2I ) ); }
	public OperandlessInstruction       /**/ F2L             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.F2L ) ); }
	public OperandlessInstruction       /**/ F2D             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.F2D ) ); }
	public OperandlessInstruction       /**/ D2I             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.D2I ) ); }
	public OperandlessInstruction       /**/ D2L             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.D2L ) ); }
	public OperandlessInstruction       /**/ D2F             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.D2F ) ); }
	public OperandlessInstruction       /**/ I2B             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2B ) ); }
	public OperandlessInstruction       /**/ I2C             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2C ) ); }
	public OperandlessInstruction       /**/ I2S             /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.I2S ) ); }
	public OperandlessInstruction       /**/ LCMP            /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.LCMP ) ); }
	public OperandlessInstruction       /**/ FCMPL           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.FCMPL ) ); }
	public OperandlessInstruction       /**/ FCMPG           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.FCMPG ) ); }
	public OperandlessInstruction       /**/ DCMPL           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DCMPL ) ); }
	public OperandlessInstruction       /**/ DCMPG           /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.DCMPG ) ); }
	public TypedOperandlessInstruction  /**/ IRETURN         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, Type.Int     /**/ ) ); } //OpCode.IRETURN ) ); }
	public TypedOperandlessInstruction  /**/ LRETURN         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, Type.Long    /**/ ) ); } //OpCode.LRETURN ) ); }
	public TypedOperandlessInstruction  /**/ FRETURN         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, Type.Float   /**/ ) ); } //OpCode.FRETURN ) ); }
	public TypedOperandlessInstruction  /**/ DRETURN         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, Type.Double  /**/ ) ); } //OpCode.DRETURN ) ); }
	public TypedOperandlessInstruction  /**/ ARETURN         /**/()                                                        /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, Type.Address /**/ ) ); } //OpCode.ARETURN ) ); }
	public TypedOperandlessInstruction  /**/ xRETURN         /**/( Type type )                                             /**/ { return add( TypedOperandlessInstruction.of( TypedOperandlessInstruction.Operation.Return, type         /**/ ) ); }
	public OperandlessInstruction       /**/ RETURN          /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.RETURN ) ); }
	public OperandlessInstruction       /**/ ARRAYLENGTH     /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.ARRAYLENGTH ) ); }
	public OperandlessInstruction       /**/ ATHROW          /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.ATHROW ) ); }
	public OperandlessInstruction       /**/ MONITORENTER    /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.MONITORENTER ) ); }
	public OperandlessInstruction       /**/ MONITOREXIT     /**/()                                                        /**/ { return add( OperandlessInstruction.of( OpCode.MONITOREXIT ) ); }

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
