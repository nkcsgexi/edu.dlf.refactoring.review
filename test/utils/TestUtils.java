package utils;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FileUtils;
import edu.dlf.refactoring.design.ASTNodePair;


public class TestUtils {

	public static String getTestFileDirectory()
	{
		return  System.getProperty("user.dir") + "/TestFiles/";
	}
	
	public static ASTNode getCUByFileName(String file) throws Exception
	{
		return ASTAnalyzer.parseICompilationUnit(FileUtils.readAllFunc.f(
			getTestFileDirectory() + file));	
	}
	
	public static ASTNodePair getNodePairByFileNames(String file1, String file2) throws Exception
	{
		return new ASTNodePair(getCUByFileName(file1), getCUByFileName(file2));
	}
	
}
