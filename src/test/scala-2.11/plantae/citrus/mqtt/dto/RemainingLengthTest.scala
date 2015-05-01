package plantae.citrus.mqtt.dto

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RemainingLengthTest extends FunSuite {
  // 0 => hex"00"
  // 127 => hex"7f"
  // 128 => hex"8001"
  // 16383 => hex"ff7f"
  // 16384 => hex"808001"
  // 2097151 => hex"ffff7f"
  // 2097152 => hex"80808001"
  // 26835455 => hex"ffffff7f"

  // -1 => error
  // 268435455 => error

  test("Remaining Length Decode Test") {

  }

  test("Remaining Length Encode Test") {

  }

}
