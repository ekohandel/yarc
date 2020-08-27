// See LICENSE for license details.

import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import chisel3._
import yarc._

class AluTest extends FlatSpec with Matchers {
  "ADD" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0 = 0 ADD 0
        poke(c.io.func, IntOp.ADD)
        poke(c.io.input1, 0)
        poke(c.io.input2, 0)
        expect(c.io.output, 0)

        //30 = 10 ADD 20
        poke(c.io.func, IntOp.ADD)
        poke(c.io.input1, 10)
        poke(c.io.input2, 20)
        expect(c.io.output, 30)

        //0 = 1 ADD 0xFFFFFFFF
        poke(c.io.func, IntOp.ADD)
        poke(c.io.input1, 1)
        poke(c.io.input2, 0xffffffff)
        expect(c.io.output, 0)

        //0 = 3 ADD 0xFFFFFFFE
        poke(c.io.func, IntOp.ADD)
        poke(c.io.input1, 3)
        poke(c.io.input2, 0xfffffffe)
        expect(c.io.output, 1)
      }
    } should be(true)
  }

  "SLL" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0x2 = 0x2 SLL 0x0
        poke(c.io.func, IntOp.SLL)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x0)
        expect(c.io.output, 0x2)

        // 0x4 = 0x2 SLL 0x1
        poke(c.io.func, IntOp.SLL)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x1)
        expect(c.io.output, 0x4)

        // 0x0 = 0x2 SLL 0x1F
        poke(c.io.func, IntOp.SLL)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x1f)
        expect(c.io.output, 0x0)

        // 0x8000_0000 = 0x1 SLL 0x1F
        poke(c.io.func, IntOp.SLL)
        poke(c.io.input1, 0x1)
        poke(c.io.input2, 0x1f)
        expect(c.io.output, 0x80000000L)
      }
    } should be(true)
  }

  "SLT" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0 = 2 SLT 0
        poke(c.io.func, IntOp.SLT)
        poke(c.io.input1, 2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0)

        // 1 = 2 SLT 5
        poke(c.io.func, IntOp.SLT)
        poke(c.io.input1, 2)
        poke(c.io.input2, 5)
        expect(c.io.output, 1)

        // 1 = -2 SLT 1
        poke(c.io.func, IntOp.SLT)
        poke(c.io.input1, -2)
        poke(c.io.input2, 1)
        expect(c.io.output, 1)

        // 1 = -2 SLT 0
        poke(c.io.func, IntOp.SLT)
        poke(c.io.input1, -2)
        poke(c.io.input2, 0)
        expect(c.io.output, 1)

        // 0 = 0 SLT -2
        poke(c.io.func, IntOp.SLT)
        poke(c.io.input1, 0)
        poke(c.io.input2, -2)
        expect(c.io.output, 0)
      }
    } should be(true)
  }

  "SLTU" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0 = 2 SLT 0
        poke(c.io.func, IntOp.SLTU)
        poke(c.io.input1, 2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0)

        // 1 = 2 SLT 5
        poke(c.io.func, IntOp.SLTU)
        poke(c.io.input1, 2)
        poke(c.io.input2, 5)
        expect(c.io.output, 1)

        // 0 = -2 SLT 1
        poke(c.io.func, IntOp.SLTU)
        poke(c.io.input1, -2)
        poke(c.io.input2, 1)
        expect(c.io.output, 0)

        // 0 = -2 SLT 0
        poke(c.io.func, IntOp.SLTU)
        poke(c.io.input1, -2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0)

        // 1 = 0 SLT -2
        poke(c.io.func, IntOp.SLTU)
        poke(c.io.input1, 0)
        poke(c.io.input2, -2)
        expect(c.io.output, 1)

      }
    } should be(true)
  }

  "XOR" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0x2 = 0x2 XOR 0x0
        poke(c.io.func, IntOp.XOR)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0x2)

        // 0x1 = 0x2 XOR 0x3
        poke(c.io.func, IntOp.XOR)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x3)
        expect(c.io.output, 0x1)
      }
    } should be(true)
  }

  "SRL" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0x2 = 0x2 SRL 0x0
        poke(c.io.func, IntOp.SRL)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x0)
        expect(c.io.output, 0x2)

        // 0x1 = 0x2 SRL 0x1
        poke(c.io.func, IntOp.SRL)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x1)
        expect(c.io.output, 0x1)

        // 0x1 = 0xFFFFFFFF SRL 0x1F
        poke(c.io.func, IntOp.SRL)
        poke(c.io.input1, 0xffffffff)
        poke(c.io.input2, 0x1f)
        expect(c.io.output, 0x1)
      }
    } should be(true)
  }

  "OR" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0x2 = 0x2 OR 0x0
        poke(c.io.func, IntOp.OR)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0x2)

        // 0x3 = 0x2 OR 0x3
        poke(c.io.func, IntOp.OR)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x3)
        expect(c.io.output, 0x3)
      }
    } should be(true)
  }

  "AND" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0x0 = 0x2 AND 0x0
        poke(c.io.func, IntOp.AND)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0)
        expect(c.io.output, 0x0)

        // 0x2 = 0x2 AND 0x3
        poke(c.io.func, IntOp.AND)
        poke(c.io.input1, 0x2)
        poke(c.io.input2, 0x3)
        expect(c.io.output, 0x2)
      }
    } should be(true)
  }

  "SUB" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 0 = 0 SUB 0
        poke(c.io.func, IntOp.SUB)
        poke(c.io.input1, 0)
        poke(c.io.input2, 0)
        expect(c.io.output, 0)

        // -2 = 10 SUB 12
        poke(c.io.func, IntOp.SUB)
        poke(c.io.input1, 10)
        poke(c.io.input2, 12)
        expect(c.io.output, 0xfffffffeL)

        // 2 = 1 SUB -1
        poke(c.io.func, IntOp.SUB)
        poke(c.io.input1, 1)
        poke(c.io.input2, 0xffffffffL)
        expect(c.io.output, 2)
      }
    } should be(true)
  }

  "SRA" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new Alu()
    ) { c =>
      new PeekPokeTester(c) {
        // 1 = 1 SRA 0
        poke(c.io.func, IntOp.SRA)
        poke(c.io.input1, 1)
        poke(c.io.input2, 0)
        expect(c.io.output, 1)

        // 0x2 = 0x8 SRA 2
        poke(c.io.func, IntOp.SRA)
        poke(c.io.input1, 0x8)
        poke(c.io.input2, 2)
        expect(c.io.output, 0x2)

        // 0xC0000000 = 0x80000000 SRA 1
        poke(c.io.func, IntOp.SRA)
        poke(c.io.input1, 0x80000000L)
        poke(c.io.input2, 1)
        expect(c.io.output, 0xc0000000L)
      }
    } should be(true)
  }
}
