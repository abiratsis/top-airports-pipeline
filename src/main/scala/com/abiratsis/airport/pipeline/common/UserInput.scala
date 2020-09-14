package com.abiratsis.airport.pipeline.common

case class UserInput(mode: String, inputFile: String, destination: String, format: String, downloadData: Boolean)

object UserInput{

  def apply(cmd: CommandLineHandler): UserInput =
    new UserInput(
      cmd.mode.toOption.fold("b")(x => x),
      cmd.inputFile.toOption.fold("")(x => x),
      cmd.destination.toOption.fold("")(x => x),
      cmd.format.toOption.fold("parquet")(x => x),
      cmd.downloadData.toOption.fold(false)(x => x)
    )
}
