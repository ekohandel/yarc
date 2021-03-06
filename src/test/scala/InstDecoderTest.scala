// See LICENSE for license details.

import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import chisel3._
import yarc._

class InstDecoderTest extends FlatSpec with Matchers {
  "LUI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b00000000000000000000_00000_0110111".U)
        expect(c.io.opcode, InstDecoder.opcode.LUI)
        expect(c.io.rd, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b10000000000000000000_10001_0110111".U)
        expect(c.io.opcode, InstDecoder.opcode.LUI)
        expect(c.io.rd, 0x11)
        expect(c.io.imm, 0x80000000L)
      }
    } should be(true)
  }

  "AUIPC" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b00000000000000000000_00000_0010111".U)
        expect(c.io.opcode, InstDecoder.opcode.AUIPC)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b10000000000000000000_10001_0010111".U)
        expect(c.io.opcode, InstDecoder.opcode.AUIPC)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.imm, 0x80000000L)
      }
    } should be(true)
  }

  "JAL" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0000_0000_0000_0000_0000_00000_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b1000_0000_0000_0000_0000_00010_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 2)
        expect(c.io.imm, 0xFFF00000L)

        poke(c.io.inst, "b1000_0000_0000_1000_0001_00010_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 2)
        expect(c.io.imm, 0xFFF81000L)

        poke(c.io.inst, "b1000_0000_0001_1000_0001_00010_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 2)
        expect(c.io.imm, 0xFFF81800L)

        poke(c.io.inst, "b1000_0001_0011_1000_0001_00010_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 2)
        expect(c.io.imm, 0xFFF81812L)

        poke(c.io.inst, "b1100_0011_0011_1000_0001_00010_1101111".U)
        expect(c.io.opcode, InstDecoder.opcode.JAL)
        expect(c.io.valid, 1)
        expect(c.io.rd, 2)
        expect(c.io.imm, 0xFFF81C32L)
      }
    } should be(true)
  }

  "JALR" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_000_00000_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_000_10001_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_101_10001_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_000_10001_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_000_10001_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_000_10001_1100111".U)
        expect(c.io.opcode, InstDecoder.opcode.JALR)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "BRANCH" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0_000000_00000_00000_000_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.EQ)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_001_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.NE)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_010_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_011_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_100_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.LT)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_101_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_110_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.LTU)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_00000_111_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GEU)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_00000_10001_101_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_10011_10001_101_0000_0_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0_000000_10011_10001_101_0000_1_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0x13)
        expect(c.io.imm, 0x800)

        poke(c.io.inst, "b0_000000_10011_10001_101_1001_1_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0x13)
        expect(c.io.imm, 0x812)

        poke(c.io.inst, "b0_100001_10011_10001_101_1001_1_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0x13)
        expect(c.io.imm, 0xC32)

        poke(c.io.inst, "b1_100001_10011_10001_101_1001_1_1100011".U)
        expect(c.io.opcode, InstDecoder.opcode.BRANCH)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.branch.GE)
        expect(c.io.rs1, 0x11)
        expect(c.io.rs2, 0x13)
        expect(c.io.imm, 0xFFFFFC32L)
      }
    } should be(true)
  }

  "LOAD" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_000_00000_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.width.B)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_000_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.B)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_001_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.H)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_010_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_011_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_100_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.BU)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_101_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.HU)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_110_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_111_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_101_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.HU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_101_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.HU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_101_10001_0000011".U)
        expect(c.io.opcode, InstDecoder.opcode.LOAD)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.width.HU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "STORE" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0000000_00000_00000_000_00000_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.B)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b0000000_00000_00000_000_00001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.B)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b0000000_00000_00000_000_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.B)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_001_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.H)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_010_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_011_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_100_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_101_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_110_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_00000_111_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 0)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_00000_10011_010_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000000_10111_10011_010_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x17)
        expect(c.io.imm, 0x11)

        poke(c.io.inst, "b0000001_10111_10011_010_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x17)
        expect(c.io.imm, 0x31)

        poke(c.io.inst, "b1000001_10111_10011_010_10001_0100011".U)
        expect(c.io.opcode, InstDecoder.opcode.STORE)
        expect(c.io.valid, 1)
        expect(c.io.funct3, InstDecoder.funct3.width.W)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x17)
        expect(c.io.imm, 0xFFFFF831L)
      }
    } should be(true)
  }

  "ADDI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_000_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_000_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_000_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_000_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_000_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "SLTI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_010_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_010_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_010_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_010_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_010_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "SLTIU" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_011_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_011_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_011_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_011_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_011_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "XORI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_100_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_100_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_100_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_100_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_100_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "ORI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_110_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_110_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_110_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_110_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_110_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "ANDI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b000000000000_00000_111_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.ANDI)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_00000_111_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ANDI)
        expect(c.io.rs1, 0)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000000_10011_111_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ANDI)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0)

        poke(c.io.inst, "b000000000001_10011_111_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ANDI)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 1)

        poke(c.io.inst, "b100000000001_10011_111_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ANDI)
        expect(c.io.rs1, 0x13)
        expect(c.io.imm, 0xFFFFF801L)
      }
    } should be(true)
  }

  "SLLI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0000000_00000_00000_001_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLLI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_00000_001_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLLI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_10011_001_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLLI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_11001_10011_001_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLLI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1000000_11001_10011_001_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0100000_11001_10011_001_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)
      }
    } should be(true)
  }

  "SRLI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0000000_00000_00000_101_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRLI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_00000_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRLI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRLI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRLI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRLI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1000000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1100000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)
      }
    } should be(true)
  }

  "SRAI" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0100000_00000_00000_101_00000_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRAI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRAI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0100000_00000_00000_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRAI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRAI)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0100000_00000_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRAI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRAI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0100000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRAI)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRAI)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1100000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_101_10001_0010011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP_IMM)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)
      }
    } should be(true)
  }

  "OP" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {
        poke(c.io.inst, "b0000000_00000_00000_000_00000_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.funct7, InstDecoder.funct7.alu.ADD)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_00000_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.funct7, InstDecoder.funct7.alu.ADD)
        expect(c.io.rs1, 0)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_00000_10011_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.funct7, InstDecoder.funct7.alu.ADD)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0)

        poke(c.io.inst, "b0000000_11001_10011_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.ADD)
        expect(c.io.funct7, InstDecoder.funct7.alu.ADD)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1000000_11001_10011_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0100000_11001_10011_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SUB)
        expect(c.io.funct7, InstDecoder.funct7.alu.SUB)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_000_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_001_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLL)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLL)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_001_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_010_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLT)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLT)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_010_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_011_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SLTU)
        expect(c.io.funct7, InstDecoder.funct7.alu.SLTU)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_011_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_100_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.XOR)
        expect(c.io.funct7, InstDecoder.funct7.alu.XOR)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_100_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_101_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRL)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRL)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_101_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0100000_11001_10011_101_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.SRA)
        expect(c.io.funct7, InstDecoder.funct7.alu.SRA)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_101_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_110_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.OR)
        expect(c.io.funct7, InstDecoder.funct7.alu.OR)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_110_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b0000000_11001_10011_111_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 1)
        expect(c.io.rd, 0x11)
        expect(c.io.funct3, InstDecoder.funct3.alu.AND)
        expect(c.io.funct7, InstDecoder.funct7.alu.AND)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)

        poke(c.io.inst, "b1110000_11001_10011_111_10001_0110011".U)
        expect(c.io.opcode, InstDecoder.opcode.OP)
        expect(c.io.valid, 0)
        expect(c.io.rd, 0x11)
        expect(c.io.rs1, 0x13)
        expect(c.io.rs2, 0x19)
      }
    } should be(true)
  }
}
