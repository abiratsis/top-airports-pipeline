package com.abiratsis.airport.pipeline.exceptions

class NullOrEmptyArgumentException(paramName: String)
  extends IllegalArgumentException(s"$paramName can't be null or empty.")