package bfx.tools.sequence.filter

abstract case class IR {
  def typ:Symbol;
}

abstract case class NamedIR extends IR {
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

case class IRBinaryOP(op:Symbol,left:IR,right:IR,method:String,typ:Symbol) extends NamedIR {
  def name = op
}


object IR {
  def compile1(e: Expr):Any = {
    
    val r = e match {
      case e: Variable => IRVariable.get(e.name)
      case e: Number => IRConst(e.value,'number)
      case e: BinaryOp => e //IRBinaryOP(compile1(e.left), e.operator.name, compile1(e.right))
      case e: Function => e // Functions.get(e.name.name, compile(e.arg))
      case e: UnaryOp => e //URUnaryOp(e.operator, compile(e.arg))
    }
    r
  }  
}