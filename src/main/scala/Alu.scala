// See LICENSE for license details.

package yarc

import chisel3._
import chisel3.util._

/**
  * Creates a RISC-V compliant ALU
  */
class Alu extends Module {
  val io = IO(new Bundle {
    val func = Input(UInt(4.W))
    val input1 = Input(UInt(32.W))
    val input2 = Input(UInt(32.W))
    val output = Output(UInt(32.W))
  })

  io.output := 0.U(32.W)

  val shamt = io.input2(4, 0)

  switch(io.func) {
    is(IntOp.ADD) { io.output := io.input1 + io.input2 }
    is(IntOp.SLL) { io.output := io.input1 << shamt }
    is(IntOp.SLT) { io.output := io.input1.asSInt() < io.input2.asSInt() }
    is(IntOp.SLTU) { io.output := io.input1 < io.input2 }
    is(IntOp.XOR) { io.output := io.input1 ^ io.input2 }
    is(IntOp.SRL) { io.output := io.input1 >> shamt }
    is(IntOp.OR) { io.output := io.input1 | io.input2 }
    is(IntOp.AND) { io.output := io.input1 & io.input2 }
    is(IntOp.SUB) { io.output := io.input1 - io.input2 }
    is(IntOp.SRA) { io.output := (io.input1.asSInt() >> shamt).asUInt() }
  }
}
