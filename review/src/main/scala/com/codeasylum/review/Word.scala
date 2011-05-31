package com.codeasylum.review

class Word (val original: String) {

  override def hashCode: Int = original.hashCode

  def canEqual (other: Any): Boolean = other.isInstanceOf[Word]

  override def equals (other: Any): Boolean = other match {
    case word: Word => original == word.original
    case _ => false
  }
}