// See LICENSE for license details.

package yarc

import chisel3._
import chisel3.util._

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val rd = Output(UInt(5.W))
    val rs1 = Output(UInt(5.W))
    val rs2 = Output(UInt(5.W))
    val shamt = Output(UInt(5.W))
    val funct3 = Output(UInt(3.W))
    val funct7 = Output(UInt(7.W))
    val intOp = Output(UInt(4.W))
    val imm = Output(UInt(32.W))
    val valid = Output(Bool())
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
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 20)
    }
    is(sType) {
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 25) ## io.inst(11, 8) ## io.inst(7)
    }
    is(bType) {
      io.imm := Fill(20, io.inst(31)) ## io.inst(7) ## io.inst(30, 25) ## io.inst(11, 8) ## 0.U(1.W)
    }
    is(uType) {
      io.imm := io.inst(31, 12) ## Fill(12, 0.U)
    }
    is(jType) {
      io.imm := Fill(12, io.inst(31)) ## io.inst(19, 12) ## io.inst(20) ## io.inst(30, 21) ## 0.U(1.W)
    }
  }

  io.intOp := 0.U(1.W) ## io.funct3
  switch(io.opcode) {
    is(Opcode.OP_IMM) {
      switch(io.funct3) {
        is(IntOpFunct3.SR) { io.intOp := io.funct7(5) ## io.funct3 }
        is(IntOpFunct3.SLL) { io.intOp := io.funct7(5) ## io.funct3 }
      }
    }
    is(Opcode.OP) { io.intOp := io.funct7(5) ## io.funct3 }
  }

  io.valid := false.B
  switch(io.opcode) {
    is(Opcode.LUI) { io.valid := true.B }
    is(Opcode.AUIPC) { io.valid := true.B }
    is(Opcode.JAL) { io.valid := true.B }
    is(Opcode.JALR) {
      switch(io.funct3) { is("b000".U) { io.valid := true.B } }
    }
    is(Opcode.BRANCH) {
      switch(io.funct3) {
        is(Branch.EQ) { io.valid := true.B }
        is(Branch.NE) { io.valid := true.B }
        is(Branch.LT) { io.valid := true.B }
        is(Branch.GE) { io.valid := true.B }
        is(Branch.LTU) { io.valid := true.B }
        is(Branch.GEU) { io.valid := true.B }
      }
    }
    is(Opcode.LOAD) {
      switch(io.funct3) {
        is(Load.B) { io.valid := true.B }
        is(Load.H) { io.valid := true.B }
        is(Load.W) { io.valid := true.B }
        is(Load.BU) { io.valid := true.B }
        is(Load.HU) { io.valid := true.B }
      }
    }
    is(Opcode.STORE) {
      switch(io.funct3) {
        is(Load.B) { io.valid := true.B }
        is(Load.H) { io.valid := true.B }
        is(Load.W) { io.valid := true.B }
      }
    }
    is(Opcode.OP_IMM) {
      switch(io.intOp) {
        is(IntOp.ADD) { io.valid := true.B }
        is(IntOp.SLL) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SLT) { io.valid := true.B }
        is(IntOp.SLTU) { io.valid := true.B }
        is(IntOp.XOR) { io.valid := true.B }
        is(IntOp.SRL) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SRA) { io.valid := Mux(io.funct7 === "b0100000".U, true.B, false.B) }
        is(IntOp.OR) { io.valid := true.B }
        is(IntOp.AND) { io.valid := true.B }
      }
    }
    is(Opcode.OP) {
      switch(io.intOp) {
        is(IntOp.ADD) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SUB) { io.valid := Mux(io.funct7 === "b0100000".U, true.B, false.B) }
        is(IntOp.SLL) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SLT) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SLTU) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.XOR) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SRL) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.SRA) { io.valid := Mux(io.funct7 === "b0100000".U, true.B, false.B) }
        is(IntOp.OR) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
        is(IntOp.AND) { io.valid := Mux(io.funct7 === "b0000000".U, true.B, false.B) }
      }
    }
  }
}
