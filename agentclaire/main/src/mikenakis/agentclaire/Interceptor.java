package mikenakis.agentclaire;

import mikenakis.bytecode.ByteCodeType;

/**
 * {@link AgentClaire} Interceptor.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Interceptor
{
	boolean intercept( ByteCodeType byteCodeType, ClassLoader classLoader );
}
