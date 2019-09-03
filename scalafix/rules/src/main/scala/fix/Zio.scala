package fix

import scalafix.v1._
import scala.meta._

class Zio extends SemanticRule("Zio") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    Patch.replaceSymbols(
      "cats/effect/IO" -> "zio/Task"
    ) ++ doc.tree.collect {
      case pureMatcher(Term.Apply(Term.Select(_, s), _)) =>
        Patch.replaceTree(s, "succeed")
      case raiseErrorMatcher(Term.Apply(Term.Select(_, s), _)) =>
        Patch.replaceTree(s, "fail")
    } + Patch.removeImportee(Importee.Name(Name("cats/effect/IO")))
  }

  val pureMatcher = SymbolMatcher.normalized("cats/effect/IO#pure.")
  val raiseErrorMatcher = SymbolMatcher.normalized("cats/effect/IO#raiseError.")
}
