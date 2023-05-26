package com.jim.demo.hystrix;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;


/**
 * @author chenjingjun
 * @date 2023/5/25 17:08:38
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UnitTest {

	/**
	 * We give it the name of the function(s) we want it to call to get it’s test data.
	 * The function has to be static and must return either a Collection, an Iterator, a Stream or an Array
	 * @return
	 */
	static int[][] data() {
		return new int[][] { { 1 , 2, 2 }, { 5, 3, 15 }, { 121, 4, 484 } };
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(UnitTest.class);

	private Observable<String> observable;

	@BeforeEach
	void setUp() {
		observable = new CommandHelloWorld("World").observe();
	}

	@Order(2)
	@RepeatedTest(5)
	@DisplayName("one test should work")
	void test() throws InterruptedException {
		Subscription subscribe = observable.subscribe(new Observer<String>() {
			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(String s) {
				LOGGER.debug("{}", s);
			}
		});
	}

	@Order(1)
	@ParameterizedTest
	@MethodSource(value =  "data")
	void testWithStringParameter(int[] data) {
		MyClass tester = new MyClass();
		int m1 = data[0];
		int m2 = data[1];
		int expected = data[2];
		Assertions.assertEquals(expected, tester.multiply(m1, m2));
	}

	@Order(3)
	@TestFactory
	Stream<DynamicTest> dynamicTestStream(){
		MyClass aClass = new MyClass();
		return Arrays.stream(data()).map(entry -> {
			int m1 = entry[0];
			int m2 = entry[1];
			int expected = entry[2];
			return DynamicTest.dynamicTest(m1 + " * " + m2 + " = " + expected, () -> Assertions.assertEquals(expected, aClass.multiply(m1, m2)));
		});
	}

	@Test
	@DisplayName("Ensure that two temporary directories with same files names and content have same hash")
	void hashTwoDynamicDirectoryWhichHaveSameContent(@TempDir Path tempDir, @TempDir Path tempDir2) throws IOException {

		Path file1 = tempDir.resolve("file.txt");

		List<String> input = Arrays.asList("input1", "input2", "input3");
		Files.write(file1, input);

		Assertions.assertTrue(Files.exists(file1), "File should exist");

		Path file2 = tempDir2.resolve("file.txt");

		Files.write(file2, input);
		Assertions.assertTrue(Files.exists(file2), "File should exist");
	}

	@Test
	void test001(){
		//You can mock concrete classes, not just interfaces
		LinkedList mockedList = mock(LinkedList.class);

		//stubbing
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		//following prints "first"
		System.out.println(mockedList.get(0));

		//following throws runtime exception
		System.out.println(mockedList.get(1));

		//following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));

		//Although it is possible to verify a stubbed invocation, usually it's just redundant
		//If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
		//If your code doesn't care what get(0) returns, then it should not be stubbed.
		verify(mockedList, times(1)).get(0);
	}

	// class to be tested
	class MyClass {
		int multiply(int i, int j) {
			return i * j;
		}
	}
}
