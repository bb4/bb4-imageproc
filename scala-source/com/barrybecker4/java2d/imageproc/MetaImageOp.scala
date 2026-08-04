/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import java.awt.image.BufferedImageOp
import java.lang.reflect.Method

import com.barrybecker4.optimization.parameter.types.Parameter
import org.slf4j.LoggerFactory

import scala.util.Random


/**
  * Contains an image operator and information about it (such as parameters).
  * @author Barry Becker
  */
object MetaImageOp {
  private val log = LoggerFactory.getLogger(classOf[MetaImageOp])

  /** Ensures that all randomness is repeatable. */
  private val RANDOM: Random = new Random(1)

  private def trace(msg: => String): Unit =
    if log.isTraceEnabled then log.trace(msg)

  private[imageproc] def setterNameFor(propertyName: String): String =
    "set" + propertyName.substring(0, 1).toUpperCase + propertyName.substring(1)

  private def newFilterInstance(opClass: Class[? <: BufferedImageOp]): BufferedImageOp =
    try opClass.getDeclaredConstructor().newInstance()
    catch {
      case ex: ReflectiveOperationException =>
        throw new IllegalArgumentException(s"Cannot construct $opClass (needs public no-arg constructor)", ex)
    }

  private[imageproc] def coerceArgument(param: Parameter, paramType: Class[?]): AnyRef =
    if paramType == classOf[Float] || paramType == java.lang.Float.TYPE then
      Float.box(param.getValue.toFloat)
    else if paramType == classOf[java.lang.Integer] || paramType == java.lang.Integer.TYPE then
      Integer.valueOf(param.getValue.toInt)
    else if paramType == classOf[java.lang.Double] || paramType == java.lang.Double.TYPE then
      java.lang.Double.valueOf(param.getValue)
    else if paramType == classOf[java.lang.Boolean] || paramType == java.lang.Boolean.TYPE then
      java.lang.Boolean.valueOf(param.getNaturalValue.asInstanceOf[Boolean])
    else if paramType == classOf[String] then
      param.getNaturalValue.asInstanceOf[String]
    else
      throw new IllegalArgumentException("Unexpected param type = " + paramType)

  private def invokeSetter(filter: BufferedImageOp, method: Method, arg: AnyRef): Unit =
    method.invoke(filter, arg)

  /** `Parameter.getType` may be boxed or primitive; JavaBean setters often use the other. */
  private def declaredSetter(clazz: Class[?], methodName: String, paramType: Class[?]): Method =
    try clazz.getDeclaredMethod(methodName, paramType)
    catch {
      case ex: NoSuchMethodException =>
        val alt = alternateBoxedOrPrimitive(paramType)
        try clazz.getDeclaredMethod(methodName, alt)
        catch {
          case ex2: NoSuchMethodException =>
            ex2.addSuppressed(ex)
            throw ex2
        }
    }

  private[imageproc] def alternateBoxedOrPrimitive(t: Class[?]): Class[?] =
    if t == java.lang.Integer.TYPE then classOf[java.lang.Integer]
    else if t == classOf[java.lang.Integer] then java.lang.Integer.TYPE
    else if t == java.lang.Double.TYPE then classOf[java.lang.Double]
    else if t == classOf[java.lang.Double] then java.lang.Double.TYPE
    else if t == java.lang.Float.TYPE then classOf[java.lang.Float]
    else if t == classOf[java.lang.Float] then java.lang.Float.TYPE
    else if t == java.lang.Boolean.TYPE then classOf[java.lang.Boolean]
    else if t == classOf[java.lang.Boolean] then java.lang.Boolean.TYPE
    else throw new NoSuchMethodException(s"No alternate for parameter type $t")
}

/**
  * Information (like parameters) about the image operator.
  *
  * For dynamic operators (`isDynamic == true`), [[getInstance]] and [[getRandomInstance]] return the
  * same [[java.awt.image.BufferedImageOp]] instance and apply parameter changes via reflection in place. Callers must not
  * use that instance from multiple threads concurrently.
  *
  * @param op the meta op
  * @param parameters list of base params based for creating concrete imageOps.
  * @param isDynamic if it can be changed
  */
class MetaImageOp(op: BufferedImageOp, val parameters: Seq[Parameter], isDynamic: Boolean = true) {

  /** last used list of params used to create recent imageOp. */
  private var lastUsedParameters: IndexedSeq[Parameter] = parameters.map(_.copy).toIndexedSeq

  def this(op: BufferedImageOp) = {
    this(op, Seq(), false)
  }

  /** @param opClass the operator class.
    * @param params all the parameters that need to be set on the op.
    */
  def this(opClass: Class[? <: BufferedImageOp], params: Seq[Parameter]) = {
    this(MetaImageOp.newFilterInstance(opClass), params, true)
  }

  /** @return a concrete filter operator instance. */
  def getInstance: BufferedImageOp = getRandomInstance(0)

  /** @param randomVariance number of standard deviations to use when randomizing params.
    * @return the same operator instance as [[op]] (mutated when `isDynamic`).
    */
  def getRandomInstance(randomVariance: Float): BufferedImageOp = {
    MetaImageOp.trace(s"getRandomInstance isDynamic=$isDynamic randomVariance=$randomVariance")
    if !isDynamic then op
    else {
      lastUsedParameters = tweakParameters(op, randomVariance)
      op
    }
  }

  /** @param param the parameter to update */
  def updateParameter(param: Parameter): Unit = {
    val idx = lastUsedParameters.indexWhere(_.name == param.name)
    if idx < 0 then
      throw new IllegalArgumentException(
        s"No parameter named '${param.name}' in ${lastUsedParameters.map(_.name).mkString(", ")}")
    lastUsedParameters = lastUsedParameters.updated(idx, param)
  }

  def getBaseParameters: Seq[Parameter] = parameters
  def getLastUsedParameters: Seq[Parameter] = lastUsedParameters

  def copy: MetaImageOp =
    if isDynamic then
      new MetaImageOp(op.getClass.asInstanceOf[Class[? <: BufferedImageOp]], parameters)
    else new MetaImageOp(op)

  private def tweakParameters(filter: BufferedImageOp, randomVariance: Float): IndexedSeq[Parameter] = {
    MetaImageOp.trace(s"tweakParameters op=${filter.getClass.getSimpleName} randomVariance=$randomVariance")
    lastUsedParameters.map { p =>
      val methodName = MetaImageOp.setterNameFor(p.name)
      MetaImageOp.trace(s"setter=$methodName paramType=${p.getType}")
      val method = MetaImageOp.declaredSetter(filter.getClass, methodName, p.getType)

      val param =
        if randomVariance > 0 then p.tweakValue(randomVariance, MetaImageOp.RANDOM)
        else p

      MetaImageOp.trace(s"tweaked value=$param (min=${param.minValue} max=${param.maxValue} v=${param.getValue})")

      val setterParamType = method.getParameterTypes.head
      val arg = MetaImageOp.coerceArgument(param, setterParamType)
      MetaImageOp.trace(s"invoke $methodName($arg)")
      MetaImageOp.invokeSetter(filter, method, arg)
      param
    }
  }
}
