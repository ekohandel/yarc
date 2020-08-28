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
        poke(c.io.func, AluFunction.add)
        poke(c.io.operand1, 0)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0)

        //30 = 10 ADD 20
        poke(c.io.func, AluFunction.add)
        poke(c.io.operand1, 10)
        poke(c.io.operand2, 20)
        expect(c.io.result, 30)

        //0 = 1 ADD 0xFFFFFFFF
        poke(c.io.func, AluFunction.add)
        poke(c.io.operand1, 1)
        poke(c.io.operand2, 0xffffffff)
        expect(c.io.result, 0)

        //0 = 3 ADD 0xFFFFFFFE
        poke(c.io.func, AluFunction.add)
        poke(c.io.operand1, 3)
        poke(c.io.operand2, 0xfffffffe)
        expect(c.io.result, 1)
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
        poke(c.io.func, AluFunction.sll)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x0)
        expect(c.io.result, 0x2)

        // 0x4 = 0x2 SLL 0x1
        poke(c.io.func, AluFunction.sll)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x1)
        expect(c.io.result, 0x4)

        // 0x0 = 0x2 SLL 0x1F
        poke(c.io.func, AluFunction.sll)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x1f)
        expect(c.io.result, 0x0)

        // 0x8000_0000 = 0x1 SLL 0x1F
        poke(c.io.func, AluFunction.sll)
        poke(c.io.operand1, 0x1)
        poke(c.io.operand2, 0x1f)
        expect(c.io.result, 0x80000000L)
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
        poke(c.io.func, AluFunction.slt)
        poke(c.io.operand1, 2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0)

        // 1 = 2 SLT 5
        poke(c.io.func, AluFunction.slt)
        poke(c.io.operand1, 2)
        poke(c.io.operand2, 5)
        expect(c.io.result, 1)

        // 1 = -2 SLT 1
        poke(c.io.func, AluFunction.slt)
        poke(c.io.operand1, -2)
        poke(c.io.operand2, 1)
        expect(c.io.result, 1)

        // 1 = -2 SLT 0
        poke(c.io.func, AluFunction.slt)
        poke(c.io.operand1, -2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 1)

        // 0 = 0 SLT -2
        poke(c.io.func, AluFunction.slt)
        poke(c.io.operand1, 0)
        poke(c.io.operand2, -2)
        expect(c.io.result, 0)
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
        poke(c.io.func, AluFunction.sltu)
        poke(c.io.operand1, 2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0)

        // 1 = 2 SLT 5
        poke(c.io.func, AluFunction.sltu)
        poke(c.io.operand1, 2)
        poke(c.io.operand2, 5)
        expect(c.io.result, 1)

        // 0 = -2 SLT 1
        poke(c.io.func, AluFunction.sltu)
        poke(c.io.operand1, -2)
        poke(c.io.operand2, 1)
        expect(c.io.result, 0)

        // 0 = -2 SLT 0
        poke(c.io.func, AluFunction.sltu)
        poke(c.io.operand1, -2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0)

        // 1 = 0 SLT -2
        poke(c.io.func, AluFunction.sltu)
        poke(c.io.operand1, 0)
        poke(c.io.operand2, -2)
        expect(c.io.result, 1)

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
        poke(c.io.func, AluFunction.xor)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0x2)

        // 0x1 = 0x2 XOR 0x3
        poke(c.io.func, AluFunction.xor)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x3)
        expect(c.io.result, 0x1)
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
        poke(c.io.func, AluFunction.srl)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x0)
        expect(c.io.result, 0x2)

        // 0x1 = 0x2 SRL 0x1
        poke(c.io.func, AluFunction.srl)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x1)
        expect(c.io.result, 0x1)

        // 0x1 = 0xFFFFFFFF SRL 0x1F
        poke(c.io.func, AluFunction.srl)
        poke(c.io.operand1, 0xffffffff)
        poke(c.io.operand2, 0x1f)
        expect(c.io.result, 0x1)
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
        poke(c.io.func, AluFunction.or)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0x2)

        // 0x3 = 0x2 OR 0x3
        poke(c.io.func, AluFunction.or)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x3)
        expect(c.io.result, 0x3)
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
        poke(c.io.func, AluFunction.and)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0x0)

        // 0x2 = 0x2 AND 0x3
        poke(c.io.func, AluFunction.and)
        poke(c.io.operand1, 0x2)
        poke(c.io.operand2, 0x3)
        expect(c.io.result, 0x2)
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
        poke(c.io.func, AluFunction.sub)
        poke(c.io.operand1, 0)
        poke(c.io.operand2, 0)
        expect(c.io.result, 0)

        // -2 = 10 SUB 12
        poke(c.io.func, AluFunction.sub)
        poke(c.io.operand1, 10)
        poke(c.io.operand2, 12)
        expect(c.io.result, 0xfffffffeL)

        // 2 = 1 SUB -1
        poke(c.io.func, AluFunction.sub)
        poke(c.io.operand1, 1)
        poke(c.io.operand2, 0xffffffffL)
        expect(c.io.result, 2)
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
        poke(c.io.func, AluFunction.sra)
        poke(c.io.operand1, 1)
        poke(c.io.operand2, 0)
        expect(c.io.result, 1)

        // 0x2 = 0x8 SRA 2
        poke(c.io.func, AluFunction.sra)
        poke(c.io.operand1, 0x8)
        poke(c.io.operand2, 2)
        expect(c.io.result, 0x2)

        // 0xC0000000 = 0x80000000 SRA 1
        poke(c.io.func, AluFunction.sra)
        poke(c.io.operand1, 0x80000000L)
        poke(c.io.operand2, 1)
        expect(c.io.result, 0xc0000000L)
      }
    } should be(true)
  }
}
