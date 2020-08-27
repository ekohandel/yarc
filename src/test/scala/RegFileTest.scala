import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import scala.math._
import chisel3._
import yarc._

class RegFileTest extends FlatSpec with Matchers {
  a[ChiselException] should be thrownBy {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new RegFile(xlen = 10)
    ) { c =>
      new PeekPokeTester(c) {}
    }
  }

  a[ChiselException] should be thrownBy {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new RegFile(num = 12)
    ) { c =>
      new PeekPokeTester(c) {}
    }
  }

  for (xlen <- List(32, 64); num <- List(16, 32)) {
    s"(xlen=${xlen},num=${num}): Initialzed" should "be all zeros" in {
      chisel3.iotesters.Driver.execute(
        Array("--target-dir", "target"),
        () => new RegFile(xlen = xlen, num = num)
      ) { c =>
        new PeekPokeTester(c) {
          poke(c.io.wrEn, 1.U)
          poke(c.io.inVal1, 0.U)

          for (addr <- 0 to math.pow(2, c.io.addr1.getWidth - 1).toInt) {
            poke(c.io.addr1, addr)
            poke(c.io.addr2, addr)
            expect(c.io.outVal1, 0)
            expect(c.io.outVal2, 0)
          }
        }
      } should be(true)
    }

    s"(xlen=${xlen},num=${num}): Writes" should "show up on output after a clock cycle" in {
      chisel3.iotesters.Driver.execute(
        Array("--target-dir", "target"),
        () => new RegFile(xlen = xlen, num = num)
      ) { c =>
        new PeekPokeTester(c) {
          for (addr <- 1 to math.pow(2, c.io.addr1.getWidth).toInt - 1) {
            poke(c.io.addr1, addr.U)
            poke(c.io.addr2, addr.U)
            poke(c.io.inVal1, (2 * addr).U)

            expect(c.io.outVal1, 0.U)
            expect(c.io.outVal2, 0.U)

            poke(c.io.wrEn, true.B)
            step(1)
            poke(c.io.wrEn, false.B)

            poke(c.io.addr1, addr.U)
            poke(c.io.addr2, (addr - 1).U)
            expect(c.io.outVal1, (2 * addr).U)
            expect(c.io.outVal2, (2 * (addr - 1)).U)
          }
        }
      } should be(true)
    }

    s"(xlen=${xlen},num=${num}): Writes to addr 0" should "have no effect" in {
      chisel3.iotesters.Driver.execute(
        Array("--target-dir", "target"),
        () => new RegFile(xlen = xlen, num = num)
      ) { c =>
        new PeekPokeTester(c) {
          for (value <- 1 to 256) {
            poke(c.io.addr1, 0.U)
            poke(c.io.inVal1, value.U)

            poke(c.io.wrEn, true.B)
            step(1)
            poke(c.io.wrEn, false.B)

            expect(c.io.outVal1, 0.U)

            step(1)
          }
        }
      } should be(true)
    }
  }
}
