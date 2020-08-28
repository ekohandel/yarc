// See LICENSE for license details.

package yarc

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object AluFunction extends ChiselEnum {
  val add , sub , sll , slt , sltu , xor , srl , sra , or , and = Value
}

/**
  * Creates a RISC-V compliant ALU
  */
class Alu(xlen: Int = 32) extends Module {

  require(xlen == 32 || xlen == 64)

  val io = IO(new Bundle {
    val func = Input(AluFunction())
    val operand1 = Input(UInt(xlen.W))
    val operand2 = Input(UInt(xlen.W))
    val result = Output(UInt(xlen.W))
  })

  val shiftAmount = io.operand2(unsignedBitLength(xlen - 1), 0)

  io.result := 0.U
  switch(io.func) {
    is(AluFunction.add) { io.result := io.operand1 + io.operand2 }
    is(AluFunction.sll) { io.result := io.operand1 << shiftAmount }
    is(AluFunction.slt) { io.result := io.operand1.asSInt() < io.operand2.asSInt() }
    is(AluFunction.sltu) { io.result := io.operand1 < io.operand2 }
    is(AluFunction.xor) { io.result := io.operand1 ^ io.operand2 }
    is(AluFunction.srl) { io.result := io.operand1 >> shiftAmount }
    is(AluFunction.or) { io.result := io.operand1 | io.operand2 }
    is(AluFunction.and) { io.result := io.operand1 & io.operand2 }
    is(AluFunction.sub) { io.result := io.operand1 - io.operand2 }
    is(AluFunction.sra) { io.result := (io.operand1.asSInt() >> shiftAmount).asUInt() }
  }
}
