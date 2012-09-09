package bfx.tools.sequence.filter

import org.junit.Test
import org.junit.Assert._

class TestExprParser {
	val parser = new ExprParser();
	
	val length = Variable('length)
	val qual = Variable('quality)
	val number10 = Number(10)
	val opge = BinaryOp('>=,length,number10)
	val fun = Function('mean,qual)
	val oplt = BinaryOp('<,fun,number10)
	val and = BinaryOp('and,oplt,opge)
	val andor = BinaryOp('and,oplt,BinaryOp('or,opge,oplt))
	
	@Test
	def testTerm() {
		val r = parser.parse("length >= 10")
		assertEquals(Some(opge),r);
	}
	
	@Test
	def testFun() {
		val r = parser.parse("mean(quality) < 10")
		assertEquals(Some(oplt),r);
	}

	@Test
	def testAnd() {
		val r = parser.parse("mean(quality) < 10 and (length >= 10)")
		assertEquals(Some(and),r);
	}

	@Test
	def testAndParen() {
		val r = parser.parse("mean(quality) < 10 and length >= 10")
		assertEquals(Some(and),r);
	}
	
	@Test
	def testAndOr() {
		val r = parser.parse("mean(quality) < 10 and length >= 10 or mean(quality) < 10")
		assertEquals(Some(andor),r);
	}

	@Test
	def testAndOrPar() {
		val r = parser.parse("mean(quality) < 10 and (length >= 10 or mean(quality) < 10)")
		assertEquals(Some(andor),r);
	}
	
}