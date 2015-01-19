package dungeondagger

import com.sudoplay.joise.module.ModuleFractal
import com.sudoplay.joise.module.ModuleBasisFunction.{InterpolationType, BasisType}
import com.sudoplay.joise.module.ModuleFractal.FractalType
import scala.util.Random

object Generator {

  def newGen() = {
    val gen: ModuleFractal = new ModuleFractal()
    gen.setAllSourceBasisTypes(BasisType.GRADIENT)
    gen.setAllSourceInterpolationTypes(InterpolationType.QUINTIC)
    gen.setNumOctaves(10)
    gen.setFrequency(0.005)
    gen.setType(FractalType.BILLOW)
    gen.setSeed(new Random().nextInt(1000))
    gen
  }


  def terrain(width: Int, height: Int, depth: Int, gen: ModuleFractal): Array[Int] =
    Array.tabulate(width * height) { i =>
      val y = i / width
      val x = i % width
      val opt = Math.abs(((gen.get(x, y) + 1) * depth).toInt)
      Math.min(opt, depth)
    }

  def main(args: Array[String]) = {
    val gen = newGen()
//    println(terrain(10, 10, 6, gen).mkString)
  }
}