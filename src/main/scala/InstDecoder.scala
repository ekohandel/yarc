package yarc

import chisel3._
import chisel3.util._

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val rd = Output(UInt(4.W))
    val rs1 = Output(UInt(4.W))
    val rs2 = Output(UInt(4.W))
    val shamt = Output(UInt(4.W))
    val funct3 = Output(UInt(3.W))
    val funct7 = Output(UInt(7.W))
    val aluOp = Output(UInt(4.W))
    val imm = Output(UInt(32.W))
  })

  val rType :: iType :: sType :: bType :: uType :: jType :: undefType :: Nil =
    Enum(7)

  def GetInstEncoding(opcode: UInt) = {
    val encType = WireDefault(undefType)

    switch(opcode) {
      is(Opcode.LUI) { encType := uType }
      is(Opcode.AUIPC) { encType := uType }
      is(Opcode.JAL) { encType := jType }
      is(Opcode.JALR) { encType := iType }
      is(Opcode.BRANCH) { encType := bType }
      is(Opcode.LOAD) { encType := iType }
      is(Opcode.STORE) { encType := sType }
      is(Opcode.OP_IMM) { encType := iType }
      is(Opcode.OP) { encType := rType }
    }

    encType
  }

  io.opcode := io.inst(6, 0)
  io.rd := io.inst(11, 7)
  io.rs1 := io.inst(19, 15)
  io.rs2 := io.inst(24, 20)
  io.shamt := io.inst(24, 20)
  io.funct3 := io.inst(14, 12)
  io.funct7 := io.inst(31, 25)

  io.imm := 0.U(32.W)
  switch(GetInstEncoding(io.opcode)) {
    is(iType) {
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 25) ## io.inst(
        24,
        21
      ) ## io.inst(20, 20)
    }
    is(sType) {
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 25) ## io.inst(11, 8) ## io
        .inst(7, 7)
    }
    is(bType) {
      io.imm := Fill(20, io.inst(31)) ## io.inst(7, 7) ## io.inst(30, 25) ## io
        .inst(11, 8) ## 0.U(1.W)
    }
    is(uType) {
      io.imm := io.inst(31, 31) ## io.inst(30, 20) ## io.inst(19, 12) ## Fill(
        12,
        0.U
      )
    }
    is(jType) {
      io.imm := Fill(12, io.inst(31)) ## io.inst(19, 12) ## io.inst(20) ## io
        .inst(30, 25) ## io.inst(24, 21) ## 0.U(1.W)
    }
  }

  io.aluOp := 0.U(4.W)
  switch(io.funct3) {
    is("b000".U) {
      io.aluOp := Mux(io.funct7(5), IntOp.SUB, IntOp.ADD)
    }
    is("b001".U) {
      io.aluOp := IntOp.SLL
    }
    is("b010".U) {
      io.aluOp := IntOp.SLT
    }
    is("b011".U) {
      io.aluOp := IntOp.SLTU
    }
    is("b100".U) {
      io.aluOp := IntOp.XOR
    }
    is("b101".U) {
      io.aluOp := Mux(io.funct7(5), IntOp.SRA, IntOp.SRL)
    }
    is("b110".U) {
      io.aluOp := IntOp.OR
    }
    is("b111".U) {
      io.aluOp := IntOp.AND
    }
  }
}
