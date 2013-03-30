package bfx.tools.sequence.filter

import scala.util.parsing.combinator.JavaTokenParsers
import grizzled.slf4j.Logger



// a > b and c < d
// a < b or (b < c and d = e)

class ExprParser extends JavaTokenParsers {
    val logger = Logger(classOf[ExprParser].getName())
    
	def makeTree(fst:Expr,lst:List[~[String,Expr]]):Expr = {
	  lst match {
	    case Nil => fst
	    case (op~rh) :: xs => BinaryOp(Symbol(op),fst,makeTree(rh,xs))
	  }
	}
	
	def funVar:Parser[Expr] = ident~"("~ident~")" ^^ { case f~lp~v~rp => Function(Symbol(f),Variable(Symbol(v))) } |
							  ident ^^ (x=>Variable(Symbol(x)))
	def atom:Parser[Expr] = 
			   (floatingPointNumber | decimalNumber) ^^ (x => Number(x.toFloat)) | 
			   stringLiteral ^^ (x=>StringLiteral(x)) | 
			   funVar
			   
	def compare =  (">=" | "<=" | ">" | "<" | "=")  ^^ (x => Symbol(x))  
	def term = atom~compare~atom ^^ {case lh~op~rh => BinaryOp(op,lh,rh)}
	def expr:Parser[Expr] = 
			   term ~ rep( ("and"|"or") ~ expr) ^^ {  
	              case fst~lst => makeTree(fst,lst)	              
	            } |  
			   "(" ~ expr ~ ")" ^^ {case lp~expr~rp => expr }| 
			   "not" ~ term ^^ {case not~term => UnaryOp('not,term) } |
			   term
				

	def parse(text:String):Option[Expr] = {
	  parseAll(this.expr,text) match {
	    case this.Success(x,_) => Some(x)
	    case this.NoSuccess(msg,_) => {
	       logger.debug(msg)
	       println(msg)
	       None 
	    }
	  }	  
	}

}

object ExprParser {
	def main(args: Array[String]) {
	  val p = new ExprParser();
	  println(p.parse("length>=42").get);
	  println(p.parse("length>42").get);
	  println(p.parse("not length>42").get);
	  println(p.parse("length>42 and mean(quality)>20").get);
	  println(p.parse("length>42 and (mean(quality)>20 or min(quality)>5)").get);
	  
	  val r = p.parse("length>42 and mean(quality)>20").get;
	  
	  println("===============")
	  println(IR.compile1(r))
	  println("===============")
	  
	  println("Finish")
	}
}

	         

