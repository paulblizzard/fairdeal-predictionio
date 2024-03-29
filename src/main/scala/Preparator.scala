package com.fairdeal

import io.prediction.controller.PPreparator
import io.prediction.controller.Params

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.feature.{IDF, IDFModel, HashingTF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/** Define Preparator parameters. Recall that for our data
  * representation we are only required to input the n-gram window
  * components.
  */
case class PreparatorParams(
  nGram: Int,
  numFeatures: Int = 15000
) extends Params

/** define your Preparator class */
class Preparator(pp: PreparatorParams)
  extends PPreparator[TrainingData, PreparedData] {

  def prepare(sc: SparkContext, td: TrainingData): PreparedData = {

    val tfHasher = new TFHasher(pp.numFeatures, pp.nGram)

    // Convert trainingdata's observation text into TF vector
    // and then fit a IDF model
    val idf: IDFModel = new IDF().fit(td.data.map(e => tfHasher.hashTF(e.text)))

    val tfIdfModel = new TFIDFModel(
      hasher = tfHasher,
      idf = idf
    )

    // Transform RDD[Observation] to RDD[(Label, text)]
    val doc: RDD[(Double, String)] = td.data.map (obs => (obs.label, obs.text))

    // transform RDD[(Label, text)] to RDD[LabeledPoint]
    val transformedData: RDD[(LabeledPoint)] = tfIdfModel.transform(doc)

    // Finally extract category map, associating label to category.
    val categoryMap = td.data.map(obs => (obs.label, obs.category)).collectAsMap.toMap

    new PreparedData(
      tfIdf = tfIdfModel,
      transformedData = transformedData,
      categoryMap = categoryMap
    )
  }

}

class TFHasher(
  val numFeatures: Int,
  val nGram: Int
) extends Serializable {

  private val hasher = new HashingTF(numFeatures = numFeatures)

  /** Hashing function: Text -> term frequency vector. */
  def hashTF(text: String): Vector = {
    val newList : Array[String] = text.split(" ")
    .sliding(nGram)
    .map(_.mkString)
    .toArray

    hasher.transform(newList)
  }
}

class TFIDFModel(
  val hasher: TFHasher,
  val idf: IDFModel
) extends Serializable {
  
  /** trasform text to tf-idf vector. */
  def transform(text: String): Vector = {
    // Map(n-gram -> document tf)
    idf.transform(hasher.hashTF(text))
  }

  /** transform RDD of (label, text) to RDD of LabeledPoint */
  def transform(doc: RDD[(Double, String)]): RDD[LabeledPoint] = {
    doc.map{ case (label, text) => LabeledPoint(label, transform(text)) }
  }
}

class PreparedData(
  val tfIdf: TFIDFModel,
  val transformedData: RDD[LabeledPoint],
  val categoryMap: Map[Double, String]
) extends Serializable