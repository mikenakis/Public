package mikenakis.bytecode.test;

import mikenakis.bytecode.printing.ByteCodePrinter;
import mikenakis.bytecode.test.kit.TestKit;
import mikenakis.benchmark.Benchmark;
import mikenakis.benchmark.Benchmarkable;
import mikenakis.bytecode.model.ByteCodeType;
import mikenakis.bytecode.reading.ByteCodeReader;
import mikenakis.kit.Kit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BytecodeBenchmark
{
	public static void main( String[] args )
	{
		BytecodeBenchmark main = new BytecodeBenchmark();
		main.run0();
	}

	private void run0()
	{
		Path modelPath = Helpers.getOutputPath( getClass() ).resolve( "model" );
		List<Path> classFilePathNames = TestKit.collectResourcePaths( modelPath, false, ".class" );
		//		for( var classFilePathName : classFilePathNames )
		//			System.out.printf( Locale.ROOT, "Class: %s\n", classFilePathName );
		List<byte[]> bytesList = classFilePathNames.stream().map( path -> Kit.unchecked( () -> Files.readAllBytes( path ) ) ).toList();
		double readingDurationSeconds = benchmarkReading( bytesList );
		double printingDurationSeconds = benchmarkPrinting( bytesList );
		System.out.printf( Locale.ROOT, "Reading:  %6.2f millis\n", readingDurationSeconds * 1e3 );
		System.out.printf( Locale.ROOT, "Printing: %6.2f millis\n", printingDurationSeconds * 1e3 );
	}

	private double benchmarkReading( List<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeReader.read( bytes );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}

	private double benchmarkPrinting( List<byte[]> bytesList )
	{
		Benchmark benchmark = new Benchmark( 0.001, 50 );
		return benchmark.measure( Benchmarkable.of( () -> //
		{
			int sum = 0;
			for( var bytes : bytesList )
			{
				ByteCodeType byteCodeType = ByteCodeReader.read( bytes );
				ByteCodePrinter.printByteCodeType( byteCodeType, Optional.empty() );
				sum += byteCodeType.hashCode();
			}
			return sum;
		} ) );
	}
}
