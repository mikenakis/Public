package mikenakis.bytecode.attributes.code;

import mikenakis.bytecode.attributes.code.instructions.BranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConditionalBranchInstruction;
import mikenakis.bytecode.attributes.code.instructions.ConstantReferencingInstruction;
import mikenakis.bytecode.attributes.code.instructions.IIncInstruction;
import mikenakis.bytecode.attributes.code.instructions.ImmediateLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.IndirectLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeDynamicInstruction;
import mikenakis.bytecode.attributes.code.instructions.InvokeInterfaceInstruction;
import mikenakis.bytecode.attributes.code.instructions.LocalVariableInstruction;
import mikenakis.bytecode.attributes.code.instructions.LookupSwitchInstruction;
import mikenakis.bytecode.attributes.code.instructions.MultiANewArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.NewPrimitiveArrayInstruction;
import mikenakis.bytecode.attributes.code.instructions.OperandlessInstruction;
import mikenakis.bytecode.attributes.code.instructions.OperandlessLoadConstantInstruction;
import mikenakis.bytecode.attributes.code.instructions.TableSwitchInstruction;
import mikenakis.kit.Kit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Instruction Models.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@SuppressWarnings( "SpellCheckingInspection" )
public final class InstructionModels
{
	//@formatter:off
	public static final OperandlessInstruction.Model             NOP             = new OperandlessInstruction.Model             ( OpCode.NOP           );
	public static final OperandlessInstruction.Model             ACONST_NULL     = new OperandlessInstruction.Model             ( OpCode.ACONST_NULL   );
	public static final OperandlessLoadConstantInstruction.Model ICONST_M1       = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_M1     );
	public static final OperandlessLoadConstantInstruction.Model ICONST_0        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_0      );
	public static final OperandlessLoadConstantInstruction.Model ICONST_1        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_1      );
	public static final OperandlessLoadConstantInstruction.Model ICONST_2        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_2      );
	public static final OperandlessLoadConstantInstruction.Model ICONST_3        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_3      );
	public static final OperandlessLoadConstantInstruction.Model ICONST_4        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_4      );
	public static final OperandlessLoadConstantInstruction.Model ICONST_5        = new OperandlessLoadConstantInstruction.Model ( OpCode.ICONST_5      );
	public static final OperandlessLoadConstantInstruction.Model LCONST_0        = new OperandlessLoadConstantInstruction.Model ( OpCode.LCONST_0      );
	public static final OperandlessLoadConstantInstruction.Model LCONST_1        = new OperandlessLoadConstantInstruction.Model ( OpCode.LCONST_1      );
	public static final OperandlessLoadConstantInstruction.Model FCONST_0        = new OperandlessLoadConstantInstruction.Model ( OpCode.FCONST_0      );
	public static final OperandlessLoadConstantInstruction.Model FCONST_1        = new OperandlessLoadConstantInstruction.Model ( OpCode.FCONST_1      );
	public static final OperandlessLoadConstantInstruction.Model FCONST_2        = new OperandlessLoadConstantInstruction.Model ( OpCode.FCONST_2      );
	public static final OperandlessLoadConstantInstruction.Model DCONST_0        = new OperandlessLoadConstantInstruction.Model ( OpCode.DCONST_0      );
	public static final OperandlessLoadConstantInstruction.Model DCONST_1        = new OperandlessLoadConstantInstruction.Model ( OpCode.DCONST_1      );
	public static final ImmediateLoadConstantInstruction.Model   BIPUSH_MODEL    = new ImmediateLoadConstantInstruction.Model   ( OpCode.BIPUSH        );
	public static final ImmediateLoadConstantInstruction.Model   SIPUSH_MODEL    = new ImmediateLoadConstantInstruction.Model   ( OpCode.SIPUSH        );
	public static final IndirectLoadConstantInstruction.Model    LDC_MODEL       = new IndirectLoadConstantInstruction.Model    ( OpCode.LDC           );
	public static final IndirectLoadConstantInstruction.Model    LDC_W_MODEL     = new IndirectLoadConstantInstruction.Model    ( OpCode.LDC_W         );
	public static final IndirectLoadConstantInstruction.Model    LDC2_W_MODEL    = new IndirectLoadConstantInstruction.Model    ( OpCode.LDC2_W        );
	public static final LocalVariableInstruction.Model           ILOAD           = new LocalVariableInstruction.Model           ( OpCode.ILOAD         );
	public static final LocalVariableInstruction.Model           LLOAD           = new LocalVariableInstruction.Model           ( OpCode.LLOAD         );
	public static final LocalVariableInstruction.Model           FLOAD           = new LocalVariableInstruction.Model           ( OpCode.FLOAD         );
	public static final LocalVariableInstruction.Model           DLOAD           = new LocalVariableInstruction.Model           ( OpCode.DLOAD         );
	public static final LocalVariableInstruction.Model           ALOAD           = new LocalVariableInstruction.Model           ( OpCode.ALOAD         );
	public static final LocalVariableInstruction.Model           ILOAD_0         = new LocalVariableInstruction.Model           ( OpCode.ILOAD_0       );
	public static final LocalVariableInstruction.Model           ILOAD_1         = new LocalVariableInstruction.Model           ( OpCode.ILOAD_1       );
	public static final LocalVariableInstruction.Model           ILOAD_2         = new LocalVariableInstruction.Model           ( OpCode.ILOAD_2       );
	public static final LocalVariableInstruction.Model           ILOAD_3         = new LocalVariableInstruction.Model           ( OpCode.ILOAD_3       );
	public static final LocalVariableInstruction.Model           LLOAD_0         = new LocalVariableInstruction.Model           ( OpCode.LLOAD_0       );
	public static final LocalVariableInstruction.Model           LLOAD_1         = new LocalVariableInstruction.Model           ( OpCode.LLOAD_1       );
	public static final LocalVariableInstruction.Model           LLOAD_2         = new LocalVariableInstruction.Model           ( OpCode.LLOAD_2       );
	public static final LocalVariableInstruction.Model           LLOAD_3         = new LocalVariableInstruction.Model           ( OpCode.LLOAD_3       );
	public static final LocalVariableInstruction.Model           FLOAD_0         = new LocalVariableInstruction.Model           ( OpCode.FLOAD_0       );
	public static final LocalVariableInstruction.Model           FLOAD_1         = new LocalVariableInstruction.Model           ( OpCode.FLOAD_1       );
	public static final LocalVariableInstruction.Model           FLOAD_2         = new LocalVariableInstruction.Model           ( OpCode.FLOAD_2       );
	public static final LocalVariableInstruction.Model           FLOAD_3         = new LocalVariableInstruction.Model           ( OpCode.FLOAD_3       );
	public static final LocalVariableInstruction.Model           DLOAD_0         = new LocalVariableInstruction.Model           ( OpCode.DLOAD_0       );
	public static final LocalVariableInstruction.Model           DLOAD_1         = new LocalVariableInstruction.Model           ( OpCode.DLOAD_1       );
	public static final LocalVariableInstruction.Model           DLOAD_2         = new LocalVariableInstruction.Model           ( OpCode.DLOAD_2       );
	public static final LocalVariableInstruction.Model           DLOAD_3         = new LocalVariableInstruction.Model           ( OpCode.DLOAD_3       );
	public static final LocalVariableInstruction.Model           ALOAD_0         = new LocalVariableInstruction.Model           ( OpCode.ALOAD_0       );
	public static final LocalVariableInstruction.Model           ALOAD_1         = new LocalVariableInstruction.Model           ( OpCode.ALOAD_1       );
	public static final LocalVariableInstruction.Model           ALOAD_2         = new LocalVariableInstruction.Model           ( OpCode.ALOAD_2       );
	public static final LocalVariableInstruction.Model           ALOAD_3         = new LocalVariableInstruction.Model           ( OpCode.ALOAD_3       );
	public static final OperandlessInstruction.Model             IALOAD          = new OperandlessInstruction.Model             ( OpCode.IALOAD        );
	public static final OperandlessInstruction.Model             LALOAD          = new OperandlessInstruction.Model             ( OpCode.LALOAD        );
	public static final OperandlessInstruction.Model             FALOAD          = new OperandlessInstruction.Model             ( OpCode.FALOAD        );
	public static final OperandlessInstruction.Model             DALOAD          = new OperandlessInstruction.Model             ( OpCode.DALOAD        );
	public static final OperandlessInstruction.Model             AALOAD          = new OperandlessInstruction.Model             ( OpCode.AALOAD        );
	public static final OperandlessInstruction.Model             BALOAD          = new OperandlessInstruction.Model             ( OpCode.BALOAD        );
	public static final OperandlessInstruction.Model             CALOAD          = new OperandlessInstruction.Model             ( OpCode.CALOAD        );
	public static final OperandlessInstruction.Model             SALOAD          = new OperandlessInstruction.Model             ( OpCode.SALOAD        );
	public static final LocalVariableInstruction.Model           ISTORE          = new LocalVariableInstruction.Model           ( OpCode.ISTORE        );
	public static final LocalVariableInstruction.Model           LSTORE          = new LocalVariableInstruction.Model           ( OpCode.LSTORE        );
	public static final LocalVariableInstruction.Model           FSTORE          = new LocalVariableInstruction.Model           ( OpCode.FSTORE        );
	public static final LocalVariableInstruction.Model           DSTORE          = new LocalVariableInstruction.Model           ( OpCode.DSTORE        );
	public static final LocalVariableInstruction.Model           ASTORE          = new LocalVariableInstruction.Model           ( OpCode.ASTORE        );
	public static final LocalVariableInstruction.Model           ISTORE_0	     = new LocalVariableInstruction.Model           ( OpCode.ISTORE_0      );
	public static final LocalVariableInstruction.Model           ISTORE_1	     = new LocalVariableInstruction.Model           ( OpCode.ISTORE_1      );
	public static final LocalVariableInstruction.Model           ISTORE_2	     = new LocalVariableInstruction.Model           ( OpCode.ISTORE_2      );
	public static final LocalVariableInstruction.Model           ISTORE_3	     = new LocalVariableInstruction.Model           ( OpCode.ISTORE_3      );
	public static final LocalVariableInstruction.Model           LSTORE_0	     = new LocalVariableInstruction.Model           ( OpCode.LSTORE_0      );
	public static final LocalVariableInstruction.Model           LSTORE_1	     = new LocalVariableInstruction.Model           ( OpCode.LSTORE_1      );
	public static final LocalVariableInstruction.Model           LSTORE_2	     = new LocalVariableInstruction.Model           ( OpCode.LSTORE_2      );
	public static final LocalVariableInstruction.Model           LSTORE_3	     = new LocalVariableInstruction.Model           ( OpCode.LSTORE_3      );
	public static final LocalVariableInstruction.Model           FSTORE_0	     = new LocalVariableInstruction.Model           ( OpCode.FSTORE_0      );
	public static final LocalVariableInstruction.Model           FSTORE_1	     = new LocalVariableInstruction.Model           ( OpCode.FSTORE_1      );
	public static final LocalVariableInstruction.Model           FSTORE_2	     = new LocalVariableInstruction.Model           ( OpCode.FSTORE_2      );
	public static final LocalVariableInstruction.Model           FSTORE_3	     = new LocalVariableInstruction.Model           ( OpCode.FSTORE_3      );
	public static final LocalVariableInstruction.Model           DSTORE_0	     = new LocalVariableInstruction.Model           ( OpCode.DSTORE_0      );
	public static final LocalVariableInstruction.Model           DSTORE_1	     = new LocalVariableInstruction.Model           ( OpCode.DSTORE_1      );
	public static final LocalVariableInstruction.Model           DSTORE_2	     = new LocalVariableInstruction.Model           ( OpCode.DSTORE_2      );
	public static final LocalVariableInstruction.Model           DSTORE_3	     = new LocalVariableInstruction.Model           ( OpCode.DSTORE_3      );
	public static final LocalVariableInstruction.Model           ASTORE_0	     = new LocalVariableInstruction.Model           ( OpCode.ASTORE_0      );
	public static final LocalVariableInstruction.Model           ASTORE_1	     = new LocalVariableInstruction.Model           ( OpCode.ASTORE_1      );
	public static final LocalVariableInstruction.Model           ASTORE_2	     = new LocalVariableInstruction.Model           ( OpCode.ASTORE_2      );
	public static final LocalVariableInstruction.Model           ASTORE_3	     = new LocalVariableInstruction.Model           ( OpCode.ASTORE_3      );
	public static final OperandlessInstruction.Model             IASTORE         = new OperandlessInstruction.Model             ( OpCode.IASTORE       );
	public static final OperandlessInstruction.Model             LASTORE         = new OperandlessInstruction.Model             ( OpCode.LASTORE       );
	public static final OperandlessInstruction.Model             FASTORE         = new OperandlessInstruction.Model             ( OpCode.FASTORE       );
	public static final OperandlessInstruction.Model             DASTORE         = new OperandlessInstruction.Model             ( OpCode.DASTORE       );
	public static final OperandlessInstruction.Model             AASTORE         = new OperandlessInstruction.Model             ( OpCode.AASTORE       );
	public static final OperandlessInstruction.Model             BASTORE         = new OperandlessInstruction.Model             ( OpCode.BASTORE       );
	public static final OperandlessInstruction.Model             CASTORE         = new OperandlessInstruction.Model             ( OpCode.CASTORE       );
	public static final OperandlessInstruction.Model             SASTORE         = new OperandlessInstruction.Model             ( OpCode.SASTORE       );
	public static final OperandlessInstruction.Model             POP             = new OperandlessInstruction.Model             ( OpCode.POP           );
	public static final OperandlessInstruction.Model             POP2            = new OperandlessInstruction.Model             ( OpCode.POP2          );
	public static final OperandlessInstruction.Model             DUP             = new OperandlessInstruction.Model             ( OpCode.DUP           );
	public static final OperandlessInstruction.Model             DUP_X1		     = new OperandlessInstruction.Model             ( OpCode.DUP_X1        );
	public static final OperandlessInstruction.Model             DUP_X2		     = new OperandlessInstruction.Model             ( OpCode.DUP_X2        );
	public static final OperandlessInstruction.Model             DUP2            = new OperandlessInstruction.Model             ( OpCode.DUP2          );
	public static final OperandlessInstruction.Model             DUP2_X1	     = new OperandlessInstruction.Model             ( OpCode.DUP2_X1       );
	public static final OperandlessInstruction.Model             DUP2_X2	     = new OperandlessInstruction.Model             ( OpCode.DUP2_X2       );
	public static final OperandlessInstruction.Model             SWAP            = new OperandlessInstruction.Model             ( OpCode.SWAP          );
	public static final OperandlessInstruction.Model             IADD            = new OperandlessInstruction.Model             ( OpCode.IADD          );
	public static final OperandlessInstruction.Model             LADD            = new OperandlessInstruction.Model             ( OpCode.LADD          );
	public static final OperandlessInstruction.Model             FADD            = new OperandlessInstruction.Model             ( OpCode.FADD          );
	public static final OperandlessInstruction.Model             DADD            = new OperandlessInstruction.Model             ( OpCode.DADD          );
	public static final OperandlessInstruction.Model             ISUB            = new OperandlessInstruction.Model             ( OpCode.ISUB          );
	public static final OperandlessInstruction.Model             LSUB            = new OperandlessInstruction.Model             ( OpCode.LSUB          );
	public static final OperandlessInstruction.Model             FSUB            = new OperandlessInstruction.Model             ( OpCode.FSUB          );
	public static final OperandlessInstruction.Model             DSUB            = new OperandlessInstruction.Model             ( OpCode.DSUB          );
	public static final OperandlessInstruction.Model             IMUL            = new OperandlessInstruction.Model             ( OpCode.IMUL          );
	public static final OperandlessInstruction.Model             LMUL            = new OperandlessInstruction.Model             ( OpCode.LMUL          );
	public static final OperandlessInstruction.Model             FMUL            = new OperandlessInstruction.Model             ( OpCode.FMUL          );
	public static final OperandlessInstruction.Model             DMUL            = new OperandlessInstruction.Model             ( OpCode.DMUL          );
	public static final OperandlessInstruction.Model             IDIV            = new OperandlessInstruction.Model             ( OpCode.IDIV          );
	public static final OperandlessInstruction.Model             LDIV            = new OperandlessInstruction.Model             ( OpCode.LDIV          );
	public static final OperandlessInstruction.Model             FDIV            = new OperandlessInstruction.Model             ( OpCode.FDIV          );
	public static final OperandlessInstruction.Model             DDIV            = new OperandlessInstruction.Model             ( OpCode.DDIV          );
	public static final OperandlessInstruction.Model             IREM            = new OperandlessInstruction.Model             ( OpCode.IREM          );
	public static final OperandlessInstruction.Model             LREM            = new OperandlessInstruction.Model             ( OpCode.LREM          );
	public static final OperandlessInstruction.Model             FREM            = new OperandlessInstruction.Model             ( OpCode.FREM          );
	public static final OperandlessInstruction.Model             DREM            = new OperandlessInstruction.Model             ( OpCode.DREM          );
	public static final OperandlessInstruction.Model             INEG            = new OperandlessInstruction.Model             ( OpCode.INEG          );
	public static final OperandlessInstruction.Model             LNEG            = new OperandlessInstruction.Model             ( OpCode.LNEG          );
	public static final OperandlessInstruction.Model             FNEG            = new OperandlessInstruction.Model             ( OpCode.FNEG          );
	public static final OperandlessInstruction.Model             DNEG            = new OperandlessInstruction.Model             ( OpCode.DNEG          );
	public static final OperandlessInstruction.Model             ISHL            = new OperandlessInstruction.Model             ( OpCode.ISHL          );
	public static final OperandlessInstruction.Model             LSHL            = new OperandlessInstruction.Model             ( OpCode.LSHL          );
	public static final OperandlessInstruction.Model             ISHR            = new OperandlessInstruction.Model             ( OpCode.ISHR          );
	public static final OperandlessInstruction.Model             LSHR            = new OperandlessInstruction.Model             ( OpCode.LSHR          );
	public static final OperandlessInstruction.Model             IUSHR           = new OperandlessInstruction.Model             ( OpCode.IUSHR         );
	public static final OperandlessInstruction.Model             LUSHR           = new OperandlessInstruction.Model             ( OpCode.LUSHR         );
	public static final OperandlessInstruction.Model             IAND            = new OperandlessInstruction.Model             ( OpCode.IAND          );
	public static final OperandlessInstruction.Model             LAND            = new OperandlessInstruction.Model             ( OpCode.LAND          );
	public static final OperandlessInstruction.Model             IOR             = new OperandlessInstruction.Model             ( OpCode.IOR           );
	public static final OperandlessInstruction.Model             LOR             = new OperandlessInstruction.Model             ( OpCode.LOR           );
	public static final OperandlessInstruction.Model             IXOR            = new OperandlessInstruction.Model             ( OpCode.IXOR          );
	public static final OperandlessInstruction.Model             LXOR            = new OperandlessInstruction.Model             ( OpCode.LXOR          );
	public static final IIncInstruction.Model                    IINC            = new IIncInstruction.Model();
	public static final OperandlessInstruction.Model             I2L             = new OperandlessInstruction.Model             ( OpCode.I2L           );
	public static final OperandlessInstruction.Model             I2F             = new OperandlessInstruction.Model             ( OpCode.I2F           );
	public static final OperandlessInstruction.Model             I2D             = new OperandlessInstruction.Model             ( OpCode.I2D           );
	public static final OperandlessInstruction.Model             L2I             = new OperandlessInstruction.Model             ( OpCode.L2I           );
	public static final OperandlessInstruction.Model             L2F             = new OperandlessInstruction.Model             ( OpCode.L2F           );
	public static final OperandlessInstruction.Model             L2D             = new OperandlessInstruction.Model             ( OpCode.L2D           );
	public static final OperandlessInstruction.Model             F2I             = new OperandlessInstruction.Model             ( OpCode.F2I           );
	public static final OperandlessInstruction.Model             F2L             = new OperandlessInstruction.Model             ( OpCode.F2L           );
	public static final OperandlessInstruction.Model             F2D             = new OperandlessInstruction.Model             ( OpCode.F2D           );
	public static final OperandlessInstruction.Model             D2I             = new OperandlessInstruction.Model             ( OpCode.D2I           );
	public static final OperandlessInstruction.Model             D2L             = new OperandlessInstruction.Model             ( OpCode.D2L           );
	public static final OperandlessInstruction.Model             D2F             = new OperandlessInstruction.Model             ( OpCode.D2F           );
	public static final OperandlessInstruction.Model             I2B             = new OperandlessInstruction.Model             ( OpCode.I2B           );
	public static final OperandlessInstruction.Model             I2C             = new OperandlessInstruction.Model             ( OpCode.I2C           );
	public static final OperandlessInstruction.Model             I2S             = new OperandlessInstruction.Model             ( OpCode.I2S           );
	public static final OperandlessInstruction.Model             LCMP            = new OperandlessInstruction.Model             ( OpCode.LCMP          );
	public static final OperandlessInstruction.Model             FCMPL           = new OperandlessInstruction.Model             ( OpCode.FCMPL         );
	public static final OperandlessInstruction.Model             FCMPG           = new OperandlessInstruction.Model             ( OpCode.FCMPG         );
	public static final OperandlessInstruction.Model             DCMPL           = new OperandlessInstruction.Model             ( OpCode.DCMPL         );
	public static final OperandlessInstruction.Model             DCMPG           = new OperandlessInstruction.Model             ( OpCode.DCMPG         );
	public static final ConditionalBranchInstruction.Model       IFEQ            = new ConditionalBranchInstruction.Model       ( OpCode.IFEQ          );
	public static final ConditionalBranchInstruction.Model       IFNE            = new ConditionalBranchInstruction.Model       ( OpCode.IFNE          );
	public static final ConditionalBranchInstruction.Model       IFLT            = new ConditionalBranchInstruction.Model       ( OpCode.IFLT          );
	public static final ConditionalBranchInstruction.Model       IFGE            = new ConditionalBranchInstruction.Model       ( OpCode.IFGE          );
	public static final ConditionalBranchInstruction.Model       IFGT            = new ConditionalBranchInstruction.Model       ( OpCode.IFGT          );
	public static final ConditionalBranchInstruction.Model       IFLE            = new ConditionalBranchInstruction.Model       ( OpCode.IFLE          );
	public static final ConditionalBranchInstruction.Model       IF_ICMPEQ       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPEQ     );
	public static final ConditionalBranchInstruction.Model       IF_ICMPNE       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPNE     );
	public static final ConditionalBranchInstruction.Model       IF_ICMPLT       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPLT     );
	public static final ConditionalBranchInstruction.Model       IF_ICMPGE       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPGE     );
	public static final ConditionalBranchInstruction.Model       IF_ICMPGT       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPGT     );
	public static final ConditionalBranchInstruction.Model       IF_ICMPLE       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ICMPLE     );
	public static final ConditionalBranchInstruction.Model       IF_ACMPEQ       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ACMPEQ     );
	public static final ConditionalBranchInstruction.Model       IF_ACMPNE       = new ConditionalBranchInstruction.Model       ( OpCode.IF_ACMPNE     );
	public static final BranchInstruction.Model                  GOTO            = new BranchInstruction.Model                  ( OpCode.GOTO          );
	public static final BranchInstruction.Model                  JSR             = new BranchInstruction.Model                  ( OpCode.JSR           );
	public static final LocalVariableInstruction.Model           RET             = new LocalVariableInstruction.Model           ( OpCode.RET           );
	public static final TableSwitchInstruction.Model             TABLESWITCH     = new TableSwitchInstruction.Model             ();
	public static final LookupSwitchInstruction.Model            LOOKUPSWITCH    = new LookupSwitchInstruction.Model            ();
	public static final OperandlessInstruction.Model             IRETURN         = new OperandlessInstruction.Model             ( OpCode.IRETURN       );
	public static final OperandlessInstruction.Model             LRETURN         = new OperandlessInstruction.Model             ( OpCode.LRETURN       );
	public static final OperandlessInstruction.Model             FRETURN         = new OperandlessInstruction.Model             ( OpCode.FRETURN       );
	public static final OperandlessInstruction.Model             DRETURN         = new OperandlessInstruction.Model             ( OpCode.DRETURN       );
	public static final OperandlessInstruction.Model             ARETURN         = new OperandlessInstruction.Model             ( OpCode.ARETURN       );
	public static final OperandlessInstruction.Model             RETURN          = new OperandlessInstruction.Model             ( OpCode.RETURN        );
	public static final ConstantReferencingInstruction.Model     GETSTATIC       = new ConstantReferencingInstruction.Model     ( OpCode.GETSTATIC     );
	public static final ConstantReferencingInstruction.Model     PUTSTATIC       = new ConstantReferencingInstruction.Model     ( OpCode.PUTSTATIC     );
	public static final ConstantReferencingInstruction.Model     GETFIELD        = new ConstantReferencingInstruction.Model     ( OpCode.GETFIELD      );
	public static final ConstantReferencingInstruction.Model     PUTFIELD        = new ConstantReferencingInstruction.Model     ( OpCode.PUTFIELD      );
	public static final ConstantReferencingInstruction.Model     INVOKEVIRTUAL   = new ConstantReferencingInstruction.Model     ( OpCode.INVOKEVIRTUAL );
	public static final ConstantReferencingInstruction.Model     INVOKESPECIAL   = new ConstantReferencingInstruction.Model     ( OpCode.INVOKESPECIAL );
	public static final ConstantReferencingInstruction.Model     INVOKESTATIC    = new ConstantReferencingInstruction.Model     ( OpCode.INVOKESTATIC  );
	public static final InvokeInterfaceInstruction.Model         INVOKEINTERFACE = new InvokeInterfaceInstruction.Model         ();
	public static final InvokeDynamicInstruction.Model           INVOKEDYNAMIC   = new InvokeDynamicInstruction.Model           ();
	public static final ConstantReferencingInstruction.Model     NEW             = new ConstantReferencingInstruction.Model     ( OpCode.NEW           );
	public static final NewPrimitiveArrayInstruction.Model       NEWARRAY        = new NewPrimitiveArrayInstruction.Model       ();
	public static final ConstantReferencingInstruction.Model     ANEWARRAY       = new ConstantReferencingInstruction.Model     ( OpCode.ANEWARRAY     );
	public static final OperandlessInstruction.Model             ARRAYLENGTH     = new OperandlessInstruction.Model             ( OpCode.ARRAYLENGTH   );
	public static final OperandlessInstruction.Model             ATHROW          = new OperandlessInstruction.Model             ( OpCode.ATHROW        );
	public static final ConstantReferencingInstruction.Model     CHECKCAST       = new ConstantReferencingInstruction.Model     ( OpCode.CHECKCAST     );
	public static final ConstantReferencingInstruction.Model     INSTANCEOF      = new ConstantReferencingInstruction.Model     ( OpCode.INSTANCEOF    );
	public static final OperandlessInstruction.Model             MONITORENTER    = new OperandlessInstruction.Model             ( OpCode.MONITORENTER  );
	public static final OperandlessInstruction.Model             MONITOREXIT     = new OperandlessInstruction.Model             ( OpCode.MONITOREXIT   );
	public static final MultiANewArrayInstruction.Model          MULTIANEWARRAY  = new MultiANewArrayInstruction.Model          ();
	public static final ConditionalBranchInstruction.Model       IFNULL          = new ConditionalBranchInstruction.Model       ( OpCode.IFNULL        );
	public static final ConditionalBranchInstruction.Model       IFNONNULL       = new ConditionalBranchInstruction.Model       ( OpCode.IFNONNULL     );
	public static final BranchInstruction.Model                  GOTO_W          = new BranchInstruction.Model                  ( OpCode.GOTO_W        );
	public static final BranchInstruction.Model                  JSR_W           = new BranchInstruction.Model                  ( OpCode.JSR_W         );
	//@formatter:on

	private static final InstructionModel[] instructionModelTable = buildInstructionModelTable();

	private static InstructionModel[] buildInstructionModelTable()
	{
		InstructionModel[] instructionModelTable = new InstructionModel[256];
		for( Field field : InstructionModels.class.getFields() )
		{
			int modifiers = field.getModifiers();
			if( !Modifier.isPublic( modifiers ) )
				continue;
			assert Modifier.isStatic( modifiers );
			assert Modifier.isFinal( modifiers );
			assert InstructionModel.class.isAssignableFrom( field.getType() );
			InstructionModel instructionModel = Kit.upCast( Kit.unchecked( () -> field.get( null ) ) );
			assert instructionModelTable[instructionModel.opCode] == null;
			instructionModelTable[instructionModel.opCode] = instructionModel;
		}
		return instructionModelTable;
	}

	public static InstructionModel getInstructionModel( int opCode )
	{
		return instructionModelTable[opCode];
	}

	private InstructionModels()
	{
	}
}
