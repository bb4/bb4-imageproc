/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import java.awt.image.BufferedImageOp
import java.lang.reflect.Method
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

/**
  * Information (like parameters) about the image operator
  * @param op the meta op
  * @param parameters list of base params based for creating concrete imageOps.
  * @param isDynamic if it can be changed
  */
class MetaImageOp(op: BufferedImageOp, val parameters: Seq[Parameter], isDynamic: Boolean = true) {

  /** last used list of params used to create recent imageOp. */
  private var lastUsedParameters: IndexedSeq[Parameter] = parameters.map(_.copy).toIndexedSeq

  def this(op: BufferedImageOp) {
    // an empty list of parameters because there are none.
    this(op, Seq(), false)
  }

  /** @param opClass the operator class.
    * @param params  all the parameters that need to be set on the op.
    */
  def this(opClass: Class[_ <: BufferedImageOp], params: Seq[Parameter]) {
    this(opClass.newInstance, params, true)
  }

  /** @return a concrete filter operator instance. */
  def getInstance: BufferedImageOp = getRandomInstance(0)

  /** @param randomVariance number of standard deviations to use when randomizing params.
    * @return a concrete instance with tweaked parameters.
    */
  def getRandomInstance(randomVariance: Float): BufferedImageOp = {
    println("getting random. isDynamic=" + isDynamic + " randomVariance=" + randomVariance)
    if (!isDynamic) op
    else {
      lastUsedParameters = tweakParameters(op, randomVariance)
      op
    }
  }

  /** @param param the parameter to update */
  def updateParameter(param: Parameter): Unit = {
    var idx: Int = 0
    while (idx < lastUsedParameters.length && lastUsedParameters(idx).name != param.name)
      idx += 1

    assert(idx < lastUsedParameters.length)
    lastUsedParameters = lastUsedParameters.updated(idx,  param)
  }

  def getBaseParameters: Seq[Parameter] = parameters
  def getLastUsedParameters: Seq[Parameter] = lastUsedParameters

  def copy: MetaImageOp = if (isDynamic) new MetaImageOp(op.getClass, parameters)
  else new MetaImageOp(op)

  /** Call the methods on the filter to set its custom parameters.
    * @param filter the image filter to tweak
    * @param randomVariance amount to tweak
    */
  private def tweakParameters(filter: BufferedImageOp, randomVariance: Float): IndexedSeq[Parameter] = {
    println("op=" + filter.getClass.getSimpleName + " randomVariance=" + randomVariance)
    val newParams: ArrayBuffer[Parameter] = ArrayBuffer[Parameter]()

    for (p <- lastUsedParameters) { // the name must match the property (e.g. foo will be set using setFoo)
      val methodName: String = "set" + p.name.substring(0, 1).toUpperCase + p.name.substring(1)
      println("methodName = " + methodName + " pType = " + p.getType)
      val method: Method = filter.getClass.getDeclaredMethod(methodName, p.getType)

      var param: Parameter = p
      if (randomVariance > 0)
        param = param.tweakValue(randomVariance, MetaImageOp.RANDOM)

      println("tweaked min = " + param.minValue + " max = " + param.maxValue + "  v = " + param.getValue)
      println("tweaked value = " + param)
      newParams.append(param)
      // @@ This should work with autoboxing, but does not for some reason, so we resort to ugly case statement.
      //args[0] = param.getNaturalValue();
      val paramType: Class[_] = param.getType

      val arg =
        if (paramType == classOf[Float]) new java.lang.Float(param.getValue.toFloat)
        else if (paramType == classOf[Int]) new java.lang.Integer(param.getValue.toInt)
        else if (paramType == classOf[Boolean]) new java.lang.Boolean(param.getNaturalValue.asInstanceOf[Boolean])
        else if (paramType == classOf[String]) param.getNaturalValue.asInstanceOf[String]
        else throw new IllegalArgumentException("Unexpected param type = " + paramType)

      println("*** calling " + methodName + " with " + arg)
      method.invoke(filter, arg) // p.getInformation().cast(p.getValue()));
    }
    newParams
  }
}
