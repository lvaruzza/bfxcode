package bfx.tools.sequence.filter

import bfx.Sequence

case class SequenceContext(seq: Sequence)

abstract class Expr {
  def walk(f: Expr => Unit)
}

case class Variable(name: Symbol) extends Expr {
  override def toString = name.toString
  def walk(f: Expr => Unit) { f(this) }
}


case class Function(name: Symbol, arg: Variable) extends Expr {
  override def toString = "[%s %s]".format(name, arg)
  override def walk(f: Expr => Unit) { f(arg); f(this) }
}

case class Number(value: Float) extends Expr {
  override def toString = value.toString
  override def walk(f: Expr => Unit) { f(this) }
}

case class StringLiteral(value: String) extends Expr {
  override def toString = value.toString
  override def walk(f: Expr => Unit) { f(this) }
}

case class UnaryOp(operator: Symbol, arg: Expr) extends Expr {
  override def toString = "(UOP %s %s)".format(operator, arg)
  override def walk(f: Expr => Unit) { arg.walk(f); f(this); }
}

case class BinaryOp(operator: Symbol, left: Expr, right: Expr) extends Expr {
  override def toString = "(BOP %s %s %s)".format(operator, left, right)
  override def walk(f: Expr => Unit) {
    left.walk(f)
    right.walk(f)
    f(this);
  }
}

object Expr {
  def eval(e: Expr)(cxt: SequenceContext): Boolean = {
    true
  }

  def validate(e: Expr) {
    e.walk(x => {
      x match {
        case x: Variable => println("f: Var " + x)
        case x: BinaryOp => println("f: OP " + x)
        case x: Expr => println(x)
      }
    })
  }

  //def  compile(e:Expr):FilterExpr = {
  //}
}

