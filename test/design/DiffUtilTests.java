package design;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;

import org.junit.Test;

import difflib.Delta;
import difflib.DiffUtils;

public class DiffUtilTests extends TestSuite{
	
	@Test
	public void basicDiffTrial()
	{
		
		List<String> s1 = new ArrayList<String>();
		List<String> s2 = new ArrayList<String>();
		s1.add("abc");
		s2.add("");
		s2.add("bcd");
		List<Delta> diffs = DiffUtils.diff(s1, s2).getDeltas();
		for(Delta d : diffs)
		{
			System.out.println(d);
		}
	}

}
