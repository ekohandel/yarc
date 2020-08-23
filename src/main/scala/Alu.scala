// See README.md for license details.

package yarc

import chisel3._
import chisel3.util._

/**
  * Perform RISC-V compliant ALU operations
  *
  * @param xlen word length
  * @param flen function length
  *
  * @note flen must be a minimum of 4
  */
class Alu(xlen: Int, flen: Int) extends Module {
  final val shiftImmediateWidth = 5

  val io = IO(new Bundle {
    val func = Input(UInt(flen.W))
    val input1 = Input(UInt(xlen.W))
    val input2 = Input(UInt(xlen.W))
    val output = Output(UInt(xlen.W))
  })

  require(flen >= 4)

  io.output := 0.U(xlen.W)

  switch(io.func) {
    is(IntOp.ADD) { io.output := io.input1 + io.input2 }
    is(IntOp.SLL) {
      io.output := io.input1 << io.input2(shiftImmediateWidth - 1, 0)
    }
    is(IntOp.SLT) { io.output := io.input1.asSInt() < io.input2.asSInt() }
    is(IntOp.SLTU) { io.output := io.input1 < io.input2 }
    is(IntOp.XOR) { io.output := io.input1 ^ io.input2 }
    is(IntOp.SRL) {
      io.output := io.input1 >> io.input2(shiftImmediateWidth - 1, 0)
    }
    is(IntOp.OR) { io.output := io.input1 | io.input2 }
    is(IntOp.AND) { io.output := io.input1 & io.input2 }
    is(IntOp.SUB) { io.output := io.input1 - io.input2 }
    is(IntOp.SRA) {
      io.output := (io.input1.asSInt() >> io.input2(shiftImmediateWidth - 1, 0))
        .asUInt()
    }
  }
}
