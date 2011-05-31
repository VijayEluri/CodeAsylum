package com.codeasylum.review

class Space (original: String) extends Word(original) {

  override def equals (other: Any): Boolean = other match {
    case space: Space => true;
    case _ => super.equals(other)
  }
}