package com.barrybecker4.java2d.imageproc

import java.awt.image.BufferedImageOp
import java.lang.reflect.Method
import java.util.logging.{Level, Logger}

import com.barrybecker4.optimization.parameter.types.Parameter

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/**
  * Contains an image operator and information about it (such as parameters).
  * @author Barry Becker
  */
object MetaImageOp {
  /** Ensures that all randomness is repeatable. */
  private val RANDOM: Random = new Random(1)
}

class MetaImageOp {
  private var opClass: Class[_ <: BufferedImageOp] = _
  private var op: BufferedImageOp = _
  /** list of base params based for creating concrete imageOps. */
  private var parameters: Seq[Parameter] = _
  /** last used list of params used to create recent imageOp. */
  private var lastUsedParameters: Seq[Parameter] = _
  private var isDynamic: Boolean = false

  /** Use this constructor if no parameters.
    * @param op image operator
    */
  def this(op: BufferedImageOp) {
    this()
    // an empty list of parameters because there are none.
    this.op = op
    isDynamic = false
  }

  /** @param opClass the operator class.
    * @param params  all the parameters that need to be set on the op.
    */
  def this(opClass: Class[_ <: BufferedImageOp], params: Seq[Parameter]) {
    this()
    this.opClass = opClass
    this.parameters = params
    isDynamic = true
  }

  /** @return a concrete filter operator instance. */
  def getInstance: BufferedImageOp = getRandomInstance(0)

  /** @param randomVariance number of standard deviations to use when randomizing params.
    * @return a concrete instance with tweaked parameters.
    */
  def getRandomInstance(randomVariance: Float): BufferedImageOp = {
    println("getting random. isDynamic=" + isDynamic + " randomVariance=" + randomVariance)
    if (!isDynamic) return op
    try {
      this.op = opClass.newInstance
      lastUsedParameters = tweakParameters(op, randomVariance)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Logger.getLogger(classOf[MetaImageOp].getName).log(Level.SEVERE, null, ex)
    }
    op
  }

  def getBaseParameters: Seq[Parameter] = parameters
  def getLastUsedParameters: Seq[Parameter] = lastUsedParameters

  def copy: MetaImageOp = if (isDynamic) new MetaImageOp(opClass, parameters)
  else new MetaImageOp(op)

  /** Call the methods on the filter to set its custom parameters.
    * @param filter the image filter to tweak
    * @param randomVariance amount to tweak
    */
  private def tweakParameters(filter: BufferedImageOp, randomVariance: Float): Seq[Parameter] = {
    System.out.println("op=" + filter.getClass.getSimpleName + " randomVariance=" + randomVariance)
    val newParams: ArrayBuffer[Parameter] = ArrayBuffer[Parameter]()

    for (p <- parameters) { // the name must match the property (e.g. foo will be set using setFoo)
      val methodName: String = "set" + p.name.substring(0, 1).toUpperCase + p.name.substring(1)
      println("methodName = " + methodName + " pType = " + p.getType)
      val method: Method = filter.getClass.getDeclaredMethod(methodName, p.getType)

      val args: Array[Any] = new Array(1)
      val param: Parameter = p.copy
      if (randomVariance > 0) param.tweakValue(randomVariance, MetaImageOp.RANDOM)
      println("tweaked min = " + param.minValue + " max = " + param.maxValue + "  v = " + param.getValue)
      println("tweaked value = " + param)
      newParams.append(param)
      // @@ This should work with autoboxing, but does not for some reason, so we resort to ugly case statement.
      //args[0] = param.getNaturalValue();
      val paramType: Class[_] = param.getType
      if (paramType == classOf[Float]) args(0) = param.getValue.toFloat
      else if (paramType == classOf[Int]) args(0) = param.getValue.toInt
      else if (paramType == classOf[Boolean]) args(0) = param.getNaturalValue.asInstanceOf[Boolean]
        else if (paramType == classOf[String]) args(0) = param.getNaturalValue.asInstanceOf[String]
          else throw new IllegalArgumentException("Unexpected param type = " + paramType)
      println("paramType = " + paramType)
      println("param val = " + args(0))
      method.invoke(filter, args) // p.getInformation().cast(p.getValue()));
    }
    newParams.toSeq
  }
}
