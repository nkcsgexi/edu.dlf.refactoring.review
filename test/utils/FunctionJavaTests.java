package utils;

import org.junit.Test;

import fj.data.Array;
import fj.data.List;
import fj.data.List.Buffer;
import junit.framework.TestSuite;

public class FunctionJavaTests extends TestSuite{
	
	@Test
	public void listTest()
	{
		Buffer<Integer> buffer = new Buffer<Integer>();
		buffer = buffer.snoc(1);
		buffer.snoc(2);
		buffer.snoc(3);
		buffer.snoc(4);
		List<Integer> list = buffer.toList();
		list.snoc(2);
		
		for(int i : buffer)
		{
			System.out.println(i);
		}
		
		for(int i : list)
		{
			System.out.println(i);
		}
 	}
	
	
	@Test
	public void arrayTest()
	{
		Array<Integer> array;
	
	}

}
