import wartremover.WartRemover.autoImport.{Wart, Warts}

object OwnWarts {
  lazy val all: Seq[wartremover.Wart] = Warts.allBut(
    Wart.Any,
    Wart.ArrayEquals,
    Wart.DefaultArguments,
    Wart.ImplicitConversion,
    Wart.Nothing,
    Wart.Overloading,
    Wart.NonUnitStatements,
    Wart.Product,
    Wart.Serializable
  )
}
