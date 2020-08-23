import chisel3.iotesters.PeekPokeTester
import org.scalatest._
import chisel3._
import yarc._

class InstDecoderTest extends FlatSpec with Matchers {
  "ADD" should "pass" in {
    chisel3.iotesters.Driver.execute(
      Array("--target-dir", "target"),
      () => new InstDecoder()
    ) { c =>
      new PeekPokeTester(c) {}
    } should be(true)
  }
}
