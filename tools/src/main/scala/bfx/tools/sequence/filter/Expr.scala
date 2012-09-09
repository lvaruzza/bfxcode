package bfx.tools.sequence.filter

abstract class Expr {
  def walk(f:Expr=>Unit)  
}

case class Variable(name: Symbol) extends Expr {
  override def toString = name.toString
  def walk(f:Expr=>Unit) {f(this)}
}

case class Function(name: Symbol,arg:Variable) extends Expr {
  override def toString = "(%s %s)".format(name,arg)
  override def walk(f:Expr=>Unit) { f(this) }
}
case class Number(value: Float) extends Expr {
  override def toString = value.toString
  override def walk(f:Expr=>Unit) { f(this) }
}

case class StringLiteral(value: String) extends Expr {
  override def toString = value.toString  
  override def walk(f:Expr=>Unit) { f(this) }
}

case class UnaryOp(operator:Symbol,arg:Expr) extends Expr {
  override def toString = "(%s %s)".format(operator,arg)
  override def walk(f:Expr=>Unit) { f(this); f(arg) }
}

case class BinaryOp(operator:Symbol,left:Expr,right:Expr) extends Expr {
  override def toString = "(%s %s %s)".format(operator,left,right)
  override def walk(f:Expr=>Unit) { f(this); f(left); f(right); }  
}


object Expr {
  def verify(e:Expr) {
    //e match {
    //  case BinaryOp()
    //}
  }
  
  //def  compile(e:Expr):FilterExpr = {
  //}
}

