// See LICENSE for license details.

package yarc

import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object InstDecoder {
  object opcode {
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
  object funct3 {
    object alu {
      val ADDI = "b000".U
      val SLLI = "b001".U
      val SLTI = "b010".U
      val SLTUI = "b011".U
      val XORI = "b100".U
      val SRLI = "b101".U
      val SRAI = "b101".U
      val ORI = "b110".U
      val ANDI = "b111".U
      val ADD = "b000".U
      val SUB = "b000".U
      val SLL = "b001".U
      val SLT = "b010".U
      val SLTU = "b011".U
      val XOR = "b100".U
      val SRL = "b101".U
      val SRA = "b101".U
      val OR = "b110".U
      val AND = "b111".U
    }
    object width {
      val B = "b000".U
      val H = "b001".U
      val W = "b010".U
      val BU = "b100".U
      val HU = "b101".U
    }
    object branch {
      val EQ = "b000".U
      val NE = "b001".U
      val LT = "b100".U
      val GE = "b101".U
      val LTU = "b110".U
      val GEU = "b111".U
    }
  }
  object funct7 {
    object alu {
      val SLLI = "b0000000".U
      val SRLI = "b0000000".U
      val SRAI = "b0100000".U
      val ADD  = "b0000000".U
      val SUB  = "b0100000".U
      val SLL  = "b0000000".U
      val SLT  = "b0000000".U
      val SLTU = "b0000000".U
      val XOR  = "b0000000".U
      val SRL  = "b0000000".U
      val SRA  = "b0100000".U
      val OR   = "b0000000".U
      val AND  = "b0000000".U
    }
  }
}

class InstDecoder extends Module {
  val io = IO(new Bundle {
    val inst = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val rd = Output(UInt(5.W))
    val rs1 = Output(UInt(5.W))
    val rs2 = Output(UInt(5.W))
    val funct3 = Output(UInt(3.W))
    val funct7 = Output(UInt(7.W))
    val imm = Output(UInt(32.W))
    val valid = Output(Bool())
  })

  object InstType extends ChiselEnum {
    val r, i, s, b, u, j, undef = Value
  }

  def GetInstEncoding(opcode: UInt) = {
    val encType = WireDefault(InstType.undef)

    switch(opcode) {
      is(InstDecoder.opcode.LUI) { encType := InstType.u }
      is(InstDecoder.opcode.AUIPC) { encType := InstType.u }
      is(InstDecoder.opcode.JAL) { encType := InstType.j }
      is(InstDecoder.opcode.JALR) { encType := InstType.i }
      is(InstDecoder.opcode.BRANCH) { encType := InstType.b }
      is(InstDecoder.opcode.LOAD) { encType := InstType.i }
      is(InstDecoder.opcode.STORE) { encType := InstType.s }
      is(InstDecoder.opcode.OP_IMM) { encType := InstType.i }
      is(InstDecoder.opcode.OP) { encType := InstType.r }
    }

    encType
  }

  io.opcode := io.inst(6, 0)
  io.rd := io.inst(11, 7)
  io.rs1 := io.inst(19, 15)
  io.rs2 := io.inst(24, 20)
  io.funct3 := io.inst(14, 12)
  io.funct7 := io.inst(31, 25)

  io.imm := 0.U(32.W)
  switch(GetInstEncoding(io.opcode)) {
    is(InstType.i) {
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 20)
    }
    is(InstType.s) {
      io.imm := Fill(21, io.inst(31)) ## io.inst(30, 25) ## io.inst(11, 8) ## io
        .inst(7)
    }
    is(InstType.b) {
      io.imm := Fill(20, io.inst(31)) ## io.inst(7) ## io.inst(30, 25) ## io
        .inst(11, 8) ## 0.U(1.W)
    }
    is(InstType.u) {
      io.imm := io.inst(31, 12) ## Fill(12, 0.U)
    }
    is(InstType.j) {
      io.imm := Fill(12, io.inst(31)) ## io.inst(19, 12) ## io.inst(20) ## io
        .inst(30, 21) ## 0.U(1.W)
    }
  }

  io.valid := false.B
  switch(io.opcode) {
    is(InstDecoder.opcode.LUI) { io.valid := true.B }
    is(InstDecoder.opcode.AUIPC) { io.valid := true.B }
    is(InstDecoder.opcode.JAL) { io.valid := true.B }
    is(InstDecoder.opcode.JALR) {
      switch(io.funct3) { is("b000".U) { io.valid := true.B } }
    }
    is(InstDecoder.opcode.BRANCH) {
      switch(io.funct3) {
        is(InstDecoder.funct3.branch.EQ) { io.valid := true.B }
        is(InstDecoder.funct3.branch.NE) { io.valid := true.B }
        is(InstDecoder.funct3.branch.LT) { io.valid := true.B }
        is(InstDecoder.funct3.branch.GE) { io.valid := true.B }
        is(InstDecoder.funct3.branch.LTU) { io.valid := true.B }
        is(InstDecoder.funct3.branch.GEU) { io.valid := true.B }
      }
    }
    is(InstDecoder.opcode.LOAD) {
      switch(io.funct3) {
        is(InstDecoder.funct3.width.B) { io.valid := true.B }
        is(InstDecoder.funct3.width.H) { io.valid := true.B }
        is(InstDecoder.funct3.width.W) { io.valid := true.B }
        is(InstDecoder.funct3.width.BU) { io.valid := true.B }
        is(InstDecoder.funct3.width.HU) { io.valid := true.B }
      }
    }
    is(InstDecoder.opcode.STORE) {
      switch(io.funct3) {
        is(InstDecoder.funct3.width.B) { io.valid := true.B }
        is(InstDecoder.funct3.width.H) { io.valid := true.B }
        is(InstDecoder.funct3.width.W) { io.valid := true.B }
      }
    }
    is(InstDecoder.opcode.OP_IMM) {
      when(io.funct3 =/= InstDecoder.funct3.alu.SLLI && io.funct3 =/= InstDecoder.funct3.alu.SRLI && io.funct3 =/= InstDecoder.funct3.alu.SRAI) {
        io.valid := true.B
      } .elsewhen (io.funct3 === InstDecoder.funct3.alu.SLLI && io.funct7 === InstDecoder.funct7.alu.SLLI) {
        io.valid := true.B
      } .elsewhen (io.funct3 === InstDecoder.funct3.alu.SRLI && io.funct7 === InstDecoder.funct7.alu.SRLI) {
        io.valid := true.B
      } .elsewhen (io.funct3 === InstDecoder.funct3.alu.SRAI && io.funct7 === InstDecoder.funct7.alu.SRAI) {
        io.valid := true.B
      } .otherwise {
        io.valid := false.B
      }
    }
    is(InstDecoder.opcode.OP) {
      when(io.funct3 === InstDecoder.funct3.alu.ADD && io.funct7 === InstDecoder.funct7.alu.ADD) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SUB && io.funct7 === InstDecoder.funct7.alu.SUB) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SLL && io.funct7 === InstDecoder.funct7.alu.SLL) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SLT && io.funct7 === InstDecoder.funct7.alu.SLT) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SLTU && io.funct7 === InstDecoder.funct7.alu.SLTU) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.XOR && io.funct7 === InstDecoder.funct7.alu.XOR) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SRL && io.funct7 === InstDecoder.funct7.alu.SRL) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.SRA && io.funct7 === InstDecoder.funct7.alu.SRA) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.OR && io.funct7 === InstDecoder.funct7.alu.OR) {
        io.valid := true.B
      } .elsewhen(io.funct3 === InstDecoder.funct3.alu.AND && io.funct7 === InstDecoder.funct7.alu.AND) {
        io.valid := true.B
      } .otherwise {
        io.valid := false.B
      }
    }
  }
}
