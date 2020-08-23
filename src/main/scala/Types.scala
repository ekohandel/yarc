package yarc

import chisel3._

object Opcode {
  val LUI = "b0110111".U
  val AUIPC = "b0010111".U
  val JAL = "b1101111".U
  val JALR = "b1100111".U
  val BRANCH = "b1100011".U
  val LOAD = "b0000011".U
  val STORE = "b0100011".U
  val OP_IMM = "b0010011".U
  val OP = "b0110011".U
  val MISC_MEM = "b0001111".U
  val SYSTEM = "b1110011".U
}

object Branch {
  val EQ = "b000".U
  val NE = "b001".U
  val LT = "b100".U
  val GE = "b101".U
  val LTU = "b110".U
  val GEU = "b111".U
}

object Load {
  val B = "b000".U
  val H = "b001".U
  val W = "b010".U
  val BU = "b100".U
  val HU = "b101".U
}

object Store {
  val B = "b000".U
  val H = "b001".U
  val W = "b010".U
}

object IntOp {
  val ADD = "b0000".U
  val SUB = "b1000".U
  val SLL = "b0001".U
  val SLT = "b0010".U
  val SLTU = "b0011".U
  val XOR = "b0100".U
  val SRL = "b0101".U
  val SRA = "b1101".U
  val OR = "b0110".U
  val AND = "b0111".U
}
