package bfx.tools.sequence.filter

abstract  class IR {
  def typ:Symbol;
}

case class Unimplemented extends IR {
  def typ = 'Unknown
}

abstract  class NamedIR extends IR {
  def name:Symbol;
}

trait IRUtils {
	def makeMap[T <: NamedIR](vars: T*) = {
		 vars.map(x => (x.name,x)).toMap 
	}
}

case class IRVariable(name: Symbol,typ: Symbol, method: String) extends NamedIR;

object IRVariable extends IRUtils {
  var variables = makeMap(IRVariable('length, 'number, "length"),
		  				  IRVariable('id, 'string, "id"),
                          IRVariable('comment, 'string, "comment"),
                          IRVariable('quality, 'numbers, "qual"))
                          
     def get(name:Symbol):IR = variables(name)
}

case class IRFunction(name:Symbol,inType:Symbol,typ:Symbol,method:String) extends NamedIR
case class IRUnaryOp(name:Symbol,inType:Symbol,typ:Symbol,method:String) extends NamedIR


object IRFunction extends IRUtils {
  var functions = makeMap(IRFunction('mean,'numbers,'number,"mean"),
                          IRFunction('median,'numbers,'number,"median"),
                          IRFunction('min,'numbers,'number,"min"),
                          IRFunction('max,'numbers,'number,"max"))
                          
                          
      def get(name:Symbol):IR = functions(name)                      
}

case class IRConst(value:Float,override val typ:Symbol) extends IR

case class IROperator(op:Symbol,typ:Symbol,operator:String) extends NamedIR {
  def name = op
}

object IROperator extends IRUtils {
    var operators = makeMap(IROperator('and,'boolean,"&&"))

    def get(op:Symbol) = operators(op)
}
case class IRBinaryOP(op:IROperator,left:IR,right:IR) extends IR {
  def typ = op.typ
}

object IRBinaryOP { 
	def make(op:Symbol,left:IR,right:IR) = {
	  val iop = IROperator.get(op)
	  IRBinaryOP(iop,left,right)
	  
	}
}


object IR {
  def compile1(e: Expr):IR = {
    
    val r = e match {
      case e: Variable => IRVariable.get(e.name)
      case e: Number => IRConst(e.value,'number)
      case e: BinaryOp => IRBinaryOP.make(e.operator,compile1(e.left),compile1(e.right))
      case e: Function => Unimplemented() // IRFunction(e.op,compile1(e.arg))
      case e: UnaryOp => Unimplemented() //IRUnaryOp(e.op,compile1(e.arg))
    }
    r
  }  
}