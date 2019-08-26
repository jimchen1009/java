package com.ximuyi.demo.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class KryoMain {
	/***
	 * Getting data in and out of Kryo is done using the Input and Output classes. These classes are not thread safe.
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Kryo kryo = new Kryo();
		kryo.register(SerializationClass.class);
		SerializationClass object = new SerializationClass();
		object.value = "Hello Kryo!";

		Output output = new Output(new FileOutputStream("file.bin"));
		kryo.writeObject(output, object);
		output.close();

		Input input = new Input(new FileInputStream("file.bin"));
		SerializationClass object2 = kryo.readObject(input, SerializationClass.class);
		input.close();
	}

	private static class SerializationClass {
		private String value;
	}
}
