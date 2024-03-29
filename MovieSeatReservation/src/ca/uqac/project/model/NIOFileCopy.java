package ca.uqac.project.model;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileCopy {
	public static void main(String[] args) {
		String inFileStr = "src/iot.jpg";
		String outFileStr = "src/toi.jpg";
		long startTime, elapsedTime; // for speed benchmarking
		int bufferSizeKB = 4;
		int bufferSize = bufferSizeKB * 1024;

		// Check file length
		File fileIn = new File(inFileStr);
		System.out.println("File size is " + fileIn.length() + " bytes");
		System.out.println("Buffer size is " + bufferSizeKB + " KB");

		// Using FileChannel with indirect ByteBuffer
		System.out.println("\n --- Using FileChannel with an indirect ByteBuffer of " + bufferSizeKB + " KB");
		try (FileChannel in = new FileInputStream(inFileStr).getChannel();
				FileChannel out = new FileOutputStream(outFileStr).getChannel()) {
			// Allocate an indirect ByteBuffer
			ByteBuffer bytebuf = ByteBuffer.allocate(bufferSize);

			startTime = System.nanoTime();
			int bytesCount;
			while ((bytesCount = in.read(bytebuf)) > 0) { // Read data from file
															// into ByteBuffer
				// flip the buffer which set the limit to current position, and
				// position to 0.
				bytebuf.flip();
				out.write(bytebuf); // Write data from ByteBuffer to file
				bytebuf.clear(); // For the next read
			}
			elapsedTime = System.nanoTime() - startTime;
			System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Using FileChannel with direct ByteBuffer
		System.out.println("\n --- Using FileChannel with a direct ByteBuffer of " + bufferSizeKB + " KB");
		try (FileChannel in = new FileInputStream(inFileStr).getChannel();
				FileChannel out = new FileOutputStream(outFileStr).getChannel()) {
			// Allocate a "direct" ByteBuffer
			ByteBuffer bytebuf = ByteBuffer.allocateDirect(bufferSize);

			startTime = System.nanoTime();
			int bytesCount;
			while ((bytesCount = in.read(bytebuf)) > 0) { // Read data from file
															// into ByteBuffer
				// flip the buffer which set the limit to current position, and
				// position to 0.
				bytebuf.flip();
				out.write(bytebuf); // Write data from ByteBuffer to file
				bytebuf.clear(); // For the next read
			}
			elapsedTime = System.nanoTime() - startTime;
			System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Using FileChannel with transferTo()
		System.out.println("\n --- Using FileChannel with transferTo()");
		try (FileChannel in = new FileInputStream(inFileStr).getChannel();
				FileChannel out = new FileOutputStream(outFileStr).getChannel()) {
			startTime = System.nanoTime();
			in.transferTo(0, in.size(), out);
			elapsedTime = System.nanoTime() - startTime;
			System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Using Buffered Stream I/O
		System.out.println("\n --- Using Buffered Stream");
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inFileStr));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFileStr))) {
			startTime = System.nanoTime();
			int bytesCount;
			while ((bytesCount = in.read()) != -1) {
				out.write(bytesCount);
			}
			elapsedTime = System.nanoTime() - startTime;
			System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		// Using a programmer-managed 4K byte-array for Disk I/O
		System.out.println("\n --- Using a programmer-managed byte-array of " + bufferSizeKB + " KB");
		try (FileInputStream in = new FileInputStream(inFileStr);
				FileOutputStream out = new FileOutputStream(outFileStr)) {
			startTime = System.nanoTime();
			byte[] byteArray = new byte[bufferSize]; // byte-array
			int bytesCount;
			while ((bytesCount = in.read(byteArray)) != -1) {
				out.write(byteArray, 0, bytesCount);
			}
			elapsedTime = System.nanoTime() - startTime;
			System.out.println("Elapsed Time is " + (elapsedTime / 1000000.0) + " msec");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}