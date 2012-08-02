package bfx.tools.sequence.filter;

import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.utils.TextUtils;

public class FilterCompiler {
	private static Logger log = LoggerFactory.getLogger(FilterCompiler.class);

	private JavaSourceCompiler javaSourceCompiler = new JavaSourceCompilerImpl();

	public  String makeSource(String pkgname,String classname,String expr) {
	    String javaSourceCode =  String.format("package %s;" +
	    		"import bfx.tools.sequence.filter.*; \n" +
	    		"public class %s extends FilterExpr{\n" +
	    		"       public boolean filter(int length,double meanQuality)  {\n" +
	    		"            return (%s);\n" +
	    		"        }\n" +
	    		"    }",pkgname,classname,expr);
	    log.debug("Generated code: " + javaSourceCode);
	    return javaSourceCode;
	}
	
	public  FilterExpr compile(String code)  {
	    JavaSourceCompiler.CompilationUnit compilationUnit = javaSourceCompiler.createCompilationUnit();
	    String classname = generateName(code);
	    String pkgname = "dynamic.filter.expressions";
	    String fullname = pkgname + "." + classname;
	    compilationUnit.addJavaSource(classname, makeSource(pkgname,classname,code));
	    ClassLoader classLoader = javaSourceCompiler.compile(compilationUnit);
	    try {
		    Class<?> filterClass = classLoader.loadClass(fullname);
		    FilterExpr expr = (FilterExpr)filterClass.newInstance();
		    return expr;
	    }  catch(Exception e) {
	    	throw new RuntimeException(String.format("Failed to compile the expression: '%s'",code),e);
	    }
	}
	
	private String generateName(String code) {
		return "Expr_" + TextUtils.md5(code);
	}
}
