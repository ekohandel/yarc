// See LICENSE for license details.

package yarc

import chisel3._
import chisel3.util._

/**
  * A RISC-V compliant register file
  *
  * The register file exposes the following signals in its IO bundle:
  *   addr1[log2(num)-1:0]
  *   addr2[log2(num)-1:0]
  *   inVal1[XLEN-1:0]
  *   outVal1[XLEN-1:0]
  *   outVal2[XLEN-1:0]
  *   wrEn
  * outVal1 immediately reflects the register value for addr1
  * outVal2 immediately reflects the register value for addr2
  * inVal1 is stored in register for addr1 on rising edge of
  *        the clock if wrEn is high
  *
  * Register corresponding to addr=0 is always 0, writes to this
  * register have no effect.
  *
  * @param xlen the XLEN RISC-V value, only valid values are 32 and 64
  * @param num the number of registers created, only valid values are 16 and 32
  */
class RegFile(xlen: Int = 32, num: Int = 32) extends Module {

  require(xlen == 32 || xlen == 64)
  require(num == 16 || num == 32)

  val io = IO(new Bundle {
    val addr1 = Input(UInt(unsignedBitLength(num - 1).W))
    val addr2 = Input(UInt(unsignedBitLength(num - 1).W))

    val inVal1 = Input(UInt(xlen.W))

    val outVal1 = Output(UInt(xlen.W))
    val outVal2 = Output(UInt(xlen.W))

    val wrEn = Input(Bool())
  })

  val reg = RegInit(VecInit(Seq.fill(num)(0.U(xlen.W))))

  /* combinationally reflect register values at output */
  io.outVal1 := Mux(io.addr1 === 0.U, 0.U, reg(io.addr1))
  io.outVal2 := Mux(io.addr2 === 0.U, 0.U, reg(io.addr2))

  /* sequentially update register value other than reg[0] when wrEn */
  when(io.wrEn) { reg(io.addr1) := Mux(io.addr1 === 0.U, 0.U, io.inVal1) }
}
