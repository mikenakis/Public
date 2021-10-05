package mikenakis.bytecode.reading;

import mikenakis.bytecode.kit.Buffer;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethod;
import mikenakis.kit.functional.Procedure1;

public interface ConstantReader
{
	int readInt();
	int readUnsignedByte();
	int readUnsignedShort();
	float readFloat();
	long readLong();
	double readDouble();
	Buffer readBuffer( int count );
	void readIndexAndSetConstant( Procedure1<Constant> setter );
	void readIndexAndSetBootstrap( Procedure1<BootstrapMethod> setter );
}
