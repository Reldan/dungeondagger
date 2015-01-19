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


  def terrain(width: Int, height: Int, depth: Int, gen: ModuleFractal): Array[Int] = {
    val data = Array.fill(width * height)(0)
    for (x ← 0 until width; y ← 0 until height) {
//         println(gen.get(x, y))
         val opt = Math.abs(((gen.get(x, y) + 1) * depth).toInt)
//         println(opt)
         data(y * width + x) = Math.min(opt, depth)
      }
    data
  }

  def main(args: Array[String]) = {
    val gen = newGen()
//    println(terrain(10, 10, 6, gen).mkString)
  }
}