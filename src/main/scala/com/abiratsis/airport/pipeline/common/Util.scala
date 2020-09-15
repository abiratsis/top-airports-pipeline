package com.abiratsis.airport.pipeline.common

object String {
  def isNullOrEmpty(str: java.lang.String) : Boolean = {
    str == null || str.isEmpty
  }
}
